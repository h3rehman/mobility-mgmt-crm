package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import repository.event.Event;
import repository.event.Eventtype;
import repository.event.EventtypeRepository;
import repository.event.presenter.EventPresenterRepository;
import repository.event.presenter.Eventpresenter;
import repository.event.presenter.Presenter;
import repository.event.presenter.PresenterRepository;
import repository.status.Status;
import repository.status.StatusRepository;

@RestController
@RequestMapping("/api")
public class ManagerAccessControllers {
	
	private static String ONELOGIN_CLIENT_ID;
	private static String ONELOGIN_CLIENT_SECRET;
	private static String access_token;
	private String managerial_role_id;
	
	@Autowired
	PresenterRepository presenterRepository;
	
	@Autowired
	EventPresenterRepository eventPresenterRepository;
	
	@Autowired
	EventtypeRepository eventTypeRepository;
	
	@Autowired
	StatusRepository statusRepository;
	
	ManagerAccessControllers(@Value("${onelogin.api.client_id}") String cid,  
								@Value("${onelogin.api.client_secret}") String csec,
								@Value("${onelogin.api.managerial_role_id}") String manRoleId){
		ONELOGIN_CLIENT_ID = cid;
		ONELOGIN_CLIENT_SECRET = csec;
		this.managerial_role_id = manRoleId;
	}
	
	static String getOLAccessToken() {
		CloseableHttpClient client = HttpClientBuilder.create().build();

		HttpPost request = new HttpPost("https://api.us.onelogin.com/auth/oauth2/v2/token");

		String credentials = String.format("%s:%s", ONELOGIN_CLIENT_ID , ONELOGIN_CLIENT_SECRET);
		byte[] encodedAuth = Base64.getEncoder().encode(credentials.getBytes());
		String authHeader = "Basic " + new String(encodedAuth);

		request.setHeader("Authorization", authHeader);
		request.addHeader("Content-Type", "application/json");
		request.setEntity(new StringEntity("{ \"grant_type\": \"client_credentials\" }", "UTF-8"));
		
		System.out.println("Retrieving OneLogin access token......!");

		try {
			CloseableHttpResponse reponse = client.execute(request);

			String content = EntityUtils.toString(reponse.getEntity());
			JSONObject json = new JSONObject(content);
			String accessToken = json.getString("access_token");
		
			return accessToken;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Token not processed";

	}
	
	
	@GetMapping("/checkUserManagerialAccess")
	public boolean checkUserManagerialAccess(@AuthenticationPrincipal Presenter user) throws ClientProtocolException, IOException {
		boolean check = false;
		if (user != null) {
			CloseableHttpClient client = HttpClientBuilder.create().build();	
			HttpGet request = new HttpGet("https://rtachicago.onelogin.com/api/2/roles/" + managerial_role_id + "/users");
			
			if (access_token == null) {
				access_token = getOLAccessToken();
			}
			
			request.setHeader("Authorization", "bearer " + access_token);
			HttpResponse response = client.execute(request);
			
			//get a fresh access token if the token expired. 
			if (response.getStatusLine().getStatusCode() != 200) {
				access_token = getOLAccessToken();
				request.setHeader("Authorization", "bearer " + access_token);
				response = client.execute(request);
			}
			
			String content = EntityUtils.toString(response.getEntity());
			JSONArray jsonResArray = new JSONArray(content);
			
			for (int i=0; i<jsonResArray.length(); i++) {
				JSONObject jsonObj = jsonResArray.getJSONObject(i);
				String roleUserName = jsonObj.getString("username");
				
				if (roleUserName.equalsIgnoreCase(user.getUsername())) {
					System.out.println("Role username: " + roleUserName);
					System.out.println("Active username: " + user.getUsername());
					check = true;
					break;
				}
			}
		}
		
		return check;	
	}
	
	
	@GetMapping("/managerialAccessRequest")
	public String managerialAccessRequest () {
	
		String accessToken = getOLAccessToken();
		return accessToken;
	}
	
	@GetMapping("/sorted-default-presenter-events/{presenterId}")
	Page<Event> getDefaultUpcomingEventsForPresenter(@PathVariable Long presenterId) {
		
		Optional<Presenter> optionalUser = presenterRepository.findById(presenterId);
		if (optionalUser != null) {
			Presenter user = optionalUser.get();
			final int pageNumber = 0;
			final int pageElements = 10;
			
			Pageable pageable = PageRequest.of(pageNumber, pageElements, Sort.by("event.startDateTime").descending());
			
			ZoneId central = ZoneId.of("America/Chicago");
			LocalDate dateNow = LocalDate.now(central);
			LocalDateTime date2DateTime = LocalDateTime.parse(dateNow.toString() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //Will compare date from Midnight
			
			Page<Eventpresenter> eventPresenters = eventPresenterRepository.findByPresenterAndEventStartDateTimeGreaterThanEqual(user, date2DateTime, pageable);
			List<Event> eventList = new ArrayList<Event>();
			
			for (Eventpresenter evePresenter : eventPresenters.getContent()) {
				eventList.add(evePresenter.getEvent()); 
			}
			
			Page<Event> events = new PageImpl<Event>(eventList, pageable, eventPresenters.getTotalElements());
			return events;	
		}
		else {
			System.out.println("No presenter found with the id: " + presenterId);
			return null;
		}
	}
	
	
	@GetMapping("/sorted-filtered-presenter-events/{presenterId}/{pageNumber}/{pageElements}/{fieldName}/{sortOrder}/{from}/{to}/{onlyUpcoming}")
	Page<Event> getMySortedFilteredEvents(Long presenterId, @PathVariable Integer pageNumber, 
			@PathVariable Integer pageElements, @PathVariable String fieldName, @PathVariable String sortOrder, 
			@PathVariable String from, @PathVariable String to, @PathVariable Boolean onlyUpcoming,
			@RequestParam(value="eveType", required=false) String [] eveTypes,
			@RequestParam(value="eveStatus", required=false) String[] eveStatuses) {
		
		Optional<Presenter> optionalUser = presenterRepository.findById(presenterId);
		if (optionalUser != null) {
			Presenter user = optionalUser.get();
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
				pageable = PageRequest.of(pageNumber, pageElements, Sort.by("event.startDateTime").descending());
			}
			
		
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
			
			LocalDateTime fromDate = null;
			LocalDateTime toDate = null;
			
			if (!from.equalsIgnoreCase("null") && !to.equalsIgnoreCase("null")) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				fromDate = LocalDateTime.parse(from, formatter);
				toDate = LocalDateTime.parse(to, formatter);
			}
			
			Page<Eventpresenter> eventPresenters = null;
			List<Event> eventList = new ArrayList<Event>();
	
			if (onlyUpcoming) { //Only Upcoming events (no from/to dates)
	
				ZoneId central = ZoneId.of("America/Chicago");
				LocalDate dateNow = LocalDate.now(central);
				LocalDateTime dateTime = LocalDateTime.parse(dateNow.toString() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //Will compare date from Midnight
	
				//Option: 1	
				if (eventTypes.size() > 0) {
					
					if (eventStatuses.size() < 1) {
						eventPresenters = eventPresenterRepository.findByPresenterAndEventEventTypeInAndEventStartDateTimeGreaterThanEqual(user, eventTypes, dateTime, pageable);
					}
					else {
					eventPresenters = eventPresenterRepository.findByPresenterAndEventEventTypeInAndEventLastStatusInAndEventStartDateTimeGreaterThanEqual(user, eventTypes, eventStatuses, dateTime, pageable);
					}
				}
				
				//Option: 2
				else {
					if (eventStatuses.size() < 1) {
						eventPresenters = eventPresenterRepository.findByPresenterAndEventStartDateTimeGreaterThanEqual(user, dateTime, pageable);
					}
					else  {
						eventPresenters = eventPresenterRepository.findByPresenterAndEventLastStatusInAndEventStartDateTimeGreaterThanEqual(user, eventStatuses, dateTime, pageable);		
				}	
			  }
			}
			else {
						
				//Option: 1	
				if (eventTypes.size() > 0 && fromDate != null && toDate != null) {
					
					if (eventStatuses.size() < 1) {
						eventPresenters = eventPresenterRepository.findByPresenterAndEventStartDateTimeBetweenAndEventEventTypeIn(user, fromDate, toDate, eventTypes, pageable);
					}
					else {
						eventPresenters = eventPresenterRepository.findByPresenterAndEventStartDateTimeBetweenAndEventEventTypeInAndEventLastStatusIn(user, fromDate, toDate, eventTypes, eventStatuses, pageable);
					}
				}
				
				//Option: 2
				else if(eventTypes.size() > 0 && fromDate == null && toDate == null) {
					
					if (eventStatuses.size() < 1) {
						eventPresenters = eventPresenterRepository.findByPresenterAndEventEventTypeIn(user, eventTypes, pageable);
					}
					else {
						eventPresenters = eventPresenterRepository.findByPresenterAndEventEventTypeInAndEventLastStatusIn(user, eventTypes, eventStatuses, pageable);				}
				}
				
				//Option: 3
				else if (fromDate != null && toDate != null && eventTypes.size() < 1) {
					if (eventStatuses.size() < 1) {
						eventPresenters = eventPresenterRepository.findByPresenterAndEventStartDateTimeBetween(user, fromDate, toDate, pageable);
						}
					else  {
						eventPresenters = eventPresenterRepository.findByPresenterAndEventStartDateTimeBetweenAndEventLastStatusIn(user, fromDate, toDate, eventStatuses, pageable);
					}
				}
				
				//Option: 4
				else {
					if (eventStatuses.size() < 1) {
						eventPresenters = eventPresenterRepository.findByPresenter(user, pageable);
						}
					else {
						eventPresenters = eventPresenterRepository.findByPresenterAndEventLastStatusIn(user, eventStatuses, pageable);
					}
				}
			}
		
			
			for (Eventpresenter evePresenter : eventPresenters.getContent()) {
				eventList.add(evePresenter.getEvent()); 
			}
			
			Pageable pageableEvents = null;
			if (!fieldName.equalsIgnoreCase("null")) {
				int preHead = fieldName.indexOf(".");
				String eventFieldName = fieldName.substring(preHead+1);
	
				if (sortOrder.equalsIgnoreCase("asce") || sortOrder.equalsIgnoreCase("null") ) {
					pageableEvents = PageRequest.of(pageNumber, pageElements, Sort.by(eventFieldName).ascending());
				}
				else {
					pageableEvents = PageRequest.of(pageNumber, pageElements, Sort.by(eventFieldName).descending());
				}
			}
			else { //default sort 
				pageableEvents = PageRequest.of(pageNumber, pageElements, Sort.by("startDateTime").descending());
			}
	
			
			events = new PageImpl<Event>(eventList, pageableEvents, eventPresenters.getTotalElements());
			
			return events;	
		}
		System.out.println("Cannot find presenter with the presenter id provided: " + presenterId);
		return null;
	}
	
	

}