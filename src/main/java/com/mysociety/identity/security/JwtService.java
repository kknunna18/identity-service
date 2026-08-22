package com.mysociety.identity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {

    private final JwtProperties props;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public JwtService(JwtProperties props) {
        this.props = props;
        try {
            if (props.getPrivateKeyPem() != null && props.getPublicKeyPem() != null) {
                this.privateKey = (RSAPrivateKey) readPrivateKey(props.getPrivateKeyPem());
                this.publicKey = (RSAPublicKey) readPublicKey(props.getPublicKeyPem());
            } else {
                // generate ephemeral keys
                var kp = Keys.keyPairFor(SignatureAlgorithm.RS256);
                this.privateKey = (RSAPrivateKey) kp.getPrivate();
                this.publicKey = (RSAPublicKey) kp.getPublic();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String subject, UUID userId, UUID societyId, List<String> roles, List<String> permissions) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getExpirationMinutes() * 60L);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("societyId", societyId == null ? null : societyId.toString());
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        claims.put("jti", UUID.randomUUID().toString());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        final String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public long getExpirationSeconds() {
        return props.getExpirationMinutes() * 60L;
    }

    private boolean isTokenExpired(String token) {
        Date exp = extractAllClaims(token).getExpiration();
        return exp.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private PrivateKey readPrivateKey(String pem) throws Exception {
        String cleaned = pem.replaceAll("-----BEGIN ([A-Z ]+)-----", "").replaceAll("-----END ([A-Z ]+)-----", "").replaceAll("\r|\n", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey readPublicKey(String pem) throws Exception {
        String cleaned = pem.replaceAll("-----BEGIN ([A-Z ]+)-----", "").replaceAll("-----END ([A-Z ]+)-----", "").replaceAll("\r|\n", "");
        byte[] keyBytes = Base64.getDecoder().decode(cleaned);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
