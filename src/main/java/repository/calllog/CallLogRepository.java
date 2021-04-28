package repository.calllog;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import repository.event.presenter.Presenter;
import repository.status.Status;

@Repository
public interface CallLogRepository extends JpaRepository<CallLog, Long>{

	@Query("SELECT cl FROM CallLog cl WHERE cl.createdBy = :presenter")
	List<CallLog> findBycreatedBy(@Param("presenter") Presenter presenter);
	
	Page<CallLog> findByCreatedBy(Presenter user, Pageable pageable);
	
	Page<CallLog> findByCreatedByAndLastModifiedDateBetween(Presenter user, LocalDateTime from, LocalDateTime to, Pageable pageable);
	
	Page<CallLog> findByCreatedByAndStatusIn(Presenter user, List<Status> statuses, Pageable pageable);
	
	Page<CallLog> findByCreatedByAndStatusInAndLastModifiedDateBetween(Presenter user, List<Status> statuses, LocalDateTime from, LocalDateTime to, Pageable pageable);
	
	
}
