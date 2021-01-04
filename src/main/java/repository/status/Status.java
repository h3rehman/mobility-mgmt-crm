package repository.status;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "Status")
public class Status {

	@Id
	@Column(name = "StatusID")
	Long statusId;
	
	@Column(name = "statusdesc")
	private String statusDesc;
	
	@Column(name = "statustype")
	private String statusType;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "StatusID")
	Set<EventStatus> statusEvents;
	
	Status(){}
	
	public Long getStatusId() {
		return statusId;
	}
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String description) {
		this.statusDesc = description;
	}
	public String getStatusType() {
		return statusType;
	}
	public void setStatusType(String type) {
		this.statusType = type;
	}
	
	
}
