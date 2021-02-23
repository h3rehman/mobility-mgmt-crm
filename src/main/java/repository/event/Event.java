package repository.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import repository.event.audience.Audiencetype;
import repository.event.audience.Eventaudiencetype;
import repository.event.presenter.Eventpresenter;
import repository.event.presenter.Presenter;
import repository.organization.EventOrganization;
import repository.organization.Organization;
import repository.organization.View;
import repository.status.EventStatus;
import repository.status.Status;

@Entity
@Table(name = "Event")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EventID")
	@JsonView(View.OrgDetail.class)
	private Long eventId;
	
	@Column(name = "eventname")
	private String eventName;
		
	@JsonView(View.OrgDetail.class)
	private String location;
	private String address;
	private String city;
	private String state;
	private String zip;
	
	@Column(name = "startdatetime", columnDefinition = "TIMESTAMP")
	@JsonView(View.OrgDetail.class)
	private LocalDateTime startDateTime;
	
	@Column(name = "enddatetime", columnDefinition = "TIMESTAMP")
	@JsonView(View.OrgDetail.class)
	private LocalDateTime endDateTime;
	
	@Column(name = "rtastaffcount")
	private String rtaStaffCount;	
	
	@Column(name = "audiencecount")
	private String audienceCount;
	
	@Column(name = "surveycomplete")
	private int surveyComplete;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "EventtypeID")
	@JsonIgnore
	Eventtype eventType;

	@OneToMany
	@JoinColumn(name = "EventID")
	@JsonView(View.OrgDetail.class)
	Set<Eventpresenter> eventPresenters;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "EventID")
	@JsonIgnore
	Set<Eventaudiencetype> eventaudienceTypes;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "EventID")
	@JsonIgnore
	Set<EventOrganization> eventOrganizations;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "EventID")
	Set<EventStatus> eventStatuses;
	
	@ManyToOne
	@JoinColumn(name = "laststatusID")
	Status lastStatus;

	//Empty Constructor for JPA
	Event(){}
	
	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getRtaStaffCount() {
		return rtaStaffCount;
	}

	public void setRtaStaffCount(String rtaStaffCount) {
		this.rtaStaffCount = rtaStaffCount;
	}

	public String getAudienceCount() {
		return audienceCount;
	}

	public void setAudienceCount(String audienceCount) {
		this.audienceCount = audienceCount;
	}

	public int getSurveyComplete() {
		return surveyComplete;
	}

	public void setSurveyComplete(int surveyComplete) {
		this.surveyComplete = surveyComplete;
	}
	
	
	public Eventtype getEventType() {
		return eventType;
	}
	
	@JsonView(View.OrgDetail.class)
	@JsonInclude(Include.NON_NULL)
	public String getEventTypeDesc() {
		return eventType.getEventTypeDesc();
	}


	public List<String> getEventPresenters() {
		List<String> presenters = new ArrayList<String>();
		for (Eventpresenter eventPresenter : eventPresenters) {
			String fullName = eventPresenter.presenter.getName() + " " + eventPresenter.presenter.getLastName();
			presenters.add(fullName);
		}
		return presenters;
	}
	
	
//	public HashMap<Long, String[]> getEventPresenters() {
//		HashMap<Long, String[]> presenters = new HashMap<>();
//		for (Eventpresenter eventPresenter : eventPresenters) {
//			String[] firstNLastNames = new String[2];
//			firstNLastNames[0] = eventPresenter.presenter.getName();
//			firstNLastNames[1] = eventPresenter.presenter.getLastName();
//			presenters.put(eventPresenter.presenter.getPresenterId(), firstNLastNames);
//		}
//		return presenters;
//	}


	public Long getEventId() {
		return eventId;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public void setEventType(Eventtype eventType) {
		this.eventType = eventType;
	}

	public void setEventPresenters(Set<Eventpresenter> eventPresenters) {
		this.eventPresenters = eventPresenters;
	}

	public Set<Audiencetype> fetchAudienceTypes(){
		Set<Audiencetype> audTypes = new HashSet<Audiencetype>(); 
		for(Eventaudiencetype eveAudType : eventaudienceTypes) {
			audTypes.add(eveAudType.getAudienceType());
		}
		return audTypes;
	}
	
	public List<String> getEventaudienceType() {
		List<String> audienceTypes = new ArrayList<String>();
		for(Eventaudiencetype eveAudType : eventaudienceTypes) {
			audienceTypes.add(eveAudType.getAudienceType().getAudienceDesc());
		}
		return audienceTypes;
	}

	public void setEventaudienceType(Set<Eventaudiencetype> eventaudienceTypes) {
		this.eventaudienceTypes = eventaudienceTypes;
	}
	
	
	@JsonIgnore
	public HashMap<Long, String> eventOrgNames(){
		HashMap<Long, String> orgs = new HashMap<>();
		for (EventOrganization eveOrg: eventOrganizations) {
			orgs.put(eveOrg.getOrganization().getOrgId(), eveOrg.getOrganization().getOrgname());
		}
		return orgs;
	}
	
	@JsonProperty
	public HashMap<Long, List<String>> getOrgNames(){
		HashMap<Long, List<String>> orgs = new HashMap<>();
		for (EventOrganization eveOrg: eventOrganizations) {
			List<String> orgNameStatus = new ArrayList<>();
			orgNameStatus.add(eveOrg.getOrganization().getOrgname());
			orgNameStatus.add(eveOrg.getOrganization().getLastStatus());
			orgs.put(eveOrg.getOrganization().getOrgId(), orgNameStatus);
		}
		return orgs;
	}

	
	@JsonIgnore
	public List<Organization> orgsInEvent () {
		List<Organization> orgs = new ArrayList<Organization>();
		for (EventOrganization eveOrg: eventOrganizations) {
			orgs.add(eveOrg.getOrganization());
		}
		return orgs;
	}
	
	@JsonIgnore
	private void setOrgNames(Set<EventOrganization> eveOrgs) {
		this.eventOrganizations = eveOrgs;
	}

	public String getLastStatus() {
		if (lastStatus != null) {
			return lastStatus.getStatusDesc();
		}
		return null;
	}	

	public void setLastStatus(Status lastStatus) {
		this.lastStatus = lastStatus;
	}

}
