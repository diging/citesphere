package edu.asu.diging.citesphere.core.service.jwt.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.model.jobs.IJob;
import edu.asu.diging.citesphere.core.service.jwt.IJobApiTokenContents;
import edu.asu.diging.citesphere.core.service.jwt.IJwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Service
@PropertySource("classpath:/config.properties")
public class JwtTokenService implements IJwtTokenService {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_api_token_expiration}")
    private long apiTokenExpiration;
    
    @Value("${_token_signing_key}")
    private String tokenSigningKey;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.service.jwt.impl.IJwtTokenService#generateToken(edu.asu.diging.citesphere.core.model.IUser)
     */
    @Override
    public String generateJobApiToken(IJob job) {
        String compactJws = Jwts.builder()
                .setSubject(job.getId())
                .setExpiration(new Date(new Date().getTime() + apiTokenExpiration*1000))
                .signWith(SignatureAlgorithm.HS512, tokenSigningKey)
                .compact();
        
        return compactJws;
    }
    
    /**
     * Method to parse a JWT token. If the token has expired, the returned {@link IJobApiTokenContents}
     * will be set to expired. If the token has an incorrect signature, null is returned.
     */
    @Override
    public IJobApiTokenContents getJobApiTokenContents(String token) {
        IJobApiTokenContents contents = new JobApiTokenContents();
        contents.setExpired(true);
        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey(tokenSigningKey).parseClaimsJws(token);
            Claims claims = jws.getBody(); 
            contents.setJobId(claims.getSubject());
            Date expirationTime = claims.getExpiration();
            if (expirationTime != null) {
                contents.setExpired(expirationTime.before(new Date()));
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.info("Token is expired.", e);
            contents.setExpired(true); 
        } catch (SignatureException e) {
            logger.warn("Token signature not correct.", e);
            return null;
        } 
        
        return contents;
    }
}
