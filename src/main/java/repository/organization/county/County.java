package repository.organization.county;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import repository.organization.Organization;

@Entity
@Table(name = "County")
public class County {
	
	@Id
	@Column(name = "CountyID")
	private long countyId;
	
	@Column(name = "countydesc")
	private String countyDesc;
	
	@OneToMany(mappedBy = "county")
	private Set<Organization> organizations = new HashSet<Organization>();

	public long getCountyId() {
		return countyId;
	}

	public void setCountyId(long countyId) {
		this.countyId = countyId;
	}

	public String getCountyDesc() {
		return countyDesc;
	}

	public void setCountyDesc(String countyDesc) {
		this.countyDesc = countyDesc;
	}

	public Set<Organization> getCustomers() {
		return organizations;
	}
	
	
}
