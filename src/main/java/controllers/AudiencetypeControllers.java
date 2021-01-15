package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import repository.event.Event;
import repository.event.EventRepository;
import repository.event.audience.Audiencetype;
import repository.event.audience.AudiencetypeRepository;
import repository.event.audience.CustomAudience;
import repository.event.audience.Eventaudiencetype;
import repository.event.presenter.Presenter;
import services.EventService;

@RestController
@RequestMapping("/api")
public class AudiencetypeControllers {
	
	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	AudiencetypeRepository audienceTypeRepository;
	
	@Autowired
	EventRepository eventRepository;
		
	AudiencetypeControllers (DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@GetMapping("/allAudienceWithTypeExist/{eventId}")
	List<CustomAudience> allCheckedAudienceTypes (@PathVariable Long eventId){
		
		List<Audiencetype> allAudiences = audienceTypeRepository.findAll();
		List<CustomAudience> allCheckedAudiences = new ArrayList<CustomAudience>();
		
		if (eventId != -1) {
			Optional<Event> optionalEvent = eventRepository.findById(eventId);
			if (optionalEvent != null) {
				Event event = optionalEvent.get();
				Set <Audiencetype> eveAudienceTypes = event.fetchAudienceTypes();
				for (int i=0; i<allAudiences.size(); i++) {
					Audiencetype audiType = allAudiences.get(i);
					CustomAudience customAudi = new CustomAudience();
					customAudi.setAudiencetypeId(audiType.getAudiencetypeId()); 
					customAudi.setAudienceDesc(audiType.getAudienceDesc()); 

					if (eveAudienceTypes.contains(audiType)) {
						customAudi.setTypeExist(true);
					}
					else {
						customAudi.setTypeExist(false);
					}
					allCheckedAudiences.add(customAudi);
				}
			}
			else {
				System.out.println("Event is null...");
				return null;
			}
		}
		else {
			for (int i=0; i<allAudiences.size(); i++) {
				Audiencetype audiType = allAudiences.get(i);
				CustomAudience customAudi = new CustomAudience();
				customAudi.setAudiencetypeId(audiType.getAudiencetypeId()); 
				customAudi.setAudienceDesc(audiType.getAudienceDesc()); 
				customAudi.setTypeExist(false);
				allCheckedAudiences.add(customAudi);
			}
		}
		return allCheckedAudiences;
	}
	
	@DeleteMapping("/deleteAudienceType/{eventId}/{audId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	void deleteAudienceType (@PathVariable Long eventId, @PathVariable Long audId, 
			@AuthenticationPrincipal Presenter user) {
		if (user != null) {
			String sql = "DELETE from eventaudiencetype "
					   + "WHERE EventID = ? "
					   + "AND audiencetypeid = ?";
			jdbcTemplate.update(sql, eventId, audId);
		}
	}
}
