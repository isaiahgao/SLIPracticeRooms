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
    
    public UserInstance(String data) throws IllegalArgumentException {
        try {
            String[] arr = data.split("\t");
            this.who = new UserData(arr[0], new FullName(arr[1], arr[2]), arr[3], Long.parseLong(arr[4]));
            this.room = Integer.parseInt(arr[5]);
            this.timeIn = new Date(Long.parseLong(arr[6]));
            this.line = Integer.parseInt(arr[7]);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
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
    
    @Override
    public String toString() {
        return this.who.toString() + "\t" + this.room + "\t" + this.timeIn.getTime() + "\t" + this.getLine();
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
                "I agree");
    }

}
