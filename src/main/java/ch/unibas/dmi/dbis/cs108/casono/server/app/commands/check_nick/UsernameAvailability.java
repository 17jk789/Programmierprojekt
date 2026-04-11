package ch.unibas.dmi.dbis.cs108.casono.server.app.commands.check_nick;

/** Represents the availability status of a username */
enum UsernameAvailability {
    /** Username is available */
    FREE,

    /** Username is already in use */
    TAKEN
}
