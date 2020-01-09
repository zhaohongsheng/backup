package com.spring.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:ftpinfo.properties")
@ConfigurationProperties(prefix="ftpinfo")
public class FtpInfo {
	
	private String host;
	private String username;
	private String passwd;
	private String port;
	private String savepPath;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getSavepPath() {
		return savepPath;
	}
	public void setSavepPath(String savepPath) {
		this.savepPath = savepPath;
	}
	@Override
	public String toString() {
		return "FtpInfo [host=" + host + ", username=" + username + ", passwd=" + passwd + ", port=" + port
				+ ", savepPath=" + savepPath + "]";
	}
	
	
}
