package controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import repository.event.presenter.Presenter;
import repository.note.Note;
import repository.note.NoteRepository;
import repository.status.Status;
import repository.status.StatusRepository;
import services.CallLogService;

@RestController
@RequestMapping("/api")
public class CallLogControllers {

	@Autowired
	CallLogRepository callLogRepository;
	
	@Autowired
	NoteRepository noteRepository;
	
	@Autowired
	StatusRepository statusRepository;
	
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
	
	@GetMapping("/call-logs-sorted-default")
	public Page<CallLog> getMyDefaultCallLogs () {
		final int pageNumber = 0;
		final int pageElements = 10;
		Pageable pageable = PageRequest.of(pageNumber, pageElements, Sort.by("createDate").descending());
		Page<CallLog> callLogs = callLogRepository.findAll(pageable);
		return callLogs;
	}
	
	@GetMapping("/call-logs-filtered-sorted/{pageNumber}/{pageElements}/{fieldName}/{sortOrder}/{from}/{to}")
	public Page<CallLog> getCustomCallLogs (@PathVariable Integer pageNumber, @PathVariable Integer pageElements, 
			@PathVariable String fieldName, @PathVariable String sortOrder, @PathVariable String from, @PathVariable String to, 
			@AuthenticationPrincipal Presenter user, @RequestParam(value="status", required=false) String [] callLogStatuses) {
		
		Pageable pageable = null;
		Page<CallLog> callLogs = null;
		
		if (!fieldName.equalsIgnoreCase("null")) {
			if (sortOrder.equalsIgnoreCase("asce") || sortOrder.equalsIgnoreCase("null") ) {
				pageable = PageRequest.of(pageNumber, pageElements, Sort.by(fieldName).ascending());
			}
			else {
				pageable = PageRequest.of(pageNumber, pageElements, Sort.by(fieldName).descending());
			}
		}
		else { //default sort 
			pageable = PageRequest.of(pageNumber, pageElements, Sort.by("createDate").descending());
		}
		
		LocalDateTime fromLMDDate = null;
		LocalDateTime toLMDDate = null;

		if (!from.equalsIgnoreCase("null") && !to.equalsIgnoreCase("null")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			fromLMDDate = LocalDateTime.parse(from, formatter);
			toLMDDate = LocalDateTime.parse(to, formatter);
		}
		
		List<Status> statuses = new ArrayList<Status>();
		if (callLogStatuses != null) {
			for (int i=0; i<callLogStatuses.length; i++) {
				Status status = statusRepository.findBystatusDesc(callLogStatuses[i]);
				if (status != null) {
					statuses.add(status);
				}
			}
		}
		
		if (statuses.size() > 0 && fromLMDDate != null && toLMDDate != null) {
			callLogs = callLogRepository.findByCreatedByAndStatusInAndLastModifiedDateBetween(user, statuses, fromLMDDate, toLMDDate, pageable);
		}
		
		else if (statuses.size() > 0 && fromLMDDate == null && toLMDDate == null) {
			callLogs = callLogRepository.findByCreatedByAndStatusIn(user, statuses, pageable);
		}
		
		else if (statuses.size() < 1 && fromLMDDate != null && toLMDDate != null) {
			callLogs = callLogRepository.findByCreatedByAndLastModifiedDateBetween(user, fromLMDDate, toLMDDate, pageable);
		}
		else {
			callLogs = callLogRepository.findAll(pageable);
		}
		
		return callLogs;
	}
	
}
