package repository.calllog;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import repository.event.presenter.Presenter;
import repository.organization.Organization;
import repository.organization.contact.Contact;
import repository.status.Status;

@Entity
@Table(name = "Calllog")
public class CallLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CalllogID")
	Long callId;

	@ManyToOne
	@JoinColumn(name = "OrgID")
	Organization org;
	
	@ManyToOne
	@JoinColumn(name = "contactid")
	Contact contact;
	
	@ManyToOne
	@JoinColumn(name = "StatusID")
	Status status;
	
	@ManyToOne
	@JoinColumn(name = "createdby")
	Presenter createdBy;
	
	@ManyToOne
	@JoinColumn(name = "lastmodifiedby")
	Presenter lastModifiedBy;

	@Column(name = "laststatusdate", columnDefinition = "TIMESTAMP")
	private LocalDateTime lastStatusDate;

	@Column(name = "createdate", columnDefinition = "TIMESTAMP")
	private LocalDateTime createDate;
	
	@Column(name = "lastmodifieddate", columnDefinition = "TIMESTAMP")
	private LocalDateTime lastModifiedDate;
	
	CallLog (){}

	public Long getCallId() {
		return callId;
	}

	public void setCallId(Long callId) {
		this.callId = callId;
	}

	public Map<String, String> getOrg() {
		if (org != null) {			
			Map<String, String> orgDetail = new HashMap<String, String>();
			orgDetail.put("orgId", org.getOrgId().toString());
			orgDetail.put("orgName", org.getOrgname());
			return orgDetail;
		}
		return null;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}

	public Map<String, String> getContact() {
		if (contact != null) {
			Map <String, String> contactDetail = new HashMap<String, String>();
			contactDetail.put("contactId", contact.getContactId().toString());
			contactDetail.put("contactName", contact.getFirstName() +
					" " + contact.getLastName());
			return contactDetail;
		}
		else {
			return null;
		}
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Map<String, String> getStatus() {
		Map<String, String> statusDetail = new HashMap<String, String>();
		statusDetail.put("lastStatusId", status.getStatusId().toString());
		statusDetail.put("lastStatus", status.getStatusDesc());
		return statusDetail;
	}

	public void setStatus(Status newStatus) {
		this.status = newStatus;
	}

	public LocalDateTime getLastStatusDate() {
		return lastStatusDate;
	}

	public void setLastStatusDate(LocalDateTime lastStatusDate) {
		this.lastStatusDate = lastStatusDate;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getCreatedBy() {
		if (createdBy != null) {
			return createdBy.getName() + " " + createdBy.getLastName();			
		}
		else {
			return null;
		}
	}

	public void setCreatedBy(Presenter createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		if (lastModifiedBy != null) {
			return lastModifiedBy.getName() + " " + lastModifiedBy.getLastName();			
		}
		else {
			return null;
		}
	}

	public void setLastModifiedBy(Presenter lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
	
}
