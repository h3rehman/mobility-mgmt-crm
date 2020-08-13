package controllers;

import java.net.URI;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonView;

import repository.event.Event;
import repository.event.EventService;
import repository.organization.Organization;
import repository.organization.View;

@RestController
@RequestMapping("/api")
public class EventControllers {
	
	DataSource dataSource;
	
	@Autowired
	EventService eventService;

	EventControllers (DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	@GetMapping("/events")
	List<Event> getAllEvents(){
		return eventService.getAllEvents();
	}
	
	@GetMapping("/events/{id}")
	public Event getEventDetail(@PathVariable Long id) throws ClassNotFoundException{
		return eventService.getEventById(id);
	}
	
	@GetMapping("/upcoming-events")
	List<Event> getFutureEvents(@RequestParam String dateTime){
		return eventService.upcomingAppointments(dateTime);
	}

	@GetMapping("/appointments/{presenterId}")
	List<Event> getMyEvents(@PathVariable int presenterId) throws ClassNotFoundException{
		return eventService.myUpcomingAppointments(presenterId);
	}
	
	@PostMapping("/event/{eventType}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createEvent(@RequestBody Event eve, @PathVariable String eventType){ 
				
		eventService.addEvent(eve, eventType);
 		
		// Build the location URI of the new item
		 URI location = ServletUriComponentsBuilder
		 .fromCurrentRequestUri()
		 .path("/{eventId}")
		 .buildAndExpand(eve.getEventId())
		 .toUri(); 

		// Explicitly create a 201 Created response
		 return ResponseEntity.created(location).build();	
	}
	
}
