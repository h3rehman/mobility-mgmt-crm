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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	NoteService noteService;
	
	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	
	NoteControllers (DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@GetMapping("/org/notes/{orgId}")
	List<Map<String, Object>> getOrgNotes(@PathVariable Long orgId) {
		String sql = "SELECT NoteID as noteId, noteentry as noteEntry " + 
				     "FROM Note JOIN Organization " + 
				     "ON Note.OrgID = Organization.OrgID " +
				     "WHERE Organization.OrgID = ? ";
		return jdbcTemplate.queryForList(sql, orgId);	
//		return noteRepository.findByorg(orgId);  Gives null key JSON Exception
	}
	
	@PostMapping("/org/newNote/{orgId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createOrgNote (@RequestBody Note note, @PathVariable Long orgId, 
			@AuthenticationPrincipal Presenter user){
		Long presenterId = null;
		if (user != null) {
			presenterId = user.getPresenterId();
			System.out.println("### Presenter Id: " + presenterId);
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
}
