package sli.isaiahgao.io;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ActionThread extends Thread {
    
    private List<Action> actions;
    
    public ActionThread() {
        this.setDaemon(true);
        this.actions = new LinkedList<>();
        this.start();
    }
    
    public synchronized void addAction(Action a) {
        actions.add(a);
    }
    
    @Override
    public synchronized void run() {
        while (true) {
            if (this.actions.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            
            List<Action> la = new LinkedList<>(this.actions);
            this.actions.clear();
            
            for (Action a : la) {
                try {
                    a.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
