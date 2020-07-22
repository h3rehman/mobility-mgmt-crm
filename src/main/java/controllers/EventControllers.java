package controllers;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import repository.event.Event;
import repository.event.EventService;

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
	
	@GetMapping("/upcoming-events")
	List<Event> getFutureEvents(@RequestParam String dateTime){
		return eventService.upcomingAppointments(dateTime);
	}

	@GetMapping("/appointments/{presenterId}")
	List<Event> getMyEvents(@PathVariable int presenterId) throws ClassNotFoundException{
		return eventService.myUpcomingAppointments(presenterId);
	}
	
}
