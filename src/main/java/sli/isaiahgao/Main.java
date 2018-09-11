package sli.isaiahgao;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import sli.isaiahgao.data.HandlerRoomData;
import sli.isaiahgao.data.HandlerUserData;
import sli.isaiahgao.gui.GUI;
import sli.isaiahgao.gui.GUIBase;
import sli.isaiahgao.gui.GUIConfirm;
import sli.isaiahgao.gui.GUIMessage;
import sli.isaiahgao.gui.commands.CommandHandler;
import sli.isaiahgao.io.ActionThread;
import sli.isaiahgao.io.QueueIO;
import sli.isaiahgao.io.SheetsIO;

public final class Main {

    private static ActionThread actionThread;
    private static HandlerRoomData handlerRoom;
    private static HandlerUserData handlerUsers;
    private static CommandHandler handlerCommands;
    private static QueueIO ioQueue;
    private static Main instance;
    private static final String TEST_DB_ID = "1bESGU8NWtu4feYrR7bYHhIib2y_a5iNOJfwiDhcF6vI";
    private static final String TEST_LOG_ID = "1a_0sD8cijhYPAaci_tPuohCgQpfVnvpFD-SPqagUsgI";//"1MdkPLOyFvOqKnkpwT7ML-vzmog7dBqBLK7i8wH-u-Mg";
    
    private static final String REAL_DB_ID = "1bESGU8NWtu4feYrR7bYHhIib2y_a5iNOJfwiDhcF6vI";
    private static final String REAL_LOG_ID = "1MdkPLOyFvOqKnkpwT7ML-vzmog7dBqBLK7i8wH-u-Mg";
    
    public static final Timer TIMER = new Timer();

    public static void main(String[] args) {
        try {
            SheetsIO.refreshService();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        actionThread = new ActionThread();
        instance = new Main();
        handlerRoom = new HandlerRoomData(instance);
        handlerUsers = new HandlerUserData(instance);
        handlerUsers.load();
        ioQueue = new QueueIO();
        instance.base = new GUIBase(instance);

        handlerRoom.loadCurrentRooms();
        //handlerRoom.synchronize();
        
        handlerCommands = new CommandHandler(instance);
        
        TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    SheetsIO.refreshService();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // refresh every half hour
        }, 60l * 30l * 1000l, 60l * 30l * 1000l);
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
    
    public static CommandHandler getCommandHandler() {
        return handlerCommands;
    }
    
    public static QueueIO getIOQueue() {
        return ioQueue;
    }
    
    public Main() {
        
    }
    
    private GUIBase base;
    
    public GUI sendMessage(String s, int xbound) {
        return new GUIMessage(this, Utils.format(s, 12, "verdana", true), null, null, xbound);
    }
    
    public GUI sendMessage(String s) {
        return this.sendMessage(s, null);
    }
    
    public GUI sendMessage(String s, JFrame todispose) {
        return this.sendMessage(s, todispose, null);
    }
    
    public GUI sendMessage(String s, JFrame todispose, Runnable runafter) {
        return new GUIMessage(this, Utils.format(s, 12, "verdana", true), todispose, runafter);
    }
    
    public GUI sendConfirm(String s, JFrame todispose, Runnable runafter) {
        return new GUIConfirm(this, Utils.format(s, 12, "verdana", true), todispose, runafter);
    }
    
    public void sendDisappearingConfirm(String msg, int xbound) {
        final GUI popup = this.sendMessage(msg, xbound);
        
        Main.TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                popup.getFrame().dispose();
            }
        }, 3000L);
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
