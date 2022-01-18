package config;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import repository.event.presenter.Presenter;
import user.User;



/**
 * 
 * @author Hamood
 * Use this Configuration for anything for now (Both Application and Infrastructure/Databases/DataSource)
 *
 */
@Configuration
@ComponentScan({"controllers", "repository", "services"})
@Controller
//@RequestMapping("/")
public class AppConfig implements WebMvcConfigurer {
	
	/**
	 * Enables the home page which is using server-side rendering of a minimal view.
	 * 
	 * @param registry
	 *            View controller registry. Allows you to register simple mappings
	 *            of URLs to static views (since there is no dynamic content a
	 *            Spring Controller is not required).
	 */
//	@Override
//	public void addViewControllers(ViewControllerRegistry registry) {
//		// Map the root URL to the index template
//		registry.addViewController("/").setViewName("index");
//	}
	
//		@GetMapping
//	  public void addViewControllers(ExpiringUsernameAuthenticationToken userToken, ViewControllerRegistry registry) {
//	    User user = (User) userToken.getPrincipal();
//	    System.out.println("###### First Name: " + user.getFirstName() + "##########");
//	    System.out.println("###### Email: " + user.getEmail() + "##########");
//	    registry.addViewController("/").setViewName("index");
//	  }

	@Autowired
	DataSource dataSource;
	
	@Bean
	public JavaMailSender javaMailSender() {
		return new JavaMailSenderImpl();
	}
	
	@Bean
	public MailConfig mailConfig() {
		return new MailConfig();
	}
		
//	@Bean
//	 public DataSource dataSource(
//    	 @Value("${db.driver}") String driver,
//	     @Value("${db.url}") String url,
//	     @Value("${db.user}") String user,
//	     @Value("${db.password}") String pwd) 
//	{
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//	    dataSource.setDriverClassName(driver);
//	    dataSource.setUrl(url);
//	    dataSource.setUsername(user);
//	    dataSource.setPassword(pwd);
// 
//        return dataSource;
//	 }
	
}
