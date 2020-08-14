package repository.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import repository.event.presenter.Eventpresenter;
import repository.event.presenter.Presenter;
import repository.event.presenter.PresenterRepository;

@Service
public class EventService {

	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	PresenterRepository presenterRepository;
	
	@Autowired
	EventtypeRepository eventtypeRepository;
	
	public List<Event> getAllEvents(){
		return eventRepository.findAll();
	}
	
	public List<Event> upcomingAppointments(String strDateTime){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime dateTime = LocalDateTime.parse(strDateTime, formatter);
		return eventRepository.findByDate(dateTime);
	}
	
	public List<Event> myUpcomingAppointments(int id) throws ClassNotFoundException{
		
		Presenter presenter = presenterById(id);
		
		List<Event> myEvents = new ArrayList<>();
		for (Eventpresenter ep : presenter.eventPresenters) {
			myEvents.add(ep.event);
		}
		return myEvents;
	}
	
	public Presenter presenterById(int id) throws ClassNotFoundException {
		Optional<Presenter> optionalPresenter = presenterRepository.findById((long) id);
		return optionalPresenter.orElseThrow(() -> new ClassNotFoundException("There is no Organization exist with the id: " + id));
	}

	public Event getEventById(Long id) throws ClassNotFoundException {
		Optional<Event> optEvent = eventRepository.findById(id);
		return optEvent.orElseThrow(() -> new ClassNotFoundException("There is no Event with the id: " + id));
	}

	public void addEvent(Event eve, String eventTypeDesc) {
		Eventtype eventType = eventtypeRepository.findByeventTypeDesc(eventTypeDesc);
		System.out.println("###################################");
		System.out.println("Event Desc is: " + eventType.getEventTypeDesc());
		System.out.println("###################################");
		eve.setEventType(eventType);
		eventRepository.save(eve);
		System.out.println("### New Event created with ID: " + eve.getEventId());
	}

	public void updateEvent(Event eve, String eventTypeDesc) {
		Eventtype eventType = eventtypeRepository.findByeventTypeDesc(eventTypeDesc);
		Optional<Event> optionalEvent = eventRepository.findById(eve.getEventId());
		
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
		}
		else {
			System.out.println("Event is null");
		}
		
	}
	
}
