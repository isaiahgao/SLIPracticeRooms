package sli.isaiahgao.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import sli.isaiahgao.Main;
import sli.isaiahgao.Utils;
import sli.isaiahgao.data.FullName;
import sli.isaiahgao.data.InputCollector;
import sli.isaiahgao.data.UserData;
import sli.isaiahgao.listener.JCardScanListener;
import sli.isaiahgao.listener.PRButtonListener;

public class GUIBase extends GUI implements ActionListener {

    private static final long serialVersionUID = 2161473071392557910L;
    private static final Color YELLOW = new Color(255, 251, 225);

    public GUIBase(Main instance) {
        super("JHUnions Practice Rooms", 1000, 600, JFrame.EXIT_ON_CLOSE, true);
        this.instance = instance;
    }

    private Main instance;
    private InputCollector in;

    //private JButton register;
    //private JButton useOnce;

    private Map<Integer, JButton> buttons;
    private int buttonPressed;

    private JLabel jcomp13;
    private JLabel jcomp14;
    private JLabel jcomp15;
    private JLabel jcomp16;

    public synchronized JButton getButtonByID(int num) {
        return this.buttons.get(num);
    }

    public synchronized int getPressedButtonID() {
        return this.buttonPressed;
    }

    public synchronized JButton getPressedButton() {
        return this.buttonPressed == 0 ? null : this.buttons.get(this.buttonPressed);
    }

    public synchronized void setPressedButton(JButton butt) {
        if (this.buttonPressed != 0) {
            JButton curbutt = this.getPressedButton();
            curbutt.setBackground(this.buttonPressed < 0 ? YELLOW : PRButtonListener.DESELECTED);
            curbutt.validate();
        }
        this.buttonPressed = butt == null ? 0 : Integer.parseInt(butt.getName());
    }

    public void scanID(String id) {
        System.out.println(id);
        // cut off first and last digit, which are ; and ?
        if (id.charAt(0) == ';') {
            id = id.substring(1);
        }
        if (id.charAt(id.length() - 1) == '?') {
            id = id.substring(0, id.length() - 1);
        }
        
        // TODO currently a temporary test user data; change later!
        UserData usd = new UserData(id, new FullName("Isaiah", "Gao"), "ygao67", 4692686710l);
        Main.getUserHandler().push(usd);
    }

    @Override
    protected void setup() {
        // construct components
        this.setBackground(Color.WHITE);

        this.addPracticeRoomButton(-1, "Remove My Info", 30, 75, 170, 70);
        this.addPracticeRoomButton(-2, "Update My Info", 210, 75, 170, 70);

        this.addPracticeRoomButton(109, 30, 155, 170, 70);
        this.addPracticeRoomButton(110, 30, 235, 170, 70);
        this.addPracticeRoomButton(111, 30, 315, 170, 70);
        this.addPracticeRoomButton(112, 30, 395, 170, 70);
        this.addPracticeRoomButton(114, 30, 475, 170, 70);
        this.addPracticeRoomButton(115, 210, 155, 170, 70);
        this.addPracticeRoomButton(116, 210, 235, 170, 70);
        this.addPracticeRoomButton(117, 210, 315, 170, 70);
        this.addPracticeRoomButton(118, 210, 395, 170, 70);
        this.addPracticeRoomButton(119, 210, 475, 170, 70);

        this.jcomp13 = new JLabel("Step ONE:");
        this.jcomp14 = new JLabel("Select a Room!");
        this.jcomp15 = new JLabel("Step TWO:");
        this.jcomp16 = new JLabel("Swipe your JCard!");

        // adjust size and set layout
        this.setPreferredSize(new Dimension(1000, 600));
        this.setLayout(null);

        // add components
        //this.add(register);
        //this.add(useOnce);
        this.add(jcomp13);
        this.add(jcomp14);
        this.add(jcomp15);
        this.add(jcomp16);

        // set component bounds(only needed by Absolute Positioning)
        //this.register.setBounds(525, 210, 180, 85);
        //this.useOnce.setBounds(725, 210, 180, 85);
        this.jcomp13.setBounds(100, 45, 100, 25);
        this.jcomp14.setBounds(100, 65, 100, 25);
        this.jcomp15.setBounds(560, 45, 100, 25);
        this.jcomp16.setBounds(595, 165, 235, 50);

        // KeyListener kl = new JCardScanListener(this);
        // this.addKeyListener(kl);
        this.in = new InputCollector();
        this.in.setEnabled(true);
        this.setupKeyListener(this);
        this.setVisible(true);
        this.setFocusable(true);
    }

    private void setupKeyListener(JComponent c) {
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(';'), "start");
        c.getActionMap().put("start", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("started collection");
                in.setCollecting(true);
            }
        });
        for (int i = 0; i < 10; i++) {
            c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(i + ""), i);
            c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(KeyStroke.getKeyStroke((int) Utils.getField(KeyEvent.class, null, "VK_NUMPAD" + i), 0), i);

            final int j = i;
            c.getActionMap().put(i, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("key pressed " + j);
                    in.add(j);
                }
            });
        }
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke('?'), "stop");
        c.getActionMap().put("stop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("stopped collection");
                if (!in.isEmpty())
                    scanID(in.toString());
                in.setCollecting(false);
            }
        });
    }
    
    private void addPracticeRoomButton(int id, String title, int b1, int b2, int b3, int b4) {
        if (this.buttons == null) {
            this.buttons = new HashMap<>();
        }

        JButton butt = new JButton(title);
        butt.setActionCommand("select_" + id);
        butt.addActionListener(new PRButtonListener(this, butt));
        butt.setBounds(b1, b2, b3, b4);
        butt.setName(id + "");
        butt.setBackground(YELLOW);
        this.add(butt);
        this.buttons.put(id, butt);
        butt.setVisible(true);
    }

    private void addPracticeRoomButton(int roomNo, int b1, int b2, int b3, int b4) {
        if (this.buttons == null) {
            this.buttons = new HashMap<>();
        }

        JButton butt = new JButton(getTitle(roomNo));
        butt.setActionCommand("select_" + roomNo);
        butt.addActionListener(new PRButtonListener(this, butt));
        butt.setBounds(b1, b2, b3, b4);
        butt.setName(roomNo + "");
        butt.setBackground(PRButtonListener.DESELECTED);
        this.add(butt);
        this.buttons.put(roomNo, butt);
        butt.setVisible(true);
    }

    private String getTitle(int roomNo) {
        if (roomNo > 111)
            return roomNo + " [PIANO]";
        if (roomNo == 109)
            return roomNo + " [DRUM]";
        return "" + roomNo;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
