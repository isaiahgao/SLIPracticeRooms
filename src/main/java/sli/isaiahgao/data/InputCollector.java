package sli.isaiahgao.data;

public class InputCollector {

    public InputCollector() {
        this.empty();
    }

    private boolean collecting;
    private boolean enabled;
    private StringBuilder buf;

    public boolean isEnabled() {
        return this.enabled;
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
        }
    }

    public void add(String s) {
        if (!enabled || !collecting)
            return;
        buf.append(s);
    }

    public void add(char c) {
        if (!enabled || !collecting)
            return;
        buf.append(c);
    }

    public void add(int i) {
        if (!enabled || !collecting)
            return;
        buf.append(i);
    }

    public void empty() {
        this.buf = new StringBuilder(30);
    }

    @Override
    public String toString() {
        return this.buf.toString();
    }

    public boolean isEmpty() {
        return this.buf.length() == 0;
    }

}
