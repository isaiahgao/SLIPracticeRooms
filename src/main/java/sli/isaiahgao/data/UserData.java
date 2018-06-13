package sli.isaiahgao.data;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Represents a user who has registered with the practice rooms.
 */
public class UserData {
    
    public static UserData fromSheetsRow(List<Object> values) {
        UserData dat = new UserData();
        dat.hopkinsID = (String) values.get(0);
        dat.name = new FullName((String) values.get(1), (String) values.get(2));
        dat.jhed = (String) values.get(3);
        dat.phone = Long.parseLong((String) values.get(4));
        return dat;
    }
    
    private UserData() {
        
    }

    public UserData(String hopkinsID, FullName name, String jhed, long phone) {
        this.hopkinsID = hopkinsID;
        this.name = name;
        this.jhed = jhed;
        this.phone = phone;
    }

    private String hopkinsID;
    private String jhed;
    private FullName name;
    private long phone;
    
    public String getHopkinsID() {
        return hopkinsID;
    }

    public String getJhed() {
        return jhed;
    }

    public FullName getName() {
        return name;
    }

    public long getPhone() {
        return phone;
    }
    
    @Override
    public String toString() {
        return hopkinsID + "\t" + jhed + "\t" + name.toString() + "\t" + phone;
    }

    public List<Object> toObjectList() {
        return Lists.newArrayList(hopkinsID, name.getFirstName(), name.getLastName(), jhed, phone);
    }

}
