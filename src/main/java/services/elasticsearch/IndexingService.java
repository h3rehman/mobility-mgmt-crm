package services.elasticsearch;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import repository.elasticsearch.ContactES;
import repository.elasticsearch.ContactESRepository;
import repository.elasticsearch.EventES;
import repository.elasticsearch.EventESRepository;
import repository.elasticsearch.OrganizationES;
import repository.elasticsearch.OrganizationESRepository;
import repository.event.Event;
import repository.event.EventRepository;
import repository.organization.Organization;
import repository.organization.OrganizationRepository;
import repository.organization.contact.Contact;
import repository.organization.contact.ContactRepository;

@Service
public class IndexingService {

	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	EventESRepository eventEsRepository;
	
	@Autowired
	OrganizationRepository orgRepository;
	
	@Autowired
	OrganizationESRepository orgEsRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	ContactESRepository contactEsRepository;
	
	@PostConstruct
	void elasticSearchIndexer() {
		
		//Indexing Events
		List<Event> events = eventRepository.findAll();
		
		for (Event event : events) {
			EventES eventEs = new EventES();
			eventEs.setId(event.getEventId().toString());
			eventEs.setEventName(event.getEventName());
			eventEs.setLocation(event.getLocation());
			eventEs.setAddress(event.getAddress());
		
			eventEsRepository.save(eventEs);
		}
		System.out.println("########## EventES Indexing completed. #############");
		
		//Indexing Orgs
		List<Organization> orgs = orgRepository.findAll();
		
		for (Organization org : orgs) {
			OrganizationES orgEs = new OrganizationES();
			orgEs.setId(org.getOrgId().toString());
			orgEs.setOrgname(org.getOrgname());
			orgEs.setAddress(org.getAddress());
			orgEs.setEmail(org.getEmail());
			orgEs.setPhone(org.getPhone());
			orgEs.setAltPhone(org.getAltphone());
			
			orgEsRepository.save(orgEs);
		}
		System.out.println("########## OrganizationES Indexing completed. #############");
		
		//Indexing Contacts
		List<Contact> contacts = contactRepository.findAll();
		
		for (Contact contact : contacts) {
			ContactES contactEs = new ContactES();
			contactEs.setId(contact.getContactId().toString());
			contactEs.setFirstName(contact.getFirstName());
			contactEs.setLastName(contact.getLastName());
			contactEs.setEmail(contact.getEmail());
			contactEs.setTitle(contact.getTitle());
			contactEs.setPhone(contact.getPhone());
			contactEs.setAltPhone(contact.getAltPhone());
			
			contactEsRepository.save(contactEs);
		}
		System.out.println("########## ContactES Indexing completed. #############");
	
	}
	
	public void createUpdateAndDeleteIndexElements(String requestType, Event event, Organization org, Contact contact) {
		if (requestType.equalsIgnoreCase("POST")) {
			if (event != null) {
				EventES eventEs = new EventES();
				eventEs.setId(event.getEventId().toString());
				eventEs.setEventName(event.getEventName());
				eventEs.setLocation(event.getLocation());
				eventEs.setAddress(event.getAddress());
			
				eventEsRepository.save(eventEs);
			}
			else if (org != null) {
				OrganizationES orgEs = new OrganizationES();
				orgEs.setId(org.getOrgId().toString());
				orgEs.setOrgname(org.getOrgname());
				orgEs.setAddress(org.getAddress());
				orgEs.setEmail(org.getEmail());
				orgEs.setPhone(org.getPhone());
				orgEs.setAltPhone(org.getAltphone());
				
				orgEsRepository.save(orgEs);
			}
			else if (contact != null) {
				ContactES contactEs = new ContactES();
				contactEs.setId(contact.getContactId().toString());
				contactEs.setFirstName(contact.getFirstName());
				contactEs.setLastName(contact.getLastName());
				contactEs.setEmail(contact.getEmail());
				contactEs.setTitle(contact.getTitle());
				contactEs.setPhone(contact.getPhone());
				contactEs.setAltPhone(contact.getAltPhone());
				
				contactEsRepository.save(contactEs);
			}
		}
		else if (requestType.equalsIgnoreCase("PUT")) {
			if (event != null) {
				Optional<EventES> optEventEs = eventEsRepository.findById(event.getEventId().toString());
				if (optEventEs != null) {
					EventES eventEs = optEventEs.get();
					
					eventEs.setEventName(event.getEventName());
					eventEs.setLocation(event.getLocation());
					eventEs.setAddress(event.getAddress());
				
					eventEsRepository.save(eventEs);
				}
			}
			else if (org != null) {
				Optional<OrganizationES> optEsOrg = orgEsRepository.findById(org.getOrgId().toString());
				if (optEsOrg != null) {
					OrganizationES orgEs = optEsOrg.get();
					
					orgEs.setOrgname(org.getOrgname());
					orgEs.setAddress(org.getAddress());
					orgEs.setEmail(org.getEmail());
					orgEs.setPhone(org.getPhone());
					orgEs.setAltPhone(org.getAltphone());
					
					orgEsRepository.save(orgEs);
				}
			}
			else if (contact != null) {
				Optional<ContactES> optContactEs = contactEsRepository.findById(contact.getContactId().toString());
				if (optContactEs != null) {
					ContactES contactEs = optContactEs.get();
					
					contactEs.setFirstName(contact.getFirstName());
					contactEs.setLastName(contact.getLastName());
					contactEs.setEmail(contact.getEmail());
					contactEs.setTitle(contact.getTitle());
					contactEs.setPhone(contact.getPhone());
					contactEs.setAltPhone(contact.getAltPhone());
					
					contactEsRepository.save(contactEs);	
				}
			}

		}
		else if (requestType.equalsIgnoreCase("DELETE")) {
			if (event != null) {
				Optional<EventES> optEventEs = eventEsRepository.findById(event.getEventId().toString());
				if (optEventEs != null) {
					EventES eventEs = optEventEs.get();
					eventEsRepository.delete(eventEs);
				}
			}
			else if (org != null) {
				Optional<OrganizationES> optOrgEs = orgEsRepository.findById(org.getOrgId().toString());
				if (optOrgEs != null) {
					OrganizationES orgEs = optOrgEs.get();
					orgEsRepository.delete(orgEs);
				}
			}
			else if (contact != null) {
				Optional<ContactES> optContactEs = contactEsRepository.findById(contact.getContactId().toString());
				if (optContactEs != null) {
					ContactES contactEs = optContactEs.get();
					contactEsRepository.delete(contactEs);
				}
			}
		}
		
	}
	
}
