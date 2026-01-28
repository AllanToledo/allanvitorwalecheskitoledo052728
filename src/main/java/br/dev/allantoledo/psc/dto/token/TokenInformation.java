package br.dev.allantoledo.psc.dto.token;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class TokenInformation {
    private String token;
    private ZonedDateTime expiresAt;
    private String user;
    private String role;
}
