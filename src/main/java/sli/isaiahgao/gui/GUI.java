package sli.isaiahgao.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import sli.isaiahgao.Main;

public abstract class GUI extends JPanel {

    private static final long serialVersionUID = -1801320926619229597L;

    public GUI(Main instance, String title, int width, int height, int exitOp, boolean visible) {
        this.instance = instance;
        
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(exitOp);
        this.frame.setTitle(title);
        this.frame.setSize(width, height);
        this.setup();
        this.frame.setVisible(visible);
        this.frame.getContentPane().add(this);
        this.frame.pack();
        this.displayGUI();
    }

    protected JFrame frame;
    protected boolean isDisplayed;
    protected Main instance;

    protected abstract void setup();

    public void displayGUI() {
        this.isDisplayed = true;
        this.frame.setVisible(true);
    }

    public void hideGUI() {
        this.frame.setVisible(false);
        this.isDisplayed = false;
    }

}
