package sli.isaiahgao;

import javax.swing.JFrame;

import sli.isaiahgao.data.HandlerRoomData;
import sli.isaiahgao.data.HandlerUserData;
import sli.isaiahgao.gui.GUIBase;
import sli.isaiahgao.gui.GUIConfirm;
import sli.isaiahgao.gui.GUIMessage;
import sli.isaiahgao.io.ActionThread;
import sli.isaiahgao.io.QueueIO;
import sli.isaiahgao.io.SheetsIO;

public final class Main {

    private static ActionThread actionThread;
    private static HandlerRoomData handlerRoom;
    private static HandlerUserData handlerUsers;
    private static QueueIO ioQueue;
    private static Main instance;
    private static final String TEST_DB_ID = "1bESGU8NWtu4feYrR7bYHhIib2y_a5iNOJfwiDhcF6vI";
    private static final String TEST_LOG_ID = "1a_0sD8cijhYPAaci_tPuohCgQpfVnvpFD-SPqagUsgI";//"1MdkPLOyFvOqKnkpwT7ML-vzmog7dBqBLK7i8wH-u-Mg";
    
    private static final String REAL_DB_ID = "1bESGU8NWtu4feYrR7bYHhIib2y_a5iNOJfwiDhcF6vI";
    private static final String REAL_LOG_ID = "1MdkPLOyFvOqKnkpwT7ML-vzmog7dBqBLK7i8wH-u-Mg";

    public static void main(String[] args) {
        try {
            SheetsIO.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        actionThread = new ActionThread();
        instance = new Main();
        handlerRoom = new HandlerRoomData(instance);
        handlerUsers = new HandlerUserData(instance);
        handlerUsers.load();
        ioQueue = new QueueIO();
        instance.base = new GUIBase(instance);
        
        handlerRoom.synchronize();
    }
    
    public static ActionThread getActionThread() {
        return actionThread;
    }
    
    public static HandlerRoomData getRoomHandler() {
        return handlerRoom;
    }
    
    public static HandlerUserData getUserHandler() {
        return handlerUsers;
    }
    
    public static QueueIO getIOQueue() {
        return ioQueue;
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
        new GUIMessage(this, Utils.format(s, 12, "verdana", true), todispose, runafter);
    }
    
    public void sendConfirm(String s, JFrame todispose, Runnable runafter) {
        new GUIConfirm(this, Utils.format(s, 12, "verdana", true), todispose, runafter);
    }
    
    public GUIBase getBaseGUI() {
        return base;
    }

    public static String getDatabaseURL() {
        return REAL_DB_ID;
    }

    public static String getLogURL() {
        return REAL_LOG_ID;
    }

}
