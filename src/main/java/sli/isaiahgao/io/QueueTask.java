package sli.isaiahgao.io;

import java.util.TimerTask;

import sli.isaiahgao.Main;

public class QueueTask extends TimerTask {
    
    public QueueTask() {
    }
    
    private boolean successful;

    @Override
    public void run() {
        try {
            Main.getIOQueue().pushAll();
            this.successful = true;
        } catch (Exception e) {
            this.successful = false;
        }
    }
    
    public boolean wasSuccessful() {
        return this.successful;
    }
    
}