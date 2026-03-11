package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import java.time.LocalTime;

public class Message {
    private String message;
    public String name;
    public int hourTime;
    public int minuteTime;
    public int game_id = 0;
    public String target = null;

    // For Global Chat

    public  Message(String message, String name) {
        this.message = message;
        this.name = name;
        LocalTime now = LocalTime.now();
        this.hourTime = now.getHour();
        this.minuteTime = now.getMinute();
    }

    // For Lobby Chat

    public  Message(String message, String name, int game_id) {
        this.message = message;
        this.name = name;
        LocalTime now = LocalTime.now();
        this.hourTime = now.getHour();
        this.minuteTime = now.getMinute();
        this.game_id = game_id;
    }

    // For Whisper Chat

    public  Message(String message, String name, int game_id, String target) {
        this.message = message;
        this.name = name;
        LocalTime now = LocalTime.now();
        this.hourTime = now.getHour();
        this.minuteTime = now.getMinute();
        this.game_id = game_id;
        this.target = target;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return String.format("%s: %s", this.name, this.message);
    }

    public void print() {
        System.out.println(this.toString());
    }
}
