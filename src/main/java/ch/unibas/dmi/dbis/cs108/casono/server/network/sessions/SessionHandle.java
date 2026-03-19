package ch.unibas.dmi.dbis.cs108.casono.server.network.sessions;

/** The SessionHandle stores the session and the two worker threads associated with the session */
record SessionHandle(Session session, Thread reader, Thread writer) {}
