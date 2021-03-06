package controllers;

import java.net.URI;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import repository.event.Event;
import repository.event.EventRepository;
import repository.event.presenter.Presenter;
import repository.note.Note;
import repository.note.NoteRepository;
import repository.organization.Organization;
import repository.organization.OrganizationRepository;
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
	NoteService noteService;
	
	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	
	NoteControllers (DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
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
	
	@PostMapping("/org/newNote/{orgId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createOrgNote (@RequestBody Note note, @PathVariable Long orgId, 
			@AuthenticationPrincipal Presenter user){
		Long presenterId = null;
		if (user != null) {
			presenterId = user.getPresenterId();
			noteService.addNote(note, orgId, presenterId, null);
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
			noteService.addNote(note, null, presenterId, eventId);
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

	//For both Organization and Event notes
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
