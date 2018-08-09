package sli.isaiahgao.io;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sli.isaiahgao.Main;

public class QueueIO {
    
    // queue to hold requests that didnt go through due to connection issues
    private Map<IODestination, List<Action>> requestMap;
    private QueueThread qthread;
    
    public enum IODestination {
        USER_DATA(Main.getDatabaseURL()),
        LOG(Main.getLogURL());
        
        private IODestination(String link) {
            this.link = link;
        }
        
        private String link;
    }
    
    public QueueIO() {
        this.requestMap = new HashMap<>();
        for (IODestination i : IODestination.values()) {
            this.requestMap.put(i, new LinkedList<>());
        }
    }
    
    public synchronized void pushRequest(IODestination dest, Action req) {
        this.requestMap.get(dest).add(req);
        
        if (this.qthread == null) {
            this.qthread = new QueueThread();
        }
    }
    
    public synchronized void pushAll() throws Exception {
        
        // if successful, clear the request map
    }

}
