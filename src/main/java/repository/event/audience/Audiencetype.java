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
	Long audiencetypeId;
	
	@Column(name = "audiencedesc")
	String audienceDesc;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "audiencetypeid")
	@JsonIgnore
	Set<Eventaudiencetype> eventsForAudience;
	
	Audiencetype() {}

	public Long getAudiencetypeId() {
		return audiencetypeId;
	}

	public void setAudiencetypeId(Long audienceTypeId) {
		this.audiencetypeId = audienceTypeId;
	}

	public String getAudienceDesc() {
		return audienceDesc;
	}

	public void setAudieanceDesc(String audienceDesc) {
		this.audienceDesc = audienceDesc;
	}

	@JsonIgnore
	public Set<Eventaudiencetype> getEventsForAudience() {
		return eventsForAudience;
	}

	public void setEventsForAudience(Set<Eventaudiencetype> eventAudienceTypes) {
		this.eventsForAudience = eventAudienceTypes;
	}
}
