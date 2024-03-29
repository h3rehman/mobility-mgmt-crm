package repository.note;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import repository.calllog.CallLog;
import repository.event.Event;
import repository.event.presenter.Presenter;
import repository.organization.Organization;
import repository.organization.contact.Contact;


@Entity
@Table(name = "Note")
public class Note {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NoteID")
	Long noteId;
	
	@Column(name = "noteentry")
	private String noteEntry;
	
	
	@Column(name = "createddate", columnDefinition = "TIMESTAMP")
	private LocalDateTime createDate;
	
	
	@Column(name = "lastmodifieddate", columnDefinition = "TIMESTAMP")
	private LocalDateTime lastModifiedDate;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "EventID")
	Event event;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "OrgID")
	Organization org;
	
	@ManyToOne
	@JoinColumn(name = "contactID")
	Contact contact;
	
	@ManyToOne
	@JoinColumn(name = "createdby")
	Presenter createdBy;
	
	@ManyToOne
	@JoinColumn(name = "lastmodifiedby")
	Presenter lastModifiedBy;
	
	@OneToOne
	@JoinColumn(name = "CalllogID")
	CallLog callLog;

	//Empty Constructor
	public Note (){}

	public Long getNoteId() {
		return noteId;
	}


	public void setNoteId(Long noteId) {
		this.noteId = noteId;
	}


	public String getNoteEntry() {
		return noteEntry;
	}


	public void setNoteEntry(String noteEntry) {
		this.noteEntry = noteEntry;
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


	public String getEvent() {
		if (event != null) {
			return event.getEventName();			
		}
		return null;
	}


	public void setEvent(Event event) {
		this.event = event;
	}


//	public String getOrg() {
//		return org.getOrgname();
//	}


	public void setOrg(Organization org) {
		this.org = org;
	}

	public Presenter getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Presenter createdBy) {
		this.createdBy = createdBy;
	}

	public Presenter getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(Presenter lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public CallLog getCallLog() {
		return callLog;
	}

	public void setCallLog(CallLog callLog) {
		this.callLog = callLog;
	}

	public String getContact() {
		if (contact != null) {
			return contact.getFirstName() + " " + contact.getLastName();
		}
		return null;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	
}
