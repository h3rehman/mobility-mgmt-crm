package controllers;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repository.status.Status;
import repository.status.StatusRepository;

@RestController
@RequestMapping("/api")
public class StatusControllers {

	@Autowired	
	StatusRepository statusRepository;
	
	DataSource dataSource;
	
	JdbcTemplate jdbcTemplate;
	
	StatusControllers (DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@GetMapping("/orgStatusTypes")
	List<Status> getOrgStatusTypes (){
		String orgType = "O";
		return statusRepository.findBystatusType(orgType);
	}
	
	@GetMapping("/eventStatusTypes")
	List<Map<String, Object>> getEventStatusTypes () {
		String sql = "SELECT statusdesc "
				   + "FROM Status "
				   + "WHERE statustype = ?";
		String eveType = "E";
		return jdbcTemplate.queryForList(sql, eveType);
	}
	
	@GetMapping("/allStatusTypes")
	List<Status> getAllStatusTypes (){
		return statusRepository.findAll();
	}
}
