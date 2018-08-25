package sli.isaiahgao.data;

public class InputCollector {
    
    private static final int ID_LENGTH = 19;

    public InputCollector() {
        this.empty();
    }

    private boolean collecting;
    private boolean enabled;
    private StringBuilder buf;
    private long lastCollected = -1;
    
    private boolean manual;

    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void toggleManual() {
        this.manual = !this.manual;
    }

    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    public boolean isCollecting() {
        return this.collecting;
    }

    public void setCollecting(boolean b) {
        this.collecting = b;
        if (b) {
            this.empty();
        } else {
            this.lastCollected = -1;
        }
    }
    
    private boolean checkTimestamp() {
        if (this.lastCollected > -1 && System.currentTimeMillis() - this.lastCollected > 40) {
            System.out.println("too slow! reset buffer");
            this.empty();
            return false;
        }
        
        this.lastCollected = System.currentTimeMillis();
        return true;
    }

    /**
     * @return whether or not the collector can hold more input.
     */
    public boolean add(String s) {
        if (!enabled)
            return true;
        
        if (!collecting)
            this.setCollecting(true);
        
        if (!this.manual && !this.checkTimestamp())
            return true;
        
        buf.append(s);
        
        if (buf.length() == ID_LENGTH) {
            return false;
        }
        return true;
    }

    public boolean add(char c) {
        return this.add("" + c);
    }

    public boolean add(int i) {
        return this.add("" + i);
    }

    public void empty() {
        this.buf = new StringBuilder(30);
        this.lastCollected = -1;
    }

    @Override
    public String toString() {
        return this.buf.toString();
    }

    public boolean isEmpty() {
        return this.buf.length() == 0;
    }

}
