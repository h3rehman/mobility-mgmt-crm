package repository.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import repository.event.audience.Eventaudiencetype;
import repository.event.presenter.Eventpresenter;
import repository.organization.EventOrganization;
import repository.status.EventStatus;

@Entity
@Table(name = "Event")
public class Event {

	@Id
	@Column(name = "EventID")
	private long eventId;
	
	@Column(name = "eventname")
	private String eventName;
		
	private String location;
	private String address;
	private String city;
	private String state;
	private String zip;
	
	@Column(name = "startdatetime", columnDefinition = "TIMESTAMP")
	private LocalDateTime startDateTime;
	
	@Column(name = "enddatetime", columnDefinition = "TIMESTAMP")
	private LocalDateTime endDateTime;
	
	@Column(name = "rtastaffcount")
	private String rtaStaffCount;

	@Column(name = "audiencecount")
	private String audienceCount;
	
	@Column(name = "surveycomplete")
	private int surveyComplete;
	
	@ManyToOne
	@JoinColumn(name = "EventtypeID")
	private Eventtype eventType;

	@OneToMany
	@JoinColumn(name = "EventID")
	List<Eventpresenter> eventPresenters;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "EventID")
	@JsonIgnore
	Set<Eventaudiencetype> eventaudienceTypes;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "EventID")
	Set<EventOrganization> eventOrganizations;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "EventID")
	Set<EventStatus> eventStatuses;

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

	public String getEventType() {
		return eventType.getEventTypeDesc();
	}

	public List<String> getEventPresenters() {
		List<String> presenters = new ArrayList<String>();
		for (Eventpresenter eventPresenter : eventPresenters) {
			presenters.add(eventPresenter.presenter.getName());
		}
		return presenters;
	}

	public long getEventId() {
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

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public void setEventType(Eventtype eventType) {
		this.eventType = eventType;
	}

	public void setEventPresenters(List<Eventpresenter> eventPresenters) {
		this.eventPresenters = eventPresenters;
	}

	
	public Set<String> getEventaudienceType() {
		Set <String> audiences = new HashSet<String>();
		for(Eventaudiencetype audType : eventaudienceTypes) {
			audiences.add(audType.getAudienceType().getAudieanceDesc());
		}
		return audiences;
	}

	public void setEventaudienceType(Set<Eventaudiencetype> eventaudienceTypes) {
		this.eventaudienceTypes = eventaudienceTypes;
	}
	
	
}
