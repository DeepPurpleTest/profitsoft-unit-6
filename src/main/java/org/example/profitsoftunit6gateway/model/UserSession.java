package org.example.profitsoftunit6gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSession {

	private String id;
	private String email;
	private String name;
	private Instant expiresAt;

	@JsonIgnore
	public boolean isExpired() {
		return expiresAt.isBefore(Instant.now());
	}

}