package ch.unibas.dmi.dbis.cs108.casono.server.domain.user;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link UserFactory} and {@link UserRegistry} name assignment
 * behavior.
 */
public class UserFactoryTest {

    // Logger removed as per patch requirement

    @Test
    public void createUsersWithDuplicateDesiredNamesAssignsUniqueNames() {
        UserRegistry registry = new UserRegistry();
        UserFactory factory = new UserFactory(registry);

        SessionId s1 = new SessionId();
        SessionId s2 = new SessionId();
        SessionId s3 = new SessionId();

        // Create first user with desired name "Alice"
        User u1 = factory.create("Alice", s1);
        // Create second user requesting the same name
        User u2 = factory.create("Alice", s2);
        // Create third user requesting the same name again
        User u3 = factory.create("Alice", s3);

        assertNotNull(u1);
        assertNotNull(u2);
        assertNotNull(u3);

        // All assigned names must be distinct
        String n1 = u1.getName();
        String n2 = u2.getName();
        String n3 = u3.getName();

        // assigned names: n1, n2, n3

        assertNotEquals(n1, n2);
        assertNotEquals(n1, n3);
        assertNotEquals(n2, n3);

        // Registry should contain all assigned names
        assertTrue(registry.getByUsername(n1).isPresent());
        assertTrue(registry.getByUsername(n2).isPresent());
        assertTrue(registry.getByUsername(n3).isPresent());
    }

    @Test
    public void createUserWithNullDesiredAssignsPlayerN() {
        UserRegistry registry = new UserRegistry();
        UserFactory factory = new UserFactory(registry);

        SessionId s1 = new SessionId();

        User u = factory.create(null, s1);
        assertNotNull(u);
        String name = u.getName();
        // assigned anonymous name: name
        assertTrue(name.startsWith("player"), "expected automatic name starting with 'player'");
        assertTrue(registry.getByUsername(name).isPresent());
    }
}
