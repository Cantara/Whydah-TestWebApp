package net.whydah.sso.web;

import net.whydah.sso.config.AppConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import java.net.URL;


public class ServerRunner {
    private static final Logger log = LoggerFactory.getLogger(ServerRunner.class);
    public static int PORT_NO = 9990;
    public static String TESTURL = "http://localhost:" + "9990" + "/test/action";
    private Server server;


    public static void main(String[] arguments) throws Exception {
        PORT_NO = getPortNo(AppConfig.readProperties().getProperty("myuri"));
        ServerRunner serverRunner = new ServerRunner();
        serverRunner.start();
        serverRunner.join();
    }

    public ServerRunner() {
        server = new Server(PORT_NO);
        // Jetty 12's ServletContextHandler has no (Server, String) constructor;
        // Jetty 9's attached the handler to the server as a side effect.
        ServletContextHandler context = new ServletContextHandler("/test");
        server.setHandler(context);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:webapp/sso/mvc-config.xml");
        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");
    }


    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public void join() throws InterruptedException {
        server.join();
    }

    private static int getPortNo(String URI) {
        try {
            int portNr = new URL(URI).getPort();
            if(portNr >= 0) {
                return portNr; //Avoiding "-1" if port is not a part of URL
            }
        } catch (Exception ue) {
            log.warn("Error in property configuration of property myuri={}. Reverting to default PORTNO=9990  ", URI);
        }
        return 9990;
    }
}
