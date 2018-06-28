package sli.isaiahgao;

import javax.swing.JFrame;

import sli.isaiahgao.data.HandlerRoomData;
import sli.isaiahgao.data.HandlerUserData;
import sli.isaiahgao.gui.GUIBase;
import sli.isaiahgao.gui.GUIConfirm;
import sli.isaiahgao.gui.GUIMessage;
import sli.isaiahgao.io.SheetsIO;

public final class Main {

    private static HandlerRoomData handlerRoom;
    private static HandlerUserData handlerUsers;
    private static Main instance;
    private final String DB_ID = "1bESGU8NWtu4feYrR7bYHhIib2y_a5iNOJfwiDhcF6vI";
    private final String LOG_ID = "";

    public static void main(String[] args) {
        try {
            //SheetsIO.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        instance = new Main();
        //handlerRoom = new HandlerRoomData(instance);
        //handlerUsers = new HandlerUserData(instance);
        //handlerUsers.load();
        instance.base = new GUIBase(instance);
    }
    
    public static HandlerRoomData getRoomHandler() {
        return handlerRoom;
    }
    
    public static HandlerUserData getUserHandler() {
        return handlerUsers;
    }
    
    public Main() {
        
    }
    
    private GUIBase base;
    
    public void sendMessage(String s) {
        this.sendMessage(s, null);
    }
    
    public void sendMessage(String s, JFrame todispose) {
        this.sendMessage(s, todispose, null);
    }
    
    public void sendMessage(String s, JFrame todispose, Runnable runafter) {
        new GUIMessage(this, "<html><center><font size=\"12\" face=\"verdana\"" + s.replace("\\n", "<br>") + "</font></center></html>", todispose, runafter);
    }
    
    public void sendConfirm(String s, JFrame todispose, Runnable runafter) {
        new GUIConfirm(this, "<html><center><font size=\"12\" face=\"verdana\"" + s.replace("\\n", "<br>") + "</font></center></html>", todispose, runafter);
    }
    
    public GUIBase getBaseGUI() {
        return base;
    }

    public String getDatabaseURL() {
        return this.DB_ID;
    }

    public String getLogURL() {
        return this.LOG_ID;
    }

}
