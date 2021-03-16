package services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import javax.sql.DataSource;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import repository.event.Event;
import repository.event.EventRepository;
import repository.note.Note;
import repository.note.NoteRepository;
import repository.organization.Organization;
import repository.organization.OrganizationRepository;

@Service
public class NoteService {
	
	@Autowired
	NoteRepository noteRepository;
	
	@Autowired
	OrganizationRepository orgRepository;
	
	@Autowired
	EventRepository eventRepository;
	
	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	
	NoteService (DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addNote(Note note, Long orgId, Long presenterId, Long eventId) {
		ZoneId central = ZoneId.of("America/Chicago");
		note.setCreateDate(LocalDateTime.now(central));
		note.setLastModifiedDate(LocalDateTime.now(central));
		
		if (orgId != null) {			 
			Optional<Organization> optionalOrg = orgRepository.findById(orgId);
			if (optionalOrg != null) {
				Organization org = optionalOrg.get();
				note.setOrg(org);
			}
		}
		if (eventId != null) {			
			Optional<Event> optionalEvent = eventRepository.findById(eventId);
			if (optionalEvent != null) {
				Event event = optionalEvent.get();
				note.setEvent(event);
			}
		}
		noteRepository.save(note);
		
		String sql = "UPDATE Note "
				   + "SET createdby = ?, lastmodifiedby = ? "
				   + "WHERE NoteID = ?";

		jdbcTemplate.update(sql, presenterId, presenterId, note.getNoteId());
		
	}
	
	public void editNote (Note note, Long noteId, Long presenterId) {
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (optionalNote != null) {
			Note originalNote = optionalNote.get();
	
			originalNote.setNoteEntry(note.getNoteEntry());
			ZoneId central = ZoneId.of("America/Chicago");
			originalNote.setLastModifiedDate(LocalDateTime.now(central));
			
			noteRepository.save(originalNote);
			
			String sql = "UPDATE Note "
					   + "SET lastmodifiedby = ? "
					   + "WHERE NoteID = ?";

			jdbcTemplate.update(sql, presenterId, noteId);
		}
	}

}
