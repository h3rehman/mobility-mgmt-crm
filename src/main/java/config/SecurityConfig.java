package config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.security.extensions.saml2.config.SAMLConfigurer.saml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;

import services.SAMLUserService;



@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	  @Value("${onelogin.metadata-path}")
	  private String metadataPath;

	  @Value("${onelogin.sp.protocol}")
	  private String spProtocol;

	  @Value("${onelogin.sp.host}")
	  private String spHost;

	  @Value("${onelogin.sp.path}")
	  private String spBashPath;

	  @Value("${onelogin.sp.key-store.file}")
	  private String keyStoreFile;

	  @Value("${onelogin.sp.key-store.password}")
	  private String keyStorePassword;

	  @Value("${onelogin.sp.key-store.alias}")
	  private String keyStoreAlias;


	  @Value("${onelogin.sp.protocol}")
	  private String protocol;

	  @Autowired
	  SAMLUserService samlUserService;
	
		
//	//These URLs pass straight through, no checks
	@Override
	public void configure(WebSecurity web) throws Exception {
	 web.ignoring().mvcMatchers("/css/**", "/images/**", "/javascript/**", "/**/*.{js,html,css}");
	 }

	//Security Policy
	 @Override
	 protected void configure(HttpSecurity http) throws Exception {
	        http
	            .csrf()
	                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
	                .and()
	            .authorizeRequests()
	            	.mvcMatchers("/saml/**").permitAll()
	                .mvcMatchers("/", "/api/user").permitAll()
	                .anyRequest().authenticated()
	        		.and()
        		.apply(saml())
	    	        .userDetailsService(samlUserService)
	    	        .serviceProvider()
	    	          .protocol(spProtocol)
	    	          .hostname(spHost)
	    	          .basePath(spBashPath)
	    	          .keyStore()
	    	            .storeFilePath(keyStoreFile)
	    	            .keyPassword(keyStorePassword)
	    	            .keyname(keyStoreAlias)
	    	          .and()
	    	        .and()
	    	        .identityProvider()
	    	          .metadataFilePath(metadataPath)
	    	        .and()
	    	    .and();
	    }
	 
	 //For Dev Environment:
	 @Bean
	 @Profile("dev")
	 public RequestCache refererRequestCache() {
	        return new HttpSessionRequestCache() {
	            @Override
	            public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
	                String referrer = request.getHeader("referer");
	                if (referrer != null) {
	                    request.getSession().setAttribute("SPRING_SECURITY_SAVED_REQUEST", new SimpleSavedRequest(referrer));
	                }
	            }
	        };
	    }
	 
}
