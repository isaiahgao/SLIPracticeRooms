package sli.isaiahgao;

import sli.isaiahgao.data.HandlerRoomData;
import sli.isaiahgao.data.HandlerUserData;
import sli.isaiahgao.gui.GUIBase;
import sli.isaiahgao.io.SheetsIO;

public class Main {

    private static HandlerRoomData handlerRoom;
    private static HandlerUserData handlerUsers;
    private static Main instance;
    private final String DB_ID = "1bESGU8NWtu4feYrR7bYHhIib2y_a5iNOJfwiDhcF6vI";
    private final String LOG_ID = "";

    public static void main(String[] args) {
        try {
            SheetsIO.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        instance = new Main();
        handlerRoom = new HandlerRoomData(instance);
        handlerUsers = new HandlerUserData(instance);
        handlerUsers.load();
        new GUIBase(instance);
    }
    
    public static HandlerRoomData getRoomHandler() {
        return handlerRoom;
    }
    
    public static HandlerUserData getUserHandler() {
        return handlerUsers;
    }

    public String getDatabaseURL() {
        return this.DB_ID;
    }

    public String getLogURL() {
        return this.LOG_ID;
    }

}
