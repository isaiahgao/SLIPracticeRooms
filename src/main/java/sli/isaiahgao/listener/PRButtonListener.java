package sli.isaiahgao.listener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import sli.isaiahgao.gui.GUIBase;

public class PRButtonListener implements ActionListener {

    public static final Color SELECTED = new Color(159, 255, 165);
    public static final Color DESELECTED = Color.WHITE;

    public PRButtonListener(GUIBase parent, JButton butt) {
        this.parent = parent;
        this.butt = butt;
    }

    private GUIBase parent;
    private JButton butt;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.butt.isSelected()) {
            this.parent.setPressedButton(null);
            this.butt.setSelected(false);
            //this.butt.setBackground(DESELECTED);
            this.butt.validate();
            return;
        }

        this.butt.setSelected(true);
        //this.butt.setBackground(SELECTED);
        this.butt.validate();
        this.parent.setPressedButton(this.butt);
    }

}
