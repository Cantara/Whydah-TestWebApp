package net.whydah.identity.spring;

import ai.quadim.cv.api.web.CvController;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.user.types.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 *
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Component
public class WhydahEntryPoint implements AuthenticationEntryPoint {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CvController cvController;

    @Autowired
    private WhydahAuthenticationProvider authenticationManager;

    @Autowired
    private WhydahService whydahService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        String returnUrl = (request.isSecure() ? "https" : "http") + "://" + request.getServerName();
        if (request.getServerPort() > 0) {
            if ((request.isSecure() && request.getServerPort() != 443) || (!request.isSecure() && request.getServerPort() != 80)) {
                returnUrl += ":" + request.getServerPort();
            }
        }
        returnUrl += request.getRequestURI();

        String ticket = request.getParameter("userticket");
        if (ticket != null && !ticket.trim().isEmpty()) {
            ApplicationToken applicationToken = whydahService.applicationLogon();

            if (applicationToken == null) {
                throw e;
            }
            // application logon
            // 201, 400, 406, 500, 501
            // get user token
            // 201, 400, 404, 406, 415, 500, 501
            // Auth complete

            UserToken userToken = whydahService.getUserToken(applicationToken, ticket);
            String username = userToken.getUserName();
            User user = new UserDetailsImpl(applicationToken, userToken);

            WhydahAuthentication authentication = new WhydahAuthentication(username, user, new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authentication));

            // notify client of response body content type
            response.addHeader("Content-Type", "application/json;charset=UTF-8");
            // set the response status code
            response.setStatus(HttpServletResponse.SC_OK);
            // set up the response body
            // write the response body
            objectMapper.writeValue(response.getOutputStream(), cvController.searchForCV("work", response));
            // commit the response
            response.flushBuffer();//            response.sendRedirect(response.encodeRedirectURL(returnUrl));
            //           response.sendRedirect(returnUrl);
        } else {
            URL ssoLoginUrl = new URL(whydahService.getSsoUrl());
            int port = 443;
            if (!ssoLoginUrl.getProtocol().equalsIgnoreCase("https")) {
                if (ssoLoginUrl.getPort() != -1) {
                    port = ssoLoginUrl.getPort();
                } else {
                    port = 80;
                }
            }
            if (pingHost(ssoLoginUrl.getHost(), port, 5)) {
                // Redirect to whydah.
                response.sendRedirect(response.encodeRedirectURL(String.format("%s/login?redirectURI=%s", whydahService.getSsoUrl(), returnUrl)));
            } else {
                response.sendRedirect(response.encodeRedirectURL("/login"));
            }
        }
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    public class Status {
        private int code;
        private String message;

        public Status(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

}
