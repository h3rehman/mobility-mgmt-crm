package services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import repository.calllog.CallLog;
import repository.calllog.CallLogRepository;
import repository.event.Event;
import repository.event.presenter.Presenter;
import repository.note.Note;
import repository.note.NoteRepository;
import repository.organization.Organization;
import repository.organization.OrganizationRepository;
import repository.organization.contact.Contact;
import repository.organization.contact.ContactRepository;
import repository.status.Status;
import repository.status.StatusRepository;

@Service
public class CallLogService {
	
	@Autowired
	CallLogRepository callLogRepository;
	
	@Autowired
	OrganizationRepository orgRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	NoteRepository noteRepository;
	
	@Autowired
	StatusRepository statusRepository;
	
	public void addChangeLog(Note note, Presenter user, Long orgId, Long contactId, 
		Long lastStatusId, String requestType) {
		
		CallLog callLog = null;
		if (requestType == "PUT") {
			Optional<CallLog> optionalCallLog = callLogRepository.findById(note.getCallLog().getCallId());
			if (optionalCallLog != null) {				
				callLog = optionalCallLog.get();
			}
		}
		else {
			callLog = note.getCallLog();
		}
			ZoneId central = ZoneId.of("America/Chicago");
			LocalDateTime currentTime = LocalDateTime.now(central);
			if (lastStatusId != -1) {
				Optional<Status> optionalStatus = statusRepository.findById(lastStatusId);
				if (optionalStatus != null) {
					Status lastStatus = optionalStatus.get();
					
					if (requestType == "POST") {					
						callLog.setCreateDate(currentTime);
						callLog.setCreatedBy(user);
					}
					callLog.setLastModifiedDate(currentTime);
					callLog.setLastModifiedBy(user);
					callLog.setStatus(lastStatus);
				}
			}
			
			if (requestType == "POST") {					
				note.setCreateDate(currentTime);
				note.setCreatedBy(user);
			}
			note.setLastModifiedDate(currentTime);
			note.setLastModifiedBy(user);
			
			if (orgId != -1) {			 
				Optional<Organization> optionalOrg = orgRepository.findById(orgId);
				if (optionalOrg != null) {
					Organization org = optionalOrg.get();
					if (lastStatusId != -1) {
						Optional<Status> optionalStatus = statusRepository.findById(lastStatusId);
						if (optionalStatus != null) {
							Status lastStatus = optionalStatus.get();
							org.setLastStatus(lastStatus);
							org.setLastContact(currentTime);
							orgRepository.save(org);
						}
					}
					callLog.setOrg(org);
					note.setOrg(org);
				}
			}
			if (contactId != -1) {			
				Optional<Contact> optionalContact = contactRepository.findById(contactId);
				if (optionalContact != null) {
					Contact contact = optionalContact.get();
					callLog.setContact(contact);
				}
			}
			callLogRepository.save(callLog);	
			//Save Note here.
			note.setCallLog(callLog);
			noteRepository.save(note);
	}
}
