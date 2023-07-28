package id.co.gemastik.web.webtool.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class AccountModel {
	@Id
	@Column(updatable = false, nullable = false)
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	@Column(nullable = false, unique = true, length = 100)
	private String username;
	
	@Column(nullable = false, unique = true)
	private String folder;
	
	@Column
    @JsonIgnore
    private String passwordHash;
	
	public AccountModel(String username, String password) {
		BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

		this.folder = UUID.randomUUID().toString();
		this.username = username;
		this.passwordHash = bCryptEncoder.encode(password);
	}
	
	public String getFolder() {
		return this.folder;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public boolean validatePassword(String oth) {
		BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
		return bCryptEncoder.matches(oth, this.passwordHash);		
	}
}
