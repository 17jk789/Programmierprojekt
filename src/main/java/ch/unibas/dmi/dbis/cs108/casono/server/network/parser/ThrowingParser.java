package ch.unibas.dmi.dbis.cs108.casono.server.network.parser;

@FunctionalInterface
interface ThrowingParser<T> {
    T parse(String value) throws Exception;
}