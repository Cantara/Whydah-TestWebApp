package net.whydah.sso.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RequestHelper {
    private static final Logger log = LoggerFactory.getLogger(RequestHelper.class);

    public static final String APPTOKEN = "apptoken";
    public static final String USERTOKENID = "usertokenid";
    public static final String USERTICKET = "userticket";
    public static final String APPLICATIONCREDENTIAL = "applicationcredential";
    public static final String USER_TOKEN_REFERENCE_NAME = "test_whydahusertoken";


    public static String getHost() {
        String host = "localhost";
        try {
            String hostName = InetAddress.getLocalHost().getHostName();

            InetAddress addrs[] = InetAddress.getAllByName(hostName);

            String myIp = null;
            for (InetAddress addr : addrs) {
                printDebugInfo(addr);

                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                    myIp = addr.getHostAddress();
                }
            }
            // System.out.println("\nIP = " + myIp);
            if (myIp != null)
                host = myIp;
        } catch (UnknownHostException e) {
        }
        return host;
    }

    private static void printDebugInfo(InetAddress addr) {
        log.debug("addr.getHostAddress() = " + addr.getHostAddress());
        log.debug("addr.getHostName() = " + addr.getHostName());
        log.debug("addr.isAnyLocalAddress() = " + addr.isAnyLocalAddress());
        log.debug("addr.isLinkLocalAddress() = " + addr.isLinkLocalAddress());
        log.debug("addr.isLoopbackAddress() = " + addr.isLoopbackAddress());
        log.debug("addr.isMulticastAddress() = " + addr.isMulticastAddress());
        log.debug("addr.isSiteLocalAddress() = " + addr.isSiteLocalAddress());
        log.debug("");
    }

    public static void removeUserTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = getUserTokenCookie(request);
        if (cookie != null) {
            cookie.setValue(USER_TOKEN_REFERENCE_NAME);
            cookie.setMaxAge(0);
            cookie.setValue("");
            response.addCookie(cookie);
        }
    }

    public static Cookie getUserTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        System.out.println("=============> header: " + cookies);
        if (cookies == null) {
            return null;
        }

        for (Cookie cooky : cookies) {
            System.out.println("Cookie: " + cooky.getName());
            if (cooky.getName().equalsIgnoreCase(USER_TOKEN_REFERENCE_NAME)) {
                return cooky;
            }
        }
        return null;
    }

    public static boolean hasRightCookie(HttpServletRequest request) {
        return getUserTokenCookie(request) != null;
    }

    public static String getUserTokenIdFromCookie(HttpServletRequest request) {
        Cookie cookie = getUserTokenCookie(request);
        if (cookie != null)
            return cookie.getValue();
        else
            return null;
    }


}
