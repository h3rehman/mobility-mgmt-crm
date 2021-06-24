package config;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;

import static org.springframework.security.extensions.saml2.config.SAMLConfigurer.saml;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import services.SAMLUserService;



@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
//	  @Value("${onelogin.metadata-path}")
//	  private Resource metadataPath;
	  
	  @Value("${external.metadata-jar-path}")
	  private Resource metadataJarPath;

	  @Value("${onelogin.sp.protocol}")
	  private String spProtocol;

	  @Value("${onelogin.sp.host}")
	  private String spHost;

	  @Value("${onelogin.sp.path}")
	  private String spBasePath;

	  @Value("${onelogin.sp.key-store.file}")
	  private String keyStoreFile;

	  @Value("${onelogin.sp.key-store.password}")
	  private String keyStorePassword;

	  @Value("${onelogin.sp.key-store.alias}")
	  private String keyStoreAlias;
	  
	  @Value("${onelogin.sp.logoutUrl}")
	  private String logoutUrl;

	  @Value("${onelogin.sp.protocol}")
	  private String protocol;
	  
	  final int responseSkew = 28800; //8 hours skew: past+future

	  @Autowired
	  SAMLUserService samlUserService;

	  @Value("${trust.store}")
	  private Resource trustStore;

	  @Value("${trust.store.password}")
	  private String trustStorePassword;
	  
	  @Value("${allowedOrigins}")
	  private List<String> allowedOrigins;
	
		
//	//These URLs pass straight through, no checks
	@Override
	public void configure(WebSecurity web) throws Exception {
	 web.ignoring().mvcMatchers("/css/**", "/images/**", "/javascript/**", "/**/*.{js,html,css}");
	 }

	//Security Policy
	 @Override
	 protected void configure(HttpSecurity http) throws Exception {
		 		   
		 WebSSOProfileConsumerImpl consumerImpl = new WebSSOProfileConsumerImpl();
		 consumerImpl.setResponseSkew(this.responseSkew);

		 //External metadata path (so that the application can also run in a jar). 
		 String absPathOfMetadataFile = metadataJarPath.getURL().toString();
		 System.out.println("########### Resource Metadata getURL: " + metadataJarPath.getURL().toString());
		 
	        http
	        	.cors()
	        	.and()
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
	    	        .webSSOProfileConsumer(consumerImpl)
	    	        .serviceProvider()
	    	          .protocol(spProtocol)
	    	          .hostname(spHost)
	    	          .basePath(spBasePath)
	    	          .keyStore()
	    	            .storeFilePath(keyStoreFile)
	    	            .keyPassword(keyStorePassword)
	    	            .keyname(keyStoreAlias)
	    	          .and()
	    	        .and()
	    	        .identityProvider()
	    	          .metadataFilePath(absPathOfMetadataFile)
	    	        .and()
	    	    .and()
	    	    	.logout()
	    	    	.logoutUrl(logoutUrl)
	    	    	.logoutSuccessUrl("https://stagemmoutreach.rtachicago.org")
	    	    .and();
	    }
	
//	 @Bean
//	 public CorsFilter corsFilter() {
//	   UrlBasedCorsConfigurationSource source = new 
//	   UrlBasedCorsConfigurationSource();
//	   CorsConfiguration config = new CorsConfiguration();
//	   config.setAllowCredentials(true);
//	   config.addAllowedOrigin("*");
//	   config.addAllowedHeader("*");
//	   config.addAllowedMethod("*");
//	   config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost", "https://rtachicago.onelogin.com"));
//	   config.setAllowedHeaders(Arrays.asList("Origin", "Authorization", "Host", "Proxy-Authorization", "Cache-Control", "Content-Type"));
//	   config.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
//	   config.setExposedHeaders(Arrays.asList("Location"));
//	   source.registerCorsConfiguration("/**", config);
//	   return new CorsFilter(source);
//	 }
	 
	 @Bean
	 public CorsConfigurationSource corsConfigurationSource() {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowCredentials(true);
			config.setAllowedOrigins(allowedOrigins);
			config.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
			config.setAllowedHeaders(Arrays.asList("*"));
//			config.setAllowedHeaders(Arrays.asList("Origin", "Authorization", "Accept","Host", "Proxy-Authorization", "Content-Type", "Location", "x-xsrf-token", "x-csrf-token"));
			config.setExposedHeaders(Arrays.asList("Location"));
			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", config);
			return source;
		}
	 
	 //For pointing back to React app after authentication
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
	 

//	    @Bean
//	    RestTemplate restTemplate() throws Exception {
//	        SSLContext sslContext = new SSLContextBuilder()
//	                .loadTrustMaterial(
//	                        trustStore.getURL(),
//	                        trustStorePassword.toCharArray()
//	                ).build();
//        SSLConnectionSocketFactory socketFactory =  new SSLConnectionSocketFactory(sslContext);
//        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
//        System.out.println("######### Custom Rest Template active ###########");
//	        return new RestTemplate(factory);
//	    }
	    
}
