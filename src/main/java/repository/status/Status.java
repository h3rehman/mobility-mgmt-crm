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
	@Column(name = "statusId")
	long statusId;
	
	private String description;
	private String type;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "statusId")
	Set<EventStatus> statusEvents;
	
	Status(){}
	
	public long getStatusId() {
		return statusId;
	}
	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
