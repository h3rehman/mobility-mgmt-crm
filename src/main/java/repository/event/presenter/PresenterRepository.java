package repository.event.presenter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresenterRepository extends JpaRepository<Presenter, Long>{
	
	public Presenter findByusername(String username);
	
	public List<Presenter> findByIsActiveTrue();
	
}
