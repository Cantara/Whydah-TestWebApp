package net.whydah.identity.spring;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 *
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@XmlRootElement(name = "role")
public class WhydahRole {
    private String name, value;

    public WhydahRole() {
    }

    public WhydahRole(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
