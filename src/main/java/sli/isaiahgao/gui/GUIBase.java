package sli.isaiahgao.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicBorders.ButtonBorder;

import sli.isaiahgao.Main;
import sli.isaiahgao.Utils;
import sli.isaiahgao.data.InputCollector;
import sli.isaiahgao.data.UserData;
import sli.isaiahgao.listener.PRButtonListener;

public class GUIBase extends GUI implements ActionListener {
    
    public static void main(String[] args) {
        new GUIBase(new Main());
    }

    private static final long serialVersionUID = 2161473071392557910L;
    private static final Color YELLOW = new Color(255, 251, 225);

    public GUIBase(Main instance) {
        super(instance, "JHUnions Practice Rooms", 2000, 1200, JFrame.EXIT_ON_CLOSE, true);
        this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private InputCollector in;

    //private JButton register;
    //private JButton useOnce;

    private Map<Integer, JButton> buttons;
    private int buttonPressed;

    private JLabel textStepOne;
    private JLabel textStepOneInfo;
    private JLabel textStepTwo;
    private JLabel textStepTwoInfo;
    
    private String curId;
    
    public synchronized String getCurrentId() {
        return curId;
    }

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
        this.curId = id;
        // cut off first and last digit, which are ; and ?
        if (id.charAt(0) == ';') {
            id = id.substring(1);
        }
        if (id.charAt(id.length() - 1) == '?') {
            id = id.substring(0, id.length() - 1);
        }
        
        JButton pressed = this.getPressedButton();
        if (pressed == null) {
            this.instance.sendMessage("Please choose an option on the left, <i>then</i> swipe your JCard!");
            return;
        }
        
        UserData data = Main.getUserHandler().getUserData(id);
        if (data == null) {
            // no user exists, prompt them to register
            new GUIPromptRegister(this.instance, this);
            return;
        }
        
        this.confirmAction(data);
    }
    
    /**
     * Do the action pressed by the current button.
     * @param usd The user.
     */
    public void confirmAction(UserData usd) {
        if (this.getPressedButtonID() == 0) {
            return;
        }
        
        // handle special buttons
        // this one is unused
        if (this.getPressedButtonID() == -1) {
            if (Main.getUserHandler().getUserData(this.getCurrentId()) == null) {
                this.instance.sendMessage("You can't remove your info from our database\\nbecause you're not registered with JHUnions!");
                this.setPressedButton(null);
                return;
            }
            
            // send confirmation about unregistering
            this.instance.sendConfirm("Are you sure you want to unregister?", null, new Runnable() {
                @Override
                public void run() {
                    Main.getUserHandler().removeUserData(GUIBase.this.getCurrentId());
                    GUIBase.this.setPressedButton(null);
                }
            });
            return;
        }
        
        if (this.getPressedButtonID() == -2) {
            // update user info
            if (Main.getUserHandler().getUserData(this.getCurrentId()) == null) {
                // if they're not in system, prompt to register instead
                new GUIAddInfoRegister(this.instance, this.getCurrentId(), this);
            } else {
                new GUIAddInfoUpdate(this.instance, this.getCurrentId(), this);
            }
            this.setPressedButton(null);
            return;
        }
        
        // TODO log room as used
        
        // reset button selection
        this.setPressedButton(null);
    }

    @Override
    protected void setup() {
        // construct components
        this.setBackground(Color.WHITE);

        Dimension dim = this.frame.getSize();
        int w = (int) dim.getWidth();
        int h = (int) dim.getHeight();
        // anchor point for buttons
        int bsy = h / 4;

        int buttonHeightOffset = (h - bsy - 50) / 6;
        int buttonWidth = w / 4 - 10;
        int buttonHeight = buttonHeightOffset - 10;
        
        //x, y, width, height
        this.addPracticeRoomButton(-2, "Update My Info", 60, bsy - buttonHeight / 2, buttonWidth * 2 + 10, buttonHeight / 2 - 5);
        
        this.addPracticeRoomButton(109, 60, bsy, buttonWidth, buttonHeight);
        this.addPracticeRoomButton(110, 60, bsy + buttonHeightOffset, buttonWidth, buttonHeight);
        this.addPracticeRoomButton(111, 60, bsy + 2 * buttonHeightOffset, buttonWidth, buttonHeight);
        this.addPracticeRoomButton(112, 60, bsy + 3 * buttonHeightOffset, buttonWidth, buttonHeight);
        this.addPracticeRoomButton(114, 60, bsy + 4 * buttonHeightOffset, buttonWidth, buttonHeight);
        this.addPracticeRoomButton(115, buttonWidth + 70, bsy, buttonWidth, buttonHeight);
        this.addPracticeRoomButton(116, buttonWidth + 70, bsy + buttonHeightOffset, buttonWidth, buttonHeight);
        this.addPracticeRoomButton(117, buttonWidth + 70, bsy + 2 * buttonHeightOffset, buttonWidth, buttonHeight);
        this.addPracticeRoomButton(118, buttonWidth + 70, bsy + 3 * buttonHeightOffset, buttonWidth, buttonHeight);
        this.addPracticeRoomButton(119, buttonWidth + 70, bsy + 4 * buttonHeightOffset, buttonWidth, buttonHeight);

        this.textStepOne = new JLabel(Utils.format("Step ONE:", 36, "arial black"));
        this.textStepOneInfo = new JLabel(Utils.format("Select Room!", 48, "verdana"));
        this.textStepTwo = new JLabel(Utils.format("Step TWO:", 36, "arial black"));
        this.textStepTwoInfo = new JLabel(Utils.format("Swipe J-Card!", 48, "verdana"));

        // adjust size and set layout
        this.setPreferredSize(new Dimension(1000, 600));
        this.setLayout(null);

        // add components
        //this.add(register);
        //this.add(useOnce);
        this.add(textStepOne);
        this.add(textStepOneInfo);
        this.add(textStepTwo);
        this.add(textStepTwoInfo);

        // set component bounds(only needed by Absolute Positioning)
        //this.register.setBounds(525, 210, 180, 85);
        //this.useOnce.setBounds(725, 210, 180, 85);
        this.textStepOne.setBounds(250, 30, 500, 100);
        this.textStepTwo.setBounds(700, 30, 500, 100);
        this.textStepOneInfo.setBounds(250, 90, 250, 200);
        this.textStepTwoInfo.setBounds(750, 90, 250, 200);

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
            private static final long serialVersionUID = 8708730083077254773L;

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
                private static final long serialVersionUID = 1148616746542133372L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("key pressed " + j);
                    in.add(j);
                }
            });
        }
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke('?'), "stop");
        c.getActionMap().put("stop", new AbstractAction() {
            private static final long serialVersionUID = 5914194104905979306L;

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("stopped collection");
                if (!in.isEmpty())
                    scanID(in.toString());
                in.setCollecting(false);
            }
        });
    }
    
    private void addPracticeRoomButton(int id, String title, int x, int y, int width, int height) {
        if (this.buttons == null) {
            this.buttons = new HashMap<>();
        }

        JButton butt = new JButton(title);
        butt.setActionCommand("select_" + id);
        butt.addActionListener(new PRButtonListener(this, butt));
        butt.setBounds(x, y, width, height);
        butt.setName(id + "");
        butt.setBackground(YELLOW);
        butt.setBorder(new ButtonBorder(Color.DARK_GRAY, Color.BLACK, Color.GRAY, Color.WHITE));
        this.add(butt);
        this.buttons.put(id, butt);
        butt.setVisible(true);
    }

    private void addPracticeRoomButton(int roomNo, int x, int y, int width, int height) {
        if (this.buttons == null) {
            this.buttons = new HashMap<>();
        }

        JButton butt = new JButton(getTitle(roomNo));
        butt.setActionCommand("select_" + roomNo);
        butt.addActionListener(new PRButtonListener(this, butt));
        butt.setBounds(x, y, width, height);
        butt.setName(roomNo + "");
        butt.setBackground(PRButtonListener.DESELECTED);
        butt.setBorder(new ButtonBorder(Color.DARK_GRAY, Color.BLACK, Color.GRAY, Color.WHITE));
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
