package repository.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import application.Application;

@SpringBootTest(classes = Application.class)
public class EventJpaTests {
	
	@Autowired
	EventRepository eventRepository;
	
	@Test
	public void testForAllEvents() {
		List<Event> events = eventRepository.findAll();				
		assertEquals(13, events.size());
		assertNotNull(events);
				
	}

	@Test
	public void testForOneEvent() {
		Optional<Event> event = eventRepository.findById(1L);
		assertNotNull(event);
//		assertEquals("The Hub", event.get().getEventName());
//		assertEquals("Presentation", event.get().getEventType());
		
//		List<Event> events = eventRepository.findAll();
//		assertEquals("The Prestige", events.get(2).getEventName());
//		assertEquals("Resource Fair", events.get(2).getEventType());
	}
}
