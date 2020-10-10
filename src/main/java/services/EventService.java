package services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import repository.event.Event;
import repository.event.EventRepository;
import repository.event.Eventtype;
import repository.event.EventtypeRepository;
import repository.event.presenter.EventPresenterRepository;
import repository.event.presenter.Eventpresenter;
import repository.event.presenter.Presenter;
import repository.event.presenter.PresenterRepository;
import repository.organization.EventOrganization;
import repository.organization.EventOrganizationRepository;
import repository.organization.Organization;
import repository.organization.OrganizationRepository;

@Service
public class EventService {

	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	PresenterRepository presenterRepository;
	
	@Autowired
	EventtypeRepository eventtypeRepository;
	
	@Autowired
	OrganizationRepository orgRepository;
	
	@Autowired
	EventOrganizationRepository eventOrgRepository;
	
	@Autowired
	EventPresenterRepository eventPresenterRepository;
	
	public List<Event> getAllEvents(){
		return eventRepository.findAll();
	}
	
	public List<Event> upcomingAppointments(String strDateTime){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime dateTime = LocalDateTime.parse(strDateTime, formatter);
		return eventRepository.findByDate(dateTime);
	}
	
	public List<Event> myUpcomingAppointments(Long long1) throws ClassNotFoundException{
		
		Presenter presenter = presenterById(long1);
		
		List<Event> myEvents = new ArrayList<>();
		for (Eventpresenter ep : presenter.eventPresenters) {
			myEvents.add(ep.event);
		}
		return myEvents;
	}
	
	public Presenter presenterById(Long long1) throws ClassNotFoundException {
		Optional<Presenter> optionalPresenter = presenterRepository.findById(long1);
		return optionalPresenter.orElseThrow(() -> new ClassNotFoundException("There is no Presenter exist with the id: " + long1));
	}

	public Event getEventById(Long id) throws ClassNotFoundException {
		Optional<Event> optEvent = eventRepository.findById(id);
		return optEvent.orElseThrow(() -> new ClassNotFoundException("There is no Event with the id: " + id));
	}

	public void addEvent(Event eve, String eventTypeDesc, String orgId, Long presenterId, boolean joinEve) {
		Eventtype eventType = eventtypeRepository.findByeventTypeDesc(eventTypeDesc);
		System.out.println("###################################");
		System.out.println("Event Desc is: " + eventType.getEventTypeDesc());
		System.out.println("###################################");
		eve.setEventType(eventType);
		eventRepository.save(eve);
		
		//Associate Org:
		Long oId = Long.parseLong(orgId);
		if (oId > 0) {
			Optional<Organization> optionalOrg = orgRepository.findById(oId);
			if (optionalOrg != null) {
				Organization org = optionalOrg.get();
				EventOrganization eveOrg = new EventOrganization();
				eveOrg.setEvent(eve);
				eveOrg.setOrganization(org);
				eventOrgRepository.save(eveOrg);
				System.out.println("##### NEW EVENT CREATED, Event ID: " + eve.getEventId() + " " + "associated Org Id: " + org.getOrgId());
			}
		}
		
		//Join the event
		if (joinEve != false && presenterId != null) {
			joinEvent(eve.getEventId(), presenterId);
		}
		
		System.out.println("### New Event created with ID: " + eve.getEventId());
	}

	public void updateEvent(Event eve, String eventTypeDesc, String orgId) {
		Eventtype eventType = eventtypeRepository.findByeventTypeDesc(eventTypeDesc);
		Optional<Event> optionalEvent = eventRepository.findById(eve.getEventId());
		
		Long oId = Long.parseLong(orgId);
		
		if (optionalEvent != null) {
			Event eveOriginal = optionalEvent.get();
			eveOriginal.setEventType(eventType);
			eveOriginal.setAddress(eve.getAddress());
			eveOriginal.setAudienceCount(eve.getAudienceCount());
			eveOriginal.setCity(eve.getCity());
			eveOriginal.setStartDateTime(eve.getStartDateTime());
			eveOriginal.setEndDateTime(eve.getEndDateTime());
			eveOriginal.setEventName(eve.getEventName());
			eveOriginal.setLocation(eve.getLocation());
			eveOriginal.setRtaStaffCount(eve.getRtaStaffCount());
			eveOriginal.setState(eve.getState());
			eveOriginal.setZip(eve.getZip());
			eventRepository.save(eveOriginal);
			if (oId > 0) {
				Optional<Organization> optionalOrg = orgRepository.findById(oId);
				if(optionalOrg != null) {
					HashMap<Long, String> associatedOrgs = eveOriginal.getOrgNames();
					if (!associatedOrgs.containsKey(oId)) {  //only add if the Organization does not exist. 
						Organization org = optionalOrg.get();
						EventOrganization eveOrg = new EventOrganization();
						eveOrg.setEvent(eveOriginal);
						eveOrg.setOrganization(org);
						eventOrgRepository.save(eveOrg);					
					}
				}
			}
		}
		else {
			System.out.println("Event is null");
		}
		
	}

	public void joinEvent(Long eventId, Long presenterId) {
		Optional<Event> optionalEvent = eventRepository.findById(eventId);
		Optional<Presenter> optionalPresenter = presenterRepository.findById(presenterId);
		
		if (optionalEvent != null && optionalPresenter != null) {
			Event event = optionalEvent.get();
			Presenter presenter = optionalPresenter.get();
			Eventpresenter evePresenter = new Eventpresenter();
			evePresenter.setEvent(event);
			evePresenter.setPresenter(presenter);
			eventPresenterRepository.save(evePresenter);
		}
		
		else {
			System.out.println("Either Event or Presenter is null...");
		}		
	}
	
}