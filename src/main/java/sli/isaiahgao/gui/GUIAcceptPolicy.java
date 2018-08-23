package sli.isaiahgao.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import sli.isaiahgao.Main;
import sli.isaiahgao.SoundHandler.Sound;
import sli.isaiahgao.data.UserData;

public class GUIAcceptPolicy extends GUI implements ActionListener, WindowListener {

    private static final long serialVersionUID = -8715194535625598073L;
    private static ImageIcon imageicon;
    
    static {
        try {
            imageicon = new ImageIcon(ImageIO.read(
                    GUIAcceptPolicy.class.getResourceAsStream("/agreement.png")
                    ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GUIAcceptPolicy(Main instance, GUIAddInfo parent, UserData user) {
        super(instance, "Practice Room Policy", 713, 1000, JFrame.DISPOSE_ON_CLOSE, true);
        this.usd = user;
        this.parent = parent;
        this.parent.frame.dispose();
        this.frame.setAlwaysOnTop(true);
        this.frame.setBackground(Color.WHITE);
        this.frame.addWindowListener(this);
    }
    
    protected UserData usd;
    protected GUIAddInfo parent;
    protected JLabel image;
    protected JButton accept, decline;

    @Override
    protected void setup() {
        try {
            image = new JLabel(imageicon); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        accept = new JButton("I have read and agree to abide by these policies");
        accept.addActionListener(this);
        accept.setActionCommand("ok");
        accept.setBackground(Color.WHITE);

        decline = new JButton("No way");
        decline.addActionListener(this);
        decline.setActionCommand("no");
        decline.setBackground(Color.WHITE);
        
        setPreferredSize(new Dimension(713, 1000));
        setLayout(null);
        
        add(accept);
        add(decline);
        add(image);
        
        accept.setBounds(10, 945, 450, 45);
        decline.setBounds(470, 945, 200, 45);
        image.setBounds(0, 0, 713, 1000);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("no")) {
            Sound.ERROR.play();
            this.frame.dispose();
        } else if (e.getActionCommand().equals("ok")) {
            try {
                Main.getUserHandler().push(usd);
                this.instance.sendMessage("You have successfully updated your information!", this.frame);
            } catch (Exception ex) {
                this.instance.sendMessage("Invalid info. Please try again.");
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        
    }

    @Override
    public void windowClosing(WindowEvent e) {
        
    }

    @Override
    public void windowClosed(WindowEvent e) {
        this.instance.sendMessage("You cannot use the practice rooms unless you agree to our policies.");
    }

    @Override
    public void windowIconified(WindowEvent e) {
        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        
    }

    @Override
    public void windowActivated(WindowEvent e) {
        
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        
    }

}
