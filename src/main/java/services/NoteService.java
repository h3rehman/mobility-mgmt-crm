package services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	public void addNote(Note note, Long orgId, Long presenterId, Long eventId) {
		System.out.println("Finding OrgId: " + orgId);
		ZoneId central = ZoneId.of("America/Chicago");
		note.setCreateDate(LocalDateTime.now(central));
		note.setLastModifiedDate(LocalDateTime.now(central));
		System.out.println("Current Time: " + LocalDateTime.now(central));
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
	}
	
	public void editNote (Note note, Long noteId, Long presenterId) {
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (optionalNote != null) {
			Note originalNote = optionalNote.get();
	
			originalNote.setNoteEntry(note.getNoteEntry());
			ZoneId central = ZoneId.of("America/Chicago");
			originalNote.setLastModifiedDate(LocalDateTime.now(central));
			
			noteRepository.save(originalNote);
		}
	}

}
