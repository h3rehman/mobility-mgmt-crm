package services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import config.MailConfig;
import repository.event.Event;
import repository.event.EventRepository;
import repository.event.Eventtype;
import repository.event.EventtypeRepository;
import repository.event.audience.Audiencetype;
import repository.event.audience.AudiencetypeRepository;
import repository.event.audience.Eventaudiencetype;
import repository.event.audience.EventaudiencetypeRepository;
import repository.event.presenter.EventPresenterRepository;
import repository.event.presenter.Eventpresenter;
import repository.event.presenter.Presenter;
import repository.event.presenter.PresenterRepository;
import repository.organization.EventOrganization;
import repository.organization.EventOrganizationRepository;
import repository.organization.Organization;
import repository.organization.OrganizationRepository;
import repository.status.Status;
import repository.status.StatusRepository;
import services.calendar.CalendarRequest;
import services.calendar.CalendarService;

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
	
	@Autowired
	AudiencetypeRepository audienceTypeRepository;
	
	@Autowired
	EventaudiencetypeRepository eventAudienceTypeRepository;
	
	@Autowired
	StatusRepository statusRepository;
	
	@Autowired
	CalendarService calendarService;
	
	@Autowired
	MailConfig mailConfig;
	
//	@Autowired
//	CalendarRequest calendarRequest;
	
	@Autowired
	JavaMailSenderImpl mailSender;
	
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

	public void addEvent(Event eve, String eventTypeDesc, String orgId, 
			Long presenterId, boolean joinEve, Long[] audTypes, String lastStatus) throws Exception {

		Status status = null;
		if (lastStatus != null) {
			status = statusRepository.findBystatusDesc(lastStatus);
			if (status != null) {
				eve.setLastStatus(status);
			}
			else {
				Optional<Status> optionalStatus = statusRepository.findById(8L);//Id for Info Needed Status
				if (optionalStatus != null) {
					status = optionalStatus.get();
					eve.setLastStatus(status);
				}
			}
		}
		else {
			Optional<Status> optionalStatus = statusRepository.findById(8L); //Id for Info Needed Status
			if (optionalStatus != null) {
				status = optionalStatus.get();
				eve.setLastStatus(status);
			}
		}
		Eventtype eventType = eventtypeRepository.findByeventTypeDesc(eventTypeDesc);
		eve.setEventType(eventType);
		eventRepository.save(eve);
		
		if (audTypes != null) {				
			addAudienceTypes(audTypes, eve);
		}
		
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
				
				org.setLastStatus(status);
				orgRepository.save(org);
			}
		}
		
		//Presenter joining the event
		if (joinEve != false && presenterId != null) {
			joinEvent(eve.getEventId(), presenterId);
		}
	  System.out.println("### New Event created with ID: " + eve.getEventId());
	}

	public void updateEvent(Event eve, String eventTypeDesc, 
			String orgId, Long[] audTypes, String lastStatus) {
		Eventtype eventType = eventtypeRepository.findByeventTypeDesc(eventTypeDesc);
		Optional<Event> optionalEvent = eventRepository.findById(eve.getEventId());
		
		Long oId = Long.parseLong(orgId);
		Status status = null;
		if (lastStatus != null || lastStatus != "") {
		status = statusRepository.findBystatusDesc(lastStatus);
		}
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
			
			if (status != null) {
				eveOriginal.setLastStatus(status);
			}

			eventRepository.save(eveOriginal);
			
			if (audTypes != null) {				
				addAudienceTypes(audTypes, eveOriginal);
			}
				
			if (oId > 0) {
				Optional<Organization> optionalOrg = orgRepository.findById(oId);
				if(optionalOrg != null) {
					Organization org = optionalOrg.get();
					
					HashMap<Long, String> associatedOrgs = eveOriginal.eventOrgNames();
					if (!associatedOrgs.containsKey(oId)) {  //only add if the Organization does not exist. 
						EventOrganization eveOrg = new EventOrganization();
						eveOrg.setEvent(eveOriginal);
						eveOrg.setOrganization(org);
						eventOrgRepository.save(eveOrg);
						org.setLastStatus(status);
						orgRepository.save(org);
					}
				}
			}
			//Update Last Status of each Org associated with this event
			List<Organization> orgs = eveOriginal.orgsInEvent();
			if (orgs.size() > 0 && status != null) {
				for (int i=0; i<orgs.size(); i++) {
					Organization org = orgs.get(i);
					org.setLastStatus(status);
					orgRepository.save(org);
				}
			}
		}
		else {
			System.out.println("Event is null");
		}	
	}

	public void joinEvent(Long eventId, Long presenterId) throws Exception {
		Optional<Event> optionalEvent = eventRepository.findById(eventId);
		Optional<Presenter> optionalPresenter = presenterRepository.findById(presenterId);
		
		if (optionalEvent != null && optionalPresenter != null) {
			Event event = optionalEvent.get();
			Presenter presenter = optionalPresenter.get();
			Eventpresenter evePresenter = new Eventpresenter();
			evePresenter.setEvent(event);
			evePresenter.setPresenter(presenter);
			eventPresenterRepository.save(evePresenter);
			sendJoinedEventInvite(event, presenter);
		}
		
		else {
			System.out.println("Either Event or Presenter is null...");
		}		
	}
	
	void addAudienceTypes(Long[] audTypes, Event event) {
		for (int i=0; i<audTypes.length; i++) {
			Optional<Audiencetype> optionalAudType = audienceTypeRepository.findById(audTypes[i]);
			if (optionalAudType != null) {
				Audiencetype originalAudType = optionalAudType.get();
				Eventaudiencetype eveAud = new Eventaudiencetype();
				eveAud.setEvent(event);
				eveAud.setAudienceType(originalAudType);
				eventAudienceTypeRepository.save(eveAud);
			}
		}
	}
	
	public void sendEventInvite(String subject, String message, Long[] presenterIds, String eventLocation, 
			Long eventId, Presenter sender) throws Exception {
		
		    mailSender.setUsername(mailConfig.getUsername());
		    mailSender.setPassword(mailConfig.getPassword());
		    Properties properties = new Properties();
		    properties.put("mail.smtp.auth", mailConfig.getSmtpAuthRequire());
		    properties.put("mail.smtp.starttls.enable", mailConfig.getSmtpTLSRequire());
		    properties.put("mail.smtp.ssl.trust", mailConfig.getExchangeServer());
		    properties.put("mail.smtp.host", mailConfig.getExchangeServer());
		    properties.put("mail.smtp.port", mailConfig.getSmtpPort());
		    mailSender.setJavaMailProperties(properties);
		    
		    String emailBody = "Sender: " + sender.getName() + " " + sender.getLastName() + "\n "+
		    					" Note from Sender: " + message;
		    
			Optional<Event> optionalEvent = eventRepository.findById(eventId);
			
			if (optionalEvent != null) {
				Event event = optionalEvent.get();
				for (int i=0; i<presenterIds.length; i++) {
				
					Optional<Presenter> optionalPresenter = presenterRepository.findById(presenterIds[i]);
					if (optionalPresenter != null) {
						
						Presenter presenter = optionalPresenter.get();
						Eventpresenter evePresenter = eventPresenterRepository.findByEventAndPresenter(event, presenter);
						if (evePresenter == null) {
							Eventpresenter evePres = new Eventpresenter();
							evePres.setEvent(event);
							evePres.setPresenter(presenter);
							eventPresenterRepository.save(evePres);
							System.out.println("#### Added new Event Presenter ####: " + presenter.getName());
						}
						else { 
							System.out.println("#### Event Presenter already exist: " + presenter.getName());
						}
						calendarService.sendCalendarInvite(
								mailConfig.getFromEmail(),
								new CalendarRequest.Builder()
								.withSubject(subject)
								.withBody(emailBody)
								.withToEmail(presenter.getEmail())
								.withMeetingStartTime(event.getStartDateTime())
								.withMeetingEndTime(event.getEndDateTime())
								.withLocation(eventLocation)
								.build()
								);
					}
				}
			}
	}

	public void sendJoinedEventInvite(Event event, Presenter presenter) throws Exception {
		
		    mailSender.setUsername(mailConfig.getUsername());
		    mailSender.setPassword(mailConfig.getPassword());
		    Properties properties = new Properties();
		    properties.put("mail.smtp.auth", mailConfig.getSmtpAuthRequire());
		    properties.put("mail.smtp.starttls.enable", mailConfig.getSmtpTLSRequire());
		    properties.put("mail.smtp.ssl.trust", mailConfig.getExchangeServer());
		    properties.put("mail.smtp.host", mailConfig.getExchangeServer());
		    properties.put("mail.smtp.port", mailConfig.getSmtpPort());
		    mailSender.setJavaMailProperties(properties);
		    
		    String emailBody = "Event Type: " +  event.getEventTypeDesc();
		    
			calendarService.sendCalendarInvite(
					mailConfig.getFromEmail(),
					new CalendarRequest.Builder()
					.withSubject(event.getEventName())
					.withBody(emailBody)
					.withToEmail(presenter.getEmail())
					.withMeetingStartTime(event.getStartDateTime())
					.withMeetingEndTime(event.getEndDateTime())
					.withLocation(event.getLocation())
					.build()
					);
		}
}
