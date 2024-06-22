package org.example.profitsoftunit6gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "userSession")
public class UserSession {

	private String id;
	private String email;
	private String name;
	private Instant expiresAt;
	private List<Authorities> authorities;

	@JsonIgnore
	public boolean isExpired() {
		return expiresAt.isBefore(Instant.now());
	}
}