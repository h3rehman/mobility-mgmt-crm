package repository.organization.county;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import repository.organization.Organization;

@Entity
@Table(name = "County")
public class County {
	
	@Id
	@Column(name = "CountyID")
	private Long countyId;
	
	@Column(name = "countydesc")
	private String countyDesc;
	
	@OneToMany(mappedBy = "county")
	private Set<Organization> organizations = new HashSet<Organization>();

	public Long getCountyId() {
		return countyId;
	}

	public void setCountyId(Long countyId) {
		this.countyId = countyId;
	}

	public String getCountyDesc() {
		return countyDesc;
	}

	public void setCountyDesc(String countyDesc) {
		this.countyDesc = countyDesc;
	}

	@JsonIgnore
	public Set<Organization> getOrganizations() {
		return organizations;
	}
	
	
}
