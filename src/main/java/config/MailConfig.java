package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

	@Value("${mail.fromEmail}")
	private String fromEmail;
	
	@Value("${mail.fromEmailName}")
	private String fromEmailName;
	
	@Value("${mail.exchangeServer}")
	private String exchangeServer;
	
	@Value("${mail.tlsRequire}")
	private Boolean smtpTLSRequire;

	@Value("${mail.authRequire}")
	private Boolean smtpAuthRequire;
	
	@Value("${mail.port}")
	private String smtpPort;
	
	@Value("${mail.sender.username}")
	private String username;
	
	@Value("${mail.sender.password}")
	private String password;
	
	@Value("${mail.exchangeServerVersion}")
	private String exchangeServerVersion;
	
	MailConfig(){}

	public String getFromEmail() {
		return fromEmail;
	}
	
	public String getFromEmailName() {
		return fromEmailName;
	}
	
	public String getExchangeServer() {
		return exchangeServer;
	}
	public Boolean getSmtpTLSRequire() {
		return smtpTLSRequire;
	}
	public Boolean getSmtpAuthRequire() {
		return smtpAuthRequire;
	}
	public String getSmtpPort() {
		return smtpPort;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getExchangeServerVersion() {
		return exchangeServerVersion;
	}
	
}
