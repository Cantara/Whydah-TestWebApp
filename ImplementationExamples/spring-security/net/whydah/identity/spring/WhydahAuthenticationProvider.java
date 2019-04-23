package net.whydah.identity.spring;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 *
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Component
public class WhydahAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(WhydahAuthentication.class);
    }
}
