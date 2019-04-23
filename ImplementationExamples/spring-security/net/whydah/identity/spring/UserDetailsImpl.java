package net.whydah.identity.spring;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of springs {@link UserDetails}
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 *
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class UserDetailsImpl implements User<String>, UserDetails {
    private static final long serialVersionUID = 1L;
    private final String userId;
    private final Integer countryId;
    private final String username, password, salt, firstName, middleName, lastName, fullName;
    private final boolean expired, temporaryPassword, admin;
    private final List<GrantedAuthority> authorities = new ArrayList<>();

    protected UserDetailsImpl() {
        userId = null;
        countryId = null;
        username = password = salt = firstName = middleName = lastName = fullName = null;
        expired = false;
        temporaryPassword = false;
        admin = true;
    }

    public UserDetailsImpl(WhydahLogonToken logonToken, WhydahUserToken userToken) {
        userId = userToken.getId();
        countryId = null; //TODO
        username = userToken.getEmail(); // TODO
        password = salt = null;
        firstName = userToken.getFirstname();
        middleName = null;
        lastName = userToken.getLastname();
        String fullName = "";
        if (firstName != null)
            fullName += firstName;
        if (middleName != null)
            fullName += " " + middleName;
        if (lastName != null)
            fullName += " " + lastName;
        this.fullName = fullName.trim();
        expired = logonToken.isExpired();
        temporaryPassword = false;

        boolean admin = false;
        //List applications the user has access to.
        ArrayList<WhydahApplication> applications = userToken.getApplications();
        if (applications != null) {
            for (WhydahApplication app : applications) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS_USER"));
                for (WhydahRole role : app.getOrganization().getRoles()) {
                    String springRole = "ROLE_" + role.getName().toUpperCase();
                    authorities.add(new SimpleGrantedAuthority(springRole));
                    if (springRole.equals("ROLE_ADMIN")) {
                        admin = true;
                        //     }
                    }
                }
            }
        }

        this.admin = admin;
    }

    //Spring UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(authorities);
    }

    //Spring UserDetails
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the salt used when hashing the password.
     *
     * @return
     */
    public String getSalt() {
        return salt;
    }

    //Spring UserDetails
    @Override
    public String getUsername() {
        return username;
    }

    //Spring UserDetails
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //Spring UserDetails
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //Spring UserDetails
    @Override
    public boolean isCredentialsNonExpired() {
        return !expired;
    }

    //Spring UserDetails
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Indicates if the password is temporary and should be changed
     *
     * @return true if the password is temporary
     */
    public boolean isTemporaryPassword() {
        return temporaryPassword;
    }

    /**
     * Indicates if the user is a global administrator
     *
     * @return true if the user is a global administrator
     */
    @Override
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Provides the internal user id from the entity which is only used as an identifier within the application
     *
     * @return A numeric identifier for the user
     */
    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getMiddleName() {
        return middleName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    /**
     * Users full name formated as: First Middle Last
     *
     * @return The users full name
     */
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getEmail() {
        return username;
    }

    /**
     * The internal numeric identifier for the country that the user has been assigned as a national administrator.
     *
     * @return A numeric identifier for the country
     */
    public Integer getCountryId() {
        return countryId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserDetailsImpl other = (UserDetailsImpl) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
}
