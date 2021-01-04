package controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import repository.calllog.CallLog;
import repository.calllog.CallLogRepository;
import repository.event.presenter.Presenter;
import repository.note.Note;
import repository.note.NoteRepository;
import services.CallLogService;

@RestController
@RequestMapping("/api")
public class CallLogControllers {

	@Autowired
	CallLogRepository callLogRepository;
	
	@Autowired
	NoteRepository noteRepository;
	
	@Autowired
	CallLogService callLogService;
	
	@GetMapping("/callLogs")
	List<CallLog> getCallLogs(){
		return callLogRepository.findAll();
	}
	
	@GetMapping("/myCallLogs")
	List<CallLog> myCallLogs(@AuthenticationPrincipal Presenter presenter){					
		return callLogRepository.findBycreatedBy(presenter);
	}
	
	@GetMapping("/callLog-detail/{callId}")
	Note getCallLog(@PathVariable Long callId) {
		Optional<CallLog> optionalCallLog = callLogRepository.findById(callId);
		if (optionalCallLog != null) {
			CallLog callLog = optionalCallLog.get();
			Note note = noteRepository.findBycallLog(callLog);
			if (note != null) {
				return note;
			}
			System.out.println("Null.......");
			return null;
		}
		else {
			return null;
		}
	}
	
	@PostMapping("/callLogChange/{orgId}/{contactId}/{lastStatusId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createOrgNote (@RequestBody Note note, 
		@PathVariable Long orgId, @PathVariable Long contactId, 
		@PathVariable Long lastStatusId, @AuthenticationPrincipal Presenter user){	
		if (user != null) {
			final String requestType = "POST";
			CallLog callLog = note.getCallLog();
			callLogService.addChangeLog(note, user, orgId, contactId, 
					lastStatusId, requestType);
			// Build the location URI of the new item
			URI location = ServletUriComponentsBuilder
					.fromCurrentRequestUri()
					.path("/{callId}")
					.buildAndExpand(callLog.getCallId())
					.toUri(); 	
			// Explicitly create a 201 Created response
			return ResponseEntity.created(location).build();				
		}
		else {
			return null;
		}
	}
	
	@PutMapping("/callLogChange/{orgId}/{contactId}/{lastStatusId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void updateNote (@RequestBody Note note, 
		@PathVariable Long orgId, @PathVariable Long contactId, 
		@PathVariable Long lastStatusId, @AuthenticationPrincipal Presenter user) {		    

		if (user != null) {
			final String requestType = "PUT";
			callLogService.addChangeLog(note, user, orgId, contactId, 
					lastStatusId, requestType);
		}
	}
}
