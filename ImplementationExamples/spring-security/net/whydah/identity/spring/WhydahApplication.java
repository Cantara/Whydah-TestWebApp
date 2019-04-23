package net.whydah.identity.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 *
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@XmlRootElement(name = "application")
public class WhydahApplication {
    private static final Logger log = LoggerFactory.getLogger(WhydahApplication.class);
    private final String defaultRole;
    private final String defaultOrgId;
    private String id;
    private String applicationName;
    private WhydahOrganization organization;
    private ArrayList<String> availableOrgIds = new ArrayList<>();

    public WhydahApplication(String id, String name, String defaultrole, String defaultorgid) {
        this.id = id;
        this.applicationName = name;
        this.defaultRole = defaultrole;
        this.defaultOrgId = defaultorgid;
    }

    @XmlAttribute(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @XmlElement(name = "organization")
    public WhydahOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(WhydahOrganization organization) {
        this.organization = organization;
    }

    public String getDefaultRole() {
        return defaultRole;
    }

    public String getDefaultOrgId() {
        return defaultOrgId;
    }

    public static WhydahApplication fromXml(String applicationXml) {
        log.debug("Build application from xml {}", applicationXml);
        WhydahApplication application = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document doc = documentBuilder.parse(new InputSource(new StringReader(applicationXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();
            String id = (String) xPath.evaluate("/application/appId", doc, XPathConstants.STRING);
            String name = (String) xPath.evaluate("/application/applicationName", doc, XPathConstants.STRING);
            String defaultrole = (String) xPath.evaluate("/application/roleName", doc, XPathConstants.STRING);
            String defaultorgid = (String) xPath.evaluate("/application/roleValue", doc, XPathConstants.STRING);
            NodeList availableOrgIds = (NodeList) xPath.evaluate("/application/availableOrgIds/orgId", doc, XPathConstants.NODESET);


            application = new WhydahApplication(id, name, defaultrole, defaultorgid);
            if (availableOrgIds != null && availableOrgIds.getLength() > 0) {
                for (int i = 0; i < availableOrgIds.getLength(); i++) {
                    Node node = availableOrgIds.item(i);
                    XPathExpression pathExpr = xPath.compile(".");
                    String orgId = (String) pathExpr.evaluate(node, XPathConstants.STRING);
                    log.debug("orgId {}", orgId);
                    application.addAvailableOrgId(orgId);
                }
            }
        } catch (Exception e) {
            log.warn("Could not create an Application from this xml {}", applicationXml, e);
        }
        return application;
    }

    private void addAvailableOrgId(String orgId) {
        if (availableOrgIds == null) {
            availableOrgIds = new ArrayList<String>();
        }
        availableOrgIds.add(orgId);
    }

    @Override
    public String toString() {
        return "WhydahApplication{" +
                "defaultRole='" + defaultRole + '\'' +
                ", defaultOrgId='" + defaultOrgId + '\'' +
                ", id='" + id + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", organization=" + organization +
                ", availableOrgIds=" + availableOrgIds +
                '}';
    }
}
