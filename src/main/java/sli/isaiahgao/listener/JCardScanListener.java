package sli.isaiahgao.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import sli.isaiahgao.gui.GUIBase;

@Deprecated
/**
 * Keyboard listener implementation of input-getter. Swapped for keybinds.
 */
public class JCardScanListener implements KeyListener {

    public JCardScanListener(GUIBase parent) {
        this.parent = parent;
    }

    private GUIBase parent;

    private long lastTyped = -1L;
    private boolean accepting = false;

    private StringBuilder builder;

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("key typed");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        System.out.println(c);
        if (this.accepting) {
            if (System.currentTimeMillis() - this.lastTyped > 100) {
                // too slow - probably a person typing
                System.out.println("too slow! cleared buffer");
                this.builder = new StringBuilder(30);
                this.accepting = false;
                this.lastTyped = System.currentTimeMillis();
                return;
            }

            System.out.println("appended " + c + ". size: " + this.builder.length());
            this.builder.append(c);

            if (c == '?') {
                this.parent.scanID(this.builder.toString());
                this.builder = new StringBuilder(30);
                this.accepting = false;
                this.lastTyped = System.currentTimeMillis();
                System.out.println("ended builder - " + builder.toString());
            }
            return;
        }

        // IDs are formatted as ;[20 numbers]?
        if (c == ';') {
            this.accepting = true;
            this.lastTyped = System.currentTimeMillis();
            this.builder = new StringBuilder(30);
            this.builder.append(e.getKeyChar());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
