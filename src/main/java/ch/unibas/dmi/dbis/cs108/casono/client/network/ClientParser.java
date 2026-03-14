package ch.unibas.dmi.dbis.cs108.casono.client.network;

public class ClientParser {
    public static final String Seperator = " ";

    enum Protocol {}

    public void parseMessage(String msg) {
        var parts = msg.split(Seperator);

        switch (parts[0]) {
            case :

            case :

            case :

            case :

            default :
                System.out.println("Response " + msg + " is not recognized");
        }


    }
}
