package ch.unibas.dmi.dbis.cs108.casono.client.network;

/**
 * Result of a successful login request.
 *
 * <p>Holds the username assigned by the server and the server-side id as a string (UUID). The
 * client previously attempted to parse the id as an integer which failed when the server returned a
 * UUID; this class therefore stores the id as a {@link String}.
 */
public class LoginResult {
    private final String username;
    private final String id;

    /**
     * Create a login result.
     *
     * @param username the assigned username
     * @param id the assigned id (UUID string) returned by the server
     */
    public LoginResult(String username, String id) {
        this.username = username;
        this.id = id;
    }

    /**
     * @return the assigned username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the assigned id as returned by the server (UUID string)
     */
    public String getId() {
        return id;
    }
}
