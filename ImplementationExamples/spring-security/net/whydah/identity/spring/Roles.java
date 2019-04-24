package net.whydah.identity.spring;

/**
 * A static and application wide definition of the role names used in the application.
 * Loosely based upon code from Gunnar Skjold (Origin AS)
 *
 * @author Gunnar Skjold
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public final class Roles {
    public static final String User = "ROLE_USER";
    public static final String Admin = "ROLE_ADMIN";
    public static final String GlobalAdmin = "ROLE_GLOBAL_ADMIN";
}
