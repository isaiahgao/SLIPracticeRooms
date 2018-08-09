package sli.isaiahgao.io;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ActionThread extends Thread {
    
    private List<Action> actions;
    
    public ActionThread() {
        this.setDaemon(true);
        this.actions = new LinkedList<>();
        this.start();
    }
    
    public void addAction(Action a) {
        actions.add(a);
    }
    
    @Override
    public void run() {
        while (true) {
            while (this.actions.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            for (Iterator<Action> ait = this.actions.iterator(); ait.hasNext();) {
                Action a = ait.next();
                try {
                    a.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ait.remove();
            }
        }
    }

}
