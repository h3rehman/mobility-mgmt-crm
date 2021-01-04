package repository.calllog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import repository.event.presenter.Presenter;

@Repository
public interface CallLogRepository extends JpaRepository<CallLog, Long>{

	@Query("SELECT cl FROM CallLog cl WHERE cl.createdBy = :presenter")
	List<CallLog> findBycreatedBy(@Param("presenter") Presenter presenter);
}
