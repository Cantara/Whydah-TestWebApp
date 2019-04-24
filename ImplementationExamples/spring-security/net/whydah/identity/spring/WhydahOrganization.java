package net.whydah.identity.spring;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 *
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@XmlRootElement(name = "organization")
public class WhydahOrganization {
    private String id;
    private String organizationName;
    private ArrayList<WhydahRole> roles = new ArrayList<>();

    public WhydahOrganization() {
    }

    public WhydahOrganization(String id, String organizationName) {
        this.id = id;
        this.organizationName = organizationName;
    }

    @XmlAttribute(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    @XmlElement(name = "role")
    public ArrayList<WhydahRole> getRoles() {
        return roles;
    }

    public void setRole(WhydahRole[] role) {
        for (int i = 0; i < role.length; i++) {
            roles.add(role[i]);

        }
    }

    public void addRole(WhydahRole role) {
        if (role != null) {
            roles.add(role);
        }
    }
}
