package sli.isaiahgao.data;

import sli.isaiahgao.gui.GUIBase;

public class InputCollector {
    
    private static final int ID_LENGTH = 15;
    private static final int GRAD_ID_LENGTH = 11;

    public InputCollector(GUIBase base) {
        this.empty();
        this.base = base;
    }

    private boolean collecting;
    private boolean enabled;
    private boolean isGradStudent;
    
    private StringBuilder buf;
    private long lastCollected = -1;
    private long lastKeystroke = -1;
    
    private GUIBase base;
    private boolean manual;

    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void toggleManual() {
        this.manual = !this.manual;
        this.base.getManualLabel().setVisible(this.manual);
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
            this.lastCollected = System.currentTimeMillis();
        } else {
            this.lastCollected = -1;
        }
    }

    /**
     * @return whether or not the collector can hold more input.
     */
    public boolean add(String s) {
        if (!enabled)
            return true;
        
        if (!collecting || (!this.manual && System.currentTimeMillis() - this.lastCollected > 100))
            this.setCollecting(true);

        if (this.manual && System.currentTimeMillis() - lastKeystroke < 10) {
            // cancel manual entry if ID is swiped
            this.manual = false;
            this.base.getManualLabel().setVisible(this.manual);
            this.empty();
            this.lastCollected = System.currentTimeMillis();
        }
        
        // check if grad student id
        if (buf.length() == 4 && s.equals("-")) {
            this.isGradStudent = true;
        } else {
            buf.append(s);
        }
        lastKeystroke = System.currentTimeMillis();
        
        if (this.isGradStudent && buf.length() == GRAD_ID_LENGTH) {
            this.buf.delete(GRAD_ID_LENGTH - 1, GRAD_ID_LENGTH);
            return false;
        }
        
        if (buf.length() == ID_LENGTH) {
            buf.append("0");
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
        System.out.println("emptied");
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
