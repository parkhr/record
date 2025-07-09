package com.example.demo.common;

import com.example.demo.enums.PermissionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    //TODO 설정파일로 이동
    private final String secretKey = "mysecretkeykeymysecretkeykeymysecretkeykeymysecretkeykeymysecretkeykeymysecretkeykeymysecretkeykeymysecretkeykeymysecretkeykeymysecretkeykey";
    private final long validityInMs = 3600_000;

    public String createToken(String username, List<PermissionType> permissions) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("permissions", permissions);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey)
            .parseClaimsJws(token).getBody().getSubject();
    }

    public List<PermissionType> getPermissions(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();

        Object permissionsObj = claims.get("permissions");
        if (permissionsObj instanceof List<?>) {
            return ((List<?>) permissionsObj).stream()
                .map(Object::toString)
                .map(PermissionType::valueOf)
                .toList();
        }

        return List.of();
    }
}