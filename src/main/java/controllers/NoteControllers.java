package controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import repository.calllog.CallLog;
import repository.calllog.CallLogRepository;
import repository.event.Event;
import repository.event.EventRepository;
import repository.event.presenter.Presenter;
import repository.note.Note;
import repository.note.NoteRepository;
import repository.organization.Organization;
import repository.organization.OrganizationRepository;
import repository.organization.contact.Contact;
import repository.organization.contact.ContactRepository;
import services.NoteService;

@RestController
@RequestMapping("/api")
public class NoteControllers {

	@Autowired
	NoteRepository noteRepository;
	
	@Autowired
	OrganizationRepository orgRepository;
	
	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	CallLogRepository callLogRepository;

	@Autowired
	NoteService noteService;
	
	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	
	NoteControllers (DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@GetMapping("/all-my-callLogs-export")
	List<Note> getAllMyCallLogNotes(@AuthenticationPrincipal Presenter user){
		return noteRepository.findByCreatedByAndCallLogIsNotNull(user);
	}
	
	@GetMapping("/all-callLogs-export")
	List<Note> getAllCallLogNotes(){
		return noteRepository.findByCallLogIsNotNull();
	}
	
	@GetMapping("/callLogs-view-export")
	List<Note> getCurrentViewCallLogNotes(@RequestParam(value="cid", required=false) String [] callLogIds){
		List<CallLog> callLogsList = new ArrayList<CallLog>();
		if (callLogIds != null) {
			for (int i=0; i<callLogIds.length; i++) {
				Optional<CallLog> callLogOpt = callLogRepository.findById(Long.parseLong(callLogIds[i]));
				if (callLogOpt != null) {
					CallLog callLog = callLogOpt.get();
					callLogsList.add(callLog);
				}
			}
			return noteRepository.findByCallLogIn(callLogsList);
		}
		return null;
	}
	
	@GetMapping("/org/notes/{orgId}")
	List<Map<String, Object>> getOrgNotes(@PathVariable Long orgId) {
		String sql = "SELECT n.NoteID as noteId, n.noteentry as noteEntry, " +
					 "n.createddate as createDate, pc.name as createdByFirstName, pc.lastname as createdByLastName, " +
				     "n.lastmodifieddate as lastModifiedDate, pl.name as lastModifiedByFirstName, pl.lastname as lastModifiedByLastName " + 
				     "FROM Note as n " +
				     "JOIN Organization o ON n.OrgID = o.OrgID "
				     + "JOIN "
				     	+ "(SELECT presenterid, name, lastname FROM presenter) pc "
				     	+ "ON n.createdby = pc.presenterid "
				     + "JOIN "
				     	+ "(SELECT presenterid, name, lastname FROM presenter) pl "
				     	+ "ON n.lastmodifiedby = pl.presenterid "
				     + "WHERE o.OrgID = ? AND n.CalllogID IS NULL";
		return jdbcTemplate.queryForList(sql, orgId);	
//		return noteRepository.findByorg(orgId);  Gives null key JSON Exception
	}
	
	
	@GetMapping("/org/callLogNotes/{orgId}")
	List<Map<String, Object>> getOrgCallLogNotes(@PathVariable Long orgId) {
		String sql = "SELECT n.CalllogID as callId, n.NoteID as noteId, n.noteentry as noteEntry, " +
					 "n.createddate as createDate, pc.name as createdByFirstName, pc.lastname as createdByLastName, " +
				     "n.lastmodifieddate as lastModifiedDate, pl.name as lastModifiedByFirstName, pl.lastname as lastModifiedByLastName " + 
				     "FROM Note as n " +
				     "JOIN Organization o ON n.OrgID = o.OrgID "
				     + "JOIN "
				     	+ "(SELECT presenterid, name, lastname FROM presenter) pc "
				     	+ "ON n.createdby = pc.presenterid "
				     + "JOIN "
				     	+ "(SELECT presenterid, name, lastname FROM presenter) pl "
				     	+ "ON n.lastmodifiedby = pl.presenterid "
				     + "WHERE o.OrgID = ? AND n.CalllogID IS NOT NULL";
		return jdbcTemplate.queryForList(sql, orgId);	
	}
	
	@GetMapping("/event/notes/{eventId}")
	List<Note> getEventNotes(@PathVariable Long eventId){
		Optional<Event> optionalEvent = eventRepository.findById(eventId);
		if (optionalEvent != null) {
			Event event = optionalEvent.get();
			return noteRepository.findByEvent(event);
		}
		return null;
	}
	
	@GetMapping("/contact/notes/{contactId}")
	List<Note> getContactNotes(@PathVariable Long contactId){
		Optional<Contact> optionalContact = contactRepository.findById(contactId);
		if (optionalContact != null) {
			Contact contact = optionalContact.get();
			return noteRepository.findByContact(contact);
		}
		return null;
	}
	
	@PostMapping("/org/newNote/{orgId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createOrgNote (@RequestBody Note note, @PathVariable Long orgId, 
			@AuthenticationPrincipal Presenter user){
		Long presenterId = null;
		if (user != null) {
			presenterId = user.getPresenterId();
			noteService.addNote(note, orgId, presenterId, null, null);
			// Build the location URI of the new item
			URI location = ServletUriComponentsBuilder
					.fromCurrentRequestUri()
					.path("/{noteId}")
					.buildAndExpand(note.getNoteId())
					.toUri(); 	
			// Explicitly create a 201 Created response
			return ResponseEntity.created(location).build();				
		}
		else {
			return null;
		}
	}
	
	@PostMapping("/event/newNote/{eventId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createEventNote (@RequestBody Note note, @PathVariable Long eventId, 
			@AuthenticationPrincipal Presenter user){
		Long presenterId = null;
		if (user != null) {
			presenterId = user.getPresenterId();
			noteService.addNote(note, null, presenterId, eventId, null);
			// Build the location URI of the new item
			URI location = ServletUriComponentsBuilder
					.fromCurrentRequestUri()
					.path("/{noteId}")
					.buildAndExpand(note.getNoteId())
					.toUri(); 	
			// Explicitly create a 201 Created response
			return ResponseEntity.created(location).build();				
		}
		else {
			return null;
		}
	}
	
	@PostMapping("/contact/newNote/{contactId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createContactNote (@RequestBody Note note, @PathVariable Long contactId, 
			@AuthenticationPrincipal Presenter user){
		Long presenterId = null;
		if (user != null) {
			presenterId = user.getPresenterId();
			noteService.addNote(note, null, presenterId, null, contactId);
			// Build the location URI of the new item
			URI location = ServletUriComponentsBuilder
					.fromCurrentRequestUri()
					.path("/{noteId}")
					.buildAndExpand(note.getNoteId())
					.toUri(); 	
			// Explicitly create a 201 Created response
			System.out.println("######### Created new Note Id: " + note.getNoteId());
			return ResponseEntity.created(location).build();				
		}
		else {
			return null;
		}
	}


	//For Organization, Contact and Event notes
	@PutMapping("/editNote/{noteId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void updateNote (@RequestBody Note note, @PathVariable Long noteId, 
			@AuthenticationPrincipal Presenter user) {		    
		Long presenterId = null;
		if (user != null) {
			presenterId = user.getPresenterId();
			noteService.editNote(note, noteId, presenterId);
		}
	}
	
	
	@DeleteMapping("/deleteNote/{noteId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void deleteNote(@PathVariable Long noteId, 
			@AuthenticationPrincipal Presenter user) {
		if (user != null && noteId != null) {			
			String sql = "DELETE from Note "
					+ "WHERE NoteID = ?";
			jdbcTemplate.update(sql, noteId);
		}
	}
}
