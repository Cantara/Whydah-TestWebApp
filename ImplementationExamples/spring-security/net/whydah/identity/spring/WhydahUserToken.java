package net.whydah.identity.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;

/*
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class WhydahUserToken {
    private static final Logger log = LoggerFactory.getLogger(WhydahUserToken.class);

    private String id;
    private String uid;
    private Integer securitylevel;
    private String personRef;
    private String firstname;
    private String lastname;
    private String email;
    private Date timestamp;
    private Long lifespan;
    private String issuer;

    private ArrayList<WhydahApplication> applications = new ArrayList<>();
    private String userName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getSecuritylevel() {
        return securitylevel;
    }

    public void setSecuritylevel(Integer securitylevel) {
        this.securitylevel = securitylevel;
    }

    public String getPersonRef() {
        return personRef;
    }

    public void setPersonRef(String personRef) {
        this.personRef = personRef;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getLifespan() {
        return lifespan;
    }

    public void setLifespan(Long lifespan) {
        this.lifespan = lifespan;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<WhydahApplication> getApplications() {
        return applications;
    }


    /**
     * @param logonResult xml similar to
     *                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     *                    <token xmlns:ns2="http://www.w3.org/1999/xhtml" id="79fa7c52-2f82-4e72-b4fa-1b99fac56975">
     *                    <uid>per.testesen@example.com</uid>
     *                    <timestamp>1409131822756</timestamp>
     *                    <lifespan>3600000</lifespan>
     *                    <issuer>http://<host>:<port>/usertokenservice/usertoken/e0287c65a5c9300c476b34edd0446778/getusertokenbytokenid</issuer>
     *                    <securitylevel>1</securitylevel>
     *                    <username>eduardor</username>
     *                    <firstname>Eduardo</firstname>
     *                    <lastname>Rodrigez</lastname>
     *                    <email>per.testesen@example.com</email>
     *                    <personRef></personRef>
     *                    <application ID="21">
     *                    <applicationName>Your Application</applicationName>
     *                    <organizationName></organizationName>
     *                    <role name="ROLE_ADMIN" value="1"/>
     *                    </application>
     *                    <p>
     *                    <ns2:link type="application/xml" href="/79fa7c52-2f82-4e72-b4fa-1b99fac56975" rel="self"/>
     *                    <hash type="MD5">a1db3f3ffe95974ce5ac953441f2621f</hash>
     *                    </usertoken>
     * @return
     */
    public static WhydahUserToken fromXml(String logonResult) {
        log.trace("Try to build xml from {}", logonResult);

        WhydahUserToken token = new WhydahUserToken();
        try {
            XMLHelper helper = new XMLHelper(logonResult);
            String tokenId = helper.findString("/usertoken/@id");// (String) xPath.evaluate("/usertoken/@id", doc, XPathConstants.STRING);
            String uid = helper.findString("/usertoken/uid");
            String issuer = helper.findString("/usertoken/issuer");
            Long securitylevel = helper.findLong("/usertoken/securitylevel");
            String username = helper.findString("/usertoken/username");
            String firstname = helper.findString("/usertoken/firstname");
            String lastname = helper.findString("/usertoken/lastname");
            String email = helper.findString("/usertoken/email");
            String personRef = helper.findString("/usertoken/personRef");
            Long timestamp = helper.findLong("/usertoken/timestamp");
            Long lifespan = helper.findLong("/usertoken/lifespan");
            token.setId(tokenId);
            token.setUid(uid);
            token.setIssuer(issuer);
            if (securitylevel != null) {
                token.setSecuritylevel(securitylevel.intValue());
            }
            token.setUserName(username);
            token.setFirstname(firstname);
            token.setLastname(lastname);
            token.setEmail(email);
            token.setPersonRef(personRef);
            if (timestamp != null) {
                token.setTimestamp(new Date(timestamp));
            }
            token.setLifespan(lifespan);

            //Application priveleges
            //NodeList nodeList = helper.findNodes("/usertoken/application");
            NodeList listOfBooks = helper.findByName("application");
            int totalBooks = listOfBooks.getLength();
            log.debug("Total no of books : " + totalBooks);

            for (int i = 0; i < listOfBooks.getLength(); i++) {

                Node firstBookNode = listOfBooks.item(i);
                if (firstBookNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element firstElement = (Element) firstBookNode;
                    String applicationId = firstElement.getAttribute("ID");
                    log.debug("ID :", applicationId);
                    String applicationName = helper.findNodeValue(firstElement, "applicationName");
                    String organizationName = helper.findNodeValue(firstElement, "organizationName");
                    WhydahApplication rcadminApplication = new WhydahApplication(applicationId, applicationName, null, null);
                    WhydahOrganization organization = new WhydahOrganization(applicationId, organizationName);

                    NodeList roles = firstElement.getElementsByTagName("role");
                    for (int r = 0; r < roles.getLength(); r++) {
                        Node roleNode = roles.item(r);
                        if (roleNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element roleElement = (Element) roleNode;
                            String roleName = roleElement.getAttribute("name");
                            String roleValue = roleElement.getAttribute("value");
                            WhydahRole role = new WhydahRole(roleName, roleValue);
                            organization.addRole(role);
                        }
                    }
                    rcadminApplication.setOrganization(organization);
                    token.addApplication(rcadminApplication);
                }
            }


        } catch (Exception e) {
            log.warn("Could not create an WhydahLogonToken from this xml {}", logonResult, e);
        }
        return token;
    }


    private void addApplication(WhydahApplication applicationAccess) {
        if (applicationAccess != null) {
            applications.add(applicationAccess);
        }

    }

    @Override
    public String toString() {
        return "WhydahUserToken{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", securitylevel=" + securitylevel +
                ", personRef='" + personRef + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", timestamp=" + timestamp +
                ", lifespan=" + lifespan +
                ", issuer='" + issuer + '\'' +
                ", applications=" + applications +
                ", userName='" + userName + '\'' +
                '}';
    }
}
