package com.hse.Curriculum.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para manejo de JWT (JSON Web Tokens)
 * Compatible con io.jsonwebtoken:jjwt-api:0.12.5
 */
@Service
public class JwtService {

    // Clave secreta para firmar tokens (debe estar en application.properties)
    @Value("${jwt.secret.key}")
    private String secretKey;

    // Tiempo de expiración del token en milisegundos (por defecto 24 horas)
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;


    /**
     * Devuelve el tiempo de expiración del JWT.
     *
     * @return el tiempo de expiración en milisegundos desde el epoch .
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }
    /**
     * Extrae el username (subject) del token
     *
     * @param token Token JWT
     * @return Username contenido en el token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim específico del token
     *
     * @param token Token JWT
     * @param claimsResolver Función para extraer el claim deseado
     * @param <T> Tipo del claim
     * @return Valor del claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token JWT para el usuario
     *
     * @param userDetails Detalles del usuario
     * @return Token JWT generado
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con claims adicionales
     *
     * @param extraClaims Claims adicionales a incluir en el token
     * @param userDetails Detalles del usuario
     * @return Token JWT generado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Construye el token JWT
     *
     * @param extraClaims Claims adicionales
     * @param userDetails Detalles del usuario
     * @param expiration Tiempo de expiración en milisegundos
     * @return Token JWT construido
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {

        long currentTimeMillis = System.currentTimeMillis();

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(currentTimeMillis))
                .expiration(new Date(currentTimeMillis + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Valida si el token es válido para el usuario
     *
     * @param token Token JWT
     * @param userDetails Detalles del usuario
     * @return true si el token es válido, false en caso contrario
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token ha expirado
     *
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token
     *
     * @param token Token JWT
     * @return Fecha de expiración
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todos los claims del token
     * ACTUALIZADO PARA JWT 0.12.x
     *
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()  // ← CAMBIO: parser() en lugar de parserBuilder()
                .verifyWith(getSignInKey())  // ← CAMBIO: verifyWith() en lugar de setSigningKey()
                .build()
                .parseSignedClaims(token)  // ← CAMBIO: parseSignedClaims() en lugar de parseClaimsJws()
                .getPayload();  // ← CAMBIO: getPayload() en lugar de getBody()
    }

    /**
     * Obtiene la clave de firma para los tokens
     *
     * @return SecretKey para firmar tokens
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token de refresh (con mayor tiempo de expiración)
     *
     * @param userDetails Detalles del usuario
     * @return Token de refresh
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtExpiration * 7); // 7 veces la expiración normal
    }

    /**
     * Extrae la fecha de emisión del token
     *
     * @param token Token JWT
     * @return Fecha de emisión
     */
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Verifica si el token fue emitido antes de una fecha específica
     * Útil para invalidar tokens antiguos después de cambio de contraseña
     *
     * @param token Token JWT
     * @param date Fecha de comparación
     * @return true si el token fue emitido antes de la fecha
     */
    public boolean isTokenIssuedBefore(String token, Date date) {
        return extractIssuedAt(token).before(date);
    }
}