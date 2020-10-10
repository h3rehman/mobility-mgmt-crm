package controllers;

import java.net.URI;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
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
import repository.event.presenter.Presenter;
import repository.organization.Organization;
import services.EventService;


@RestController
@RequestMapping("/api")
public class EventControllers {
	
	DataSource dataSource;
	
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	EventService eventService;

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
	
	@PostMapping("/event/{eventType}/{orgId}/{joinEve}")
	@ResponseStatus(HttpStatus.CREATED) //201
	public ResponseEntity<Void> createEvent(@RequestBody Event eve, 
	@PathVariable String eventType, @PathVariable String orgId, @PathVariable boolean joinEve, 
	@AuthenticationPrincipal Presenter user){ 
		Long presenterId = null;
		if (user != null) {
			presenterId = user.getPresenterId();
		}
		
		eventService.addEvent(eve, eventType, orgId, presenterId, joinEve);
 		
		// Build the location URI of the new item
		 URI location = ServletUriComponentsBuilder
		 .fromCurrentRequestUri()
		 .path("/{eventId}")
		 .buildAndExpand(eve.getEventId())
		 .toUri(); 

		// Explicitly create a 201 Created response
		 return ResponseEntity.created(location).build();	
	}
	
	@PutMapping("/event/{eventType}/{orgId}/{joinEve}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void updateEvent(@RequestBody Event eve, @PathVariable String eventType, @PathVariable String orgId) {
		eventService.updateEvent(eve, eventType, orgId);
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
	
}
