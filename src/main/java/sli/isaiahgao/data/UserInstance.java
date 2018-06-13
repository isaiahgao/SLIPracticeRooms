package sli.isaiahgao.data;

import java.util.Date;

/**
 * Represents a user of the practice room.
 */
public class UserInstance {

    public UserInstance(UserData who, int room) {
        this.who = who;
        this.room = room;
        this.timeIn = new Date();
    }

    private UserData who;
    private int room;
    private Date timeIn;

}
