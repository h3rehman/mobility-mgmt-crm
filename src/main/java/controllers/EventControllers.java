package controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import repository.event.Event;
import repository.event.EventRepository;
import repository.event.Eventtype;
import repository.event.EventtypeRepository;
import repository.event.presenter.Presenter;
import repository.organization.Organization;
import repository.status.Status;
import repository.status.StatusRepository;
import services.EventService;


@RestController
@RequestMapping("/api")
public class EventControllers {
	
	DataSource dataSource;
	
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	EventService eventService;
	
	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	EventtypeRepository eventTypeRepository;
	
	@Autowired
	StatusRepository statusRepository;

	EventControllers (DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@GetMapping("/events")
	List<Event> getAllEvents(){
		return eventService.getAllEvents();
	}
	
	@GetMapping("/events/{id}")
	public Event getEventDetail(@PathVariable Long id) throws ClassNotFoundException{
		return eventService.getEventById(id);
	}
	
	@GetMapping("/checkPresenter/{eventId}")
	public boolean checkForPresenterInEvent(@PathVariable Long eventId, @AuthenticationPrincipal Presenter user) {
		if (user != null) {
			Long presenterId = user.getPresenterId();
			String sql = "SELECT count(*) "
					    +"FROM eventpresenter "
					    + "WHERE eventid = ? "
					    + "AND presenterid = ?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, eventId, presenterId);
			return count > 0;
		}
		else {
			return false;
		}
	}
	
	@DeleteMapping("/removePresenter/{eventId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void removePresenterFromEvent(@PathVariable Long eventId, @AuthenticationPrincipal Presenter user) {
		if (user != null) {
			Long presenterId = user.getPresenterId();
			String sql = "DELETE from eventpresenter "
					   + "WHERE eventid = ? "
					   + "AND presenterid = ?";
			jdbcTemplate.update(sql, eventId, presenterId);
		}
	}
	
	@GetMapping("/upcoming-events")
	List<Event> getFutureEvents(@RequestParam String dateTime){
		return eventService.upcomingAppointments(dateTime);
	}

	@GetMapping("/appointments")
	List<Event> getMyEvents(@AuthenticationPrincipal Presenter user) throws ClassNotFoundException{
		return eventService.myUpcomingAppointments(user.getPresenterId());
	}
	
	@PostMapping("/event/{eventType}/{orgId}/{joinEve}/{lastStatus}")
	@ResponseStatus(HttpStatus.CREATED) //201
	public ResponseEntity<Void> createEvent(@RequestBody Event eve, @PathVariable String eventType,
	@PathVariable String orgId, @PathVariable boolean joinEve, @PathVariable String lastStatus,
	@AuthenticationPrincipal Presenter user, @RequestParam(value="audType", required=false) Long [] audTypes){ 
		Long presenterId = null;
		if (user != null) {
			presenterId = user.getPresenterId();
		}
		
		eventService.addEvent(eve, eventType, orgId, presenterId, joinEve, audTypes, lastStatus);
 		
		// Build the location URI of the new item
		 URI location = ServletUriComponentsBuilder
		 .fromCurrentRequestUri()
		 .path("/{eventId}")
		 .buildAndExpand(eve.getEventId())
		 .toUri(); 

		// Explicitly create a 201 Created response
		 return ResponseEntity.created(location).build();	
	}
	
	@PutMapping("/event/{eventType}/{orgId}/{joinEve}/{lastStatus}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void updateEvent(@RequestBody Event eve, @PathVariable String lastStatus,
		@PathVariable String eventType, @PathVariable String orgId, 
		@RequestParam(value="audType", required=false) Long [] audTypes) {
		eventService.updateEvent(eve, eventType, orgId, audTypes, lastStatus);
	}
	
	@PutMapping("/joinevent/{eventId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void joinEvent(@PathVariable Long eventId, @AuthenticationPrincipal Presenter user) {
		if (user == null ) {
			System.out.println("No user associated with the Event...");
		}
		else {
			System.out.println("#### Presenter ID: " + user.getPresenterId());
			System.out.println("#### Presenter Name: " + user.getName());
			Long presenterId = user.getPresenterId();
			eventService.joinEvent(eventId, presenterId);
			System.out.println(user.getName() + " joined Event Id: " + eventId);
		}
	}
	
	@GetMapping("/events-sorted-default/{pageNumber}/{pageElements}")
	public Page<Event> getListedEvents(@PathVariable Integer pageNumber, 
			@PathVariable Integer pageElements){
		Pageable pagedEvent = PageRequest.of(pageNumber, pageElements, Sort.by("startDateTime").descending());
		Page<Event> pagedEvents = eventRepository.findAll(pagedEvent);
		return pagedEvents;
	}
	
	
	@GetMapping("/events-filtered-sorted/{pageNumber}/{pageElements}/{fieldName}/{sortOrder}/{from}/{to}")
	public Page<Event> getFilteredSortedEvents(@PathVariable Integer pageNumber, 
			@PathVariable Integer pageElements, @PathVariable String fieldName, 
			@PathVariable String sortOrder, @PathVariable String from, @PathVariable String to, 
			@RequestParam(value="eveType", required=false) String [] eveTypes,
			@RequestParam(value="eveStatus", required=false) String[] eveStatuses){
		
		Page<Event> events = null;
		Pageable pageable = null;
		if (!fieldName.equalsIgnoreCase("null")) {
			if (sortOrder.equalsIgnoreCase("asce") || sortOrder.equalsIgnoreCase("null") ) {
				pageable = PageRequest.of(pageNumber, pageElements, Sort.by(fieldName).ascending());
			}
			else {
				pageable = PageRequest.of(pageNumber, pageElements, Sort.by(fieldName).descending());
			}
		}
		else { //default sort 
			pageable = PageRequest.of(pageNumber, pageElements, Sort.by("startDateTime").descending());
		}
		
		LocalDateTime fromDate = null;
		LocalDateTime toDate = null;
	
		List<Eventtype> eventTypes = new ArrayList<Eventtype>();
		if (eveTypes != null) {
			for (int i=0; i<eveTypes.length; i++) {
				Eventtype eveType = eventTypeRepository.findByeventTypeDesc(eveTypes[i]);
				if (eveType != null) {
					eventTypes.add(eveType);
				}
			}
		}
		
		List<Status> eventStatuses = new ArrayList<Status>();
		if (eveStatuses != null) {
			for (int i=0; i<eveStatuses.length; i++) {
				Status status = statusRepository.findBystatusDesc(eveStatuses[i]);
				if (status != null) {
					eventStatuses.add(status);
				}
			}
		}
	
		if (!from.equalsIgnoreCase("null") && !to.equalsIgnoreCase("null")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			fromDate = LocalDateTime.parse(from, formatter);
			toDate = LocalDateTime.parse(to, formatter);
		}
		
		//Option: 1	
		if (eventTypes.size() > 0 && fromDate != null && toDate != null) {
			
			if (eventStatuses.size() < 1) {
				events = eventRepository.
						findByStartDateTimeBetweenAndEventTypeIn
						(fromDate, toDate, eventTypes, pageable);
			}
			else {
				events = eventRepository.
						findByStartDateTimeBetweenAndEventTypeInAndLastStatusIn
						(fromDate, toDate, eventTypes, eventStatuses, pageable);
			}
		}
		
		//Option: 2
		else if(eventTypes.size() > 0 && fromDate == null && toDate == null) {
			
			if (eventStatuses.size() < 1) {
				events = eventRepository.
						findByEventTypeIn(eventTypes, pageable);
			}
			else {
				events = eventRepository.
						findByEventTypeInAndLastStatusIn
						(eventTypes, eventStatuses, pageable);
			}
		}
		
		//Option: 3
		else if (fromDate != null && toDate != null && eventTypes.size() < 1) {
			if (eventStatuses.size() < 1) {
				events = eventRepository.
						findByStartDateTimeBetween
						(fromDate, toDate, pageable);
				}
			else  {
				events = eventRepository.
						findByStartDateTimeBetweenAndLastStatusIn
						(fromDate, toDate, eventStatuses, pageable);
			}
		}
		
		//Option: 4
		else if (eventTypes.size() < 1 && fromDate == null && toDate == null) {
			if (eventStatuses.size() < 1) {
				events = eventRepository.findAll(pageable);
				}
			else {
				events = eventRepository.findByLastStatusIn(eventStatuses, pageable);
			}
		}
		
		return events;
	}
	
	@GetMapping("/send-invite/{subject}/{message}/{eventLocation}/{eventId}")
	public void createEventInvite(@PathVariable String subject, @PathVariable String message, 
	@PathVariable String eventLocation, @PathVariable Long eventId, @AuthenticationPrincipal Presenter sender,
	@RequestParam(value="presenter", required=false) Long [] presenterIds) throws Exception {
		if (subject == null) {
			subject = eventLocation;
		}
		eventService.sendEventInvite(subject, message, presenterIds, eventLocation, eventId, sender);
	}
	
		
	@GetMapping("/all-event-types")
	public List<Eventtype> getEventTypes(){
		List<Eventtype> allEventTypes = eventTypeRepository.findAll();
		return allEventTypes;
	}
	
}
