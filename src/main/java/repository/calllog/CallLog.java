package repository.calllog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CallLog")
public class CallLog {
	
	@Id
	@Column(name = "calllogId")
	long callId;

	
}
