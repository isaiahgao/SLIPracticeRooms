package sli.isaiahgao.io;

public class QueueThread extends Thread {
    
    public QueueThread() {
        this.setDaemon(true);
        this.run();
    }
    
    private QueueTask task;
    
    @Override
    public void run() {
        this.task = new QueueTask();
        this.task.run();
        while (!this.task.wasSuccessful()) {
            this.task.run();
        }
    }

}
