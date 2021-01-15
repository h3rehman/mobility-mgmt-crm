package repository.event.audience;

/**
 * 
 * @author Hamood
 * Custom Audiencetype object to add a boolean field and compare the audience types 
 * in EventAudiencetype so that its easier to get the last state of check-boxes in UI.    
 *
 */

public class CustomAudience {
	
	Long audiencetypeId;
	String audienceDesc;
	Boolean typeExist;
	
	public CustomAudience() {}

	public Long getAudiencetypeId() {
		return audiencetypeId;
	}

	public void setAudiencetypeId(Long audienceTypeId) {
		this.audiencetypeId = audienceTypeId;
	}

	public String getAudienceDesc() {
		return audienceDesc;
	}

	public void setAudienceDesc(String audienceDesc) {
		this.audienceDesc = audienceDesc;
	}

	public Boolean getTypeExist() {
		return typeExist;
	}

	public void setTypeExist(Boolean typeExist) {
		this.typeExist = typeExist;
	}

}
