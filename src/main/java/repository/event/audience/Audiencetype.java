package repository.event.audience;


import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Audiencetype")
public class Audiencetype {

	@Id
	@Column(name = "audiencetypeid")
	long audiencetypeId;
	
	@Column(name = "audiencedesc")
	String audieanceDesc;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "audiencetypeid")
	@JsonIgnore
	Set<Eventaudiencetype> eventsForAudience;

	public long getAudienceTypeId() {
		return audiencetypeId;
	}

	public void setAudienceTypeId(long audienceTypeId) {
		this.audiencetypeId = audienceTypeId;
	}

	public String getAudieanceDesc() {
		return audieanceDesc;
	}

	public void setAudieanceDesc(String audieanceDesc) {
		this.audieanceDesc = audieanceDesc;
	}

	@JsonIgnore
	public Set<Eventaudiencetype> getEventsForAudience() {
		return eventsForAudience;
	}

	public void setEventsForAudience(Set<Eventaudiencetype> eventAudienceTypes) {
		this.eventsForAudience = eventAudienceTypes;
	}
	
	
	
}
