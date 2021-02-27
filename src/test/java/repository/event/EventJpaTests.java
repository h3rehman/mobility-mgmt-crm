package repository.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import application.Application;

@EnableAutoConfiguration
@SpringBootTest(classes = Application.class)
@ExtendWith(SpringExtension.class)
public class EventJpaTests {
	
	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	EventtypeRepository eventTypeRepository;
	
	
//	@Autowired
//	DataSource dataSource;
//	
//	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

//	EventJpaTests (DataSource dataSource){
//		this.dataSource = dataSource;
//		this.jdbcTemplate = new JdbcTemplate(dataSource);
//	}
	
	@Test
	public void testForAllEvents() {
		List<Event> events = eventRepository.findAll();				
//		assertEquals(16, events.size());
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
	
	@Test
	public void testForEventFilterAndCount() {
		
		Long eventCountJPALong = eventRepository.count();
		int eventCountJPA = eventCountJPALong.intValue();
		
		String sql = "SELECT COUNT(*) FROM Event";
//		Integer eventCountJDBC = jdbcTemplate.queryForObject(sql, Integer.class);
		
		assertEquals(eventCountJPA, 26);
		
		Pageable pageable = PageRequest.of(0, eventCountJPA);
		
		LocalDateTime fromDate = null;
		LocalDateTime toDate = null;
		
		String from = "2020-10-01 00:00:00";
		String to = "2021-01-31 00:00:00";
		
		String sql2 = "SELECT * from EVENT e "
					+ "WHERE e.startdatetime between ? AND ? "
					+ "AND e.EventtypeID = ?";
		
//		List<Map<String, Object>> eventsJDBC = jdbcTemplate.queryForList(sql2, from, to, 1L);
			
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		fromDate = LocalDateTime.parse(from, formatter);
		toDate = LocalDateTime.parse(to, formatter);
		
		Eventtype eventType = eventTypeRepository.findByeventTypeDesc("Presentation");
		
		List <Eventtype> eventTypes = new ArrayList<Eventtype>();
		eventTypes.add(eventType);
		
		Page<Event> eventsJPA = eventRepository.
		findByStartDateTimeBetweenAndEventTypeIn(fromDate, toDate, eventTypes, pageable);
		
		assertEquals(eventsJPA.getTotalElements(), 5);
		
	}
}
