package net.whydah.sso.web;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.web.util.ModelHelper;
import net.whydah.sso.web.util.RequestHelper;
import net.whydah.sso.web.util.SSOHelper;
import net.whydah.sso.web.util.XpathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.QueryParam;
import java.io.IOException;
import java.util.Properties;

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    static String REDIRECT_TO_LOGIN_SERVICE; //"redirect:http://"+getHost()+":" + SSO_PORT + "/ssoHelper/login" + "?redirectURI=http://"+getHost()+":" + MY_PORT + "/test/hello";
    static String REDIRECT_TO_LOGOUT_SERVICE; // = "redirect:http://"+getHost()+":" + SSO_PORT + "/ssoHelper/logout" + "?redirectURI=http://"+getHost()+":" + MY_PORT + "/test/hello";
    static String LOGOUT_SERVICE; // = "http://"+getHost()+":" + SSO_PORT + "/ssoHelper/logoutaction" + "?redirectURI=http://"+getHost()+":" + MY_PORT + "/test/logout";
    private String myUri;
    private String ssoLoginWebappUri;
    private String tokenServiceUri;
    private final SSOHelper ssoHelper;


    public LoginController() {
    	try {
            Properties config = AppConfig.readProperties();
            myUri = config.getProperty("myuri");
            ssoLoginWebappUri = config.getProperty("logonservice");
            tokenServiceUri = config.getProperty("tokenservice");

            if (myUri == null || ssoLoginWebappUri == null || tokenServiceUri == null) {
                log.error("Urls not set correctly. Exiting. myUri={}, ssoLoginWebappUri={}, tokenServiceUri={}", myUri, ssoLoginWebappUri, tokenServiceUri);
                System.exit(1);
            }

	    	REDIRECT_TO_LOGIN_SERVICE = "redirect:" + ssoLoginWebappUri + "login?redirectURI=" + myUri + "hello";
	    	REDIRECT_TO_LOGOUT_SERVICE = "redirect:" + ssoLoginWebappUri + "logout?redirectURI=" + myUri + "hello";
            LOGOUT_SERVICE = ssoLoginWebappUri + "logoutaction?redirectURI=" + myUri + "hello";
            log.debug("REDIRECT_TO_LOGIN_SERVICE: {}", REDIRECT_TO_LOGIN_SERVICE);
            log.debug("REDIRECT_TO_LOGOUT_SERVICE: {}", REDIRECT_TO_LOGOUT_SERVICE);
            log.debug("LOGOUT_SERVICE: {}", LOGOUT_SERVICE);
    	} catch (IOException e) {
			log.error("Unable to read properties from file.");
			throw new RuntimeException("Unable to read properties", e);
		}

        log.info("LoginController initialized ok. myUri={}, ssoLoginWebappUri={}, tokenServiceUri={}", myUri, ssoLoginWebappUri, tokenServiceUri);
        log.info("Try the service at {}", myUri + "hello");
        ssoHelper = new SSOHelper(tokenServiceUri);
    }

    @RequestMapping("/hello")
    public String hello(@QueryParam("userticket") String userticket, HttpServletRequest request, HttpServletResponse response, Model model) {
        String applicationTokenXml = ssoHelper.logonApplication();

        //String userTokenID = request.getParameter(USER_TOKEN_REFERENCE_NAME);
        if (userticket != null && userticket.length() > 3) {
            model.addAttribute(ModelHelper.GREETING, "Resolving user from userticket!\n");
            log.debug("Looking for userticket (URL param):" + userticket);
            String userToken = ssoHelper.getUserTokenByTicket(applicationTokenXml, userticket);

            if (userToken.length() > 10) {
                model.addAttribute(ModelHelper.USERTOKEN, userToken);
                model.addAttribute(ModelHelper.LOGOUTURL, LOGOUT_SERVICE);
                model.addAttribute(ModelHelper.REALNAME, XpathHelper.getRealName(userToken));
                return "hello";

                // handle re-load of page with userticket on URI
            } else if (RequestHelper.hasRightCookie(request)) {
                model.addAttribute(ModelHelper.GREETING, "Resolving user from Cookie!\n");
                String userTokenIDFromCookie = RequestHelper.getUserTokenIdFromCookie(request);
                userToken = ssoHelper.getUserTokenByTokenID(ssoHelper.logonApplication(), userTokenIDFromCookie);
                log.debug("Looking for userTokenID (Cookie):" + userToken);

                if (userToken.length() > 10) {
                    model.addAttribute(ModelHelper.USERTOKEN, userToken);
                    model.addAttribute(ModelHelper.LOGOUTURL, LOGOUT_SERVICE);
                    model.addAttribute(ModelHelper.REALNAME, XpathHelper.getRealName(userToken));
                    return "hello";
                } else {
                    RequestHelper.removeUserTokenCookie(request, response);
                    return REDIRECT_TO_LOGIN_SERVICE;
                }
            }

            return REDIRECT_TO_LOGIN_SERVICE;

        } else if (RequestHelper.hasRightCookie(request)) {
            model.addAttribute(ModelHelper.GREETING, "Resolving user from Cookie!\n");
            String userTokenIDFromCookie = RequestHelper.getUserTokenIdFromCookie(request);
            String userToken = ssoHelper.getUserTokenByTokenID(ssoHelper.logonApplication(), userTokenIDFromCookie);
                log.debug("Looking for userTokenID (Cookie):" + userToken);

                if (userToken.length() > 10) {
                    model.addAttribute(ModelHelper.USERTOKEN, userToken);
                    model.addAttribute(ModelHelper.LOGOUTURL, LOGOUT_SERVICE);
                    model.addAttribute(ModelHelper.REALNAME, XpathHelper.getRealName(userToken));
                    return "hello";
                } else {
                    RequestHelper.removeUserTokenCookie(request, response);
                    return REDIRECT_TO_LOGIN_SERVICE;
                }
            }

        return REDIRECT_TO_LOGIN_SERVICE;
    }

    @RequestMapping("/logout")
    public String logut(HttpServletRequest request, HttpServletResponse response, Model model) {
         return "logout";
    }

    @RequestMapping("/locallogout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        String userTokenId = RequestHelper.getUserTokenIdFromCookie(request);

        Cookie cookie = new Cookie(RequestHelper.USER_TOKEN_REFERENCE_NAME, "localhost");
        cookie.setValue(RequestHelper.USER_TOKEN_REFERENCE_NAME);
        //cookie.setMaxAge(new Long(Long.parseLong(getTokenMaxAge(10000 + ssoHelper.getUserToken())) - System.currentTimeMillis()).intValue()); // set
        cookie.setMaxAge(100000);
        //cookie.setDomain("localhost");
        cookie.setValue("");
        response.addCookie(cookie);
        return REDIRECT_TO_LOGOUT_SERVICE + '&' + RequestHelper.USER_TOKEN_REFERENCE_NAME + '=' + userTokenId;
    }

    @RequestMapping("/action")
    public String action(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute(ModelHelper.GREETING, "Hello world!\n");
        return "action";
    }
}
