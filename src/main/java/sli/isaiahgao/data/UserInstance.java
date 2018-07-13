package sli.isaiahgao.data;

import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import sli.isaiahgao.Utils;

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
    private int line;
    
    public int getLine() {
        return this.line;
    }
    
    public void setLine(int line) {
        this.line = line;
    }
    
    public UserData getUser() {
        return this.who;
    }
    
    public int getRoom() {
        return this.room;
    }
    
    public Date getTimeIn() {
        return this.timeIn;
    }
    
    public String getSheetName() {
        return Utils.capitalizeFirst(Month.of(this.timeIn.getMonth() + 1).toString()) + " " + Calendar.getInstance().get(Calendar.YEAR);
    }
    
    public List<Object> toObjectList() {
        Date date = new Date();
        String ds = (date.getMonth() + 1) + "/" + date.getDate() + "/" + (date.getYear() + 1900);
        String time = Utils.getTime(date);
        return Lists.newArrayList(ds + " " + time.substring(0, time.length() - 3),
                this.who.getName().getFirstName() + " " + this.who.getName().getLastName(),
                this.who.getJhed() + "@jhu.edu",
                this.who.getPhone(),
                "Room " + this.room,
                time,
                "I agree.");
    }

}
