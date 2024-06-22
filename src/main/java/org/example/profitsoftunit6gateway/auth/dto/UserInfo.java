package org.example.profitsoftunit6gateway.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.example.profitsoftunit6gateway.model.Authorities;

import java.util.List;

@Getter
@Builder
@Jacksonized
@ToString
public class UserInfo {

	private String email;

	private String name;

	private List<Authorities> authorities;
}
