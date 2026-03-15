package ch.unibas.dmi.dbis.cs108.casono.server.network.handlers;

import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserHandler {

    private final List<String> usernames;

    public UserHandler() {
        this.usernames = new ArrayList<String>();
    }

    public Boolean checkUsername(String username) {
        for (int i = 0; i < this.usernames.size(); i++) {
            if (this.usernames.get(i).equals(username)) {
                return false;
            }
        }
        return true;
    }

    public Pattern usrRex = Pattern.compile("USERNAME=(?<user>\\w+)");

    public String addUsername(String username) {
        Matcher matcher = usrRex.matcher(username);
        if (! matcher.find()) {
            throw new RuntimeException("Cannot parse " +  username);
        }
        String usr = matcher.group("user");
        if (!this.checkUsername(usr)) {
            this.usernames.add(usr);
        } else {
            this.usernames.add(getAltUsername(usr));
        }
        return String.format("USERNAME=%s", usr);
    }

    public void removeUsername(String username) {
        this.usernames.remove(username);
    }

    public String getAltUsername(String username) {
        int count = 0;
        while (true) {
            username = String.format("%s%d", username, ++count);
            if (this.checkUsername(username)) {
                return username;
            }
        }
    }

}
