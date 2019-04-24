package net.whydah.identity.spring;

import net.whydah.sso.application.types.ApplicationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 *
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@XmlRootElement(name = "token")
public class WhydahLogonToken extends ApplicationToken {
    private static final Logger log = LoggerFactory.getLogger(WhydahLogonToken.class);

}
