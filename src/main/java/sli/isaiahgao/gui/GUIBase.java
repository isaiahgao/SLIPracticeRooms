package sli.isaiahgao.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import sli.isaiahgao.Graphics.PracticeRoomButton;
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

    public GUIBase(Main instance) {
        super(instance, "JHUnions Practice Rooms BETA v0.5", 1280, 1024, JFrame.EXIT_ON_CLOSE, true);
        this.setBackground(new Color(18, 18, 42));
        //this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
    private JLabel isManual;
    private JButton refreshButton;
    
    private long lastSync;
    private String curId;
    
    public JLabel getManualLabel() {
        return this.isManual;
    }
    
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
            curbutt.setSelected(false);
            curbutt.validate();
        }
        this.buttonPressed = butt == null ? 0 : Integer.parseInt(butt.getName());
        this.toggleTextVisibility(this.buttonPressed == 0);
    }
    
    public void setButtonEnabled(int button, boolean enabled) {
        this.buttons.get(button).setEnabled(enabled);
    }

    public void scanID(String id) {
        System.out.println("Scanned ID: " + id);
        this.curId = id;
        
        if (Main.getRoomHandler().usingRoom(id)) {
            // sign out if using a room
            Main.getRoomHandler().scan(Main.getRoomHandler().getUserInstance(id).getUser(), 0);
            this.setPressedButton(null);
            return;
        }
        
        JButton pressed = this.getPressedButton();
        if (pressed == null) {
            this.instance.sendMessage("Please choose a practice room,<br><i>then</i> swipe your JCard!", 65);
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
            System.out.println("curid: " + this.getCurrentId());
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
        
        // sign in
        Main.getRoomHandler().scan(usd, this.buttonPressed);
        
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
        int buttonWidth = w / 2 - 100;
        int buttonHeight = buttonHeightOffset - 10;
        
        // add the refresh button
        /*try {
            this.refreshButton = new JButton(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/refresh.png"))));
            this.refreshButton.setBounds(10, 10, 50, 50);
            this.refreshButton.setBackground(Color.WHITE);
            this.refreshButton.setActionCommand("refresh");
            this.refreshButton.addActionListener(this);
            this.add(this.refreshButton);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        
        //x, y, width, height
        this.addPracticeRoomButton(-2, Utils.format("<font color=\"white\">Update My Info</font>", 16, "Corbel"), 80, bsy - buttonHeight / 2, buttonWidth * 2 + 10, buttonHeight / 2 - 5);
        
        try {
            this.addPracticeRoomButton(109, 80, bsy, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(110, 80, bsy + buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(111, 80, bsy + 2 * buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(112, 80, bsy + 3 * buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(114, 80, bsy + 4 * buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(115, buttonWidth + 90, bsy, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(116, buttonWidth + 90, bsy + buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(117, buttonWidth + 90, bsy + 2 * buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(118, buttonWidth + 90, bsy + 3 * buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(119, buttonWidth + 90, bsy + 4 * buttonHeightOffset, buttonWidth, buttonHeight);
        } catch (Exception e) {
            // shouldnt happen
            throw new RuntimeException(e);
        }

        this.textStepOne = new JLabel(Utils.format("<font color=\"#8796d1\">Step ONE:</font>", 28, "arial black"));
        this.textStepOneInfo = new JLabel(Utils.format("<font color=\"f9faff\">Select Room!</font>", 72, "verdana"));
        this.textStepTwo = new JLabel(Utils.format("<font color=\"#8796d1\">Step TWO:</font>", 28, "arial black"));
        this.textStepTwoInfo = new JLabel(Utils.format("<font color=\"f9faff\">Swipe J-Card!</font>", 72, "verdana"));
        this.isManual = new JLabel("<html><font color=\"white\">MANUAL ID ENTRY</font></html>");

        // adjust size and set layout
        this.setPreferredSize(new Dimension(1280, 1024));
        this.setLayout(null);

        // add components
        //this.add(register);
        //this.add(useOnce);
        this.add(textStepOne);
        this.add(textStepOneInfo);
        this.add(textStepTwo);
        this.add(textStepTwoInfo);
        this.add(this.isManual);
        this.toggleTextVisibility(true);

        // set component bounds(only needed by Absolute Positioning)
        //this.register.setBounds(525, 210, 180, 85);
        //this.useOnce.setBounds(725, 210, 180, 85);
        this.textStepOne.setBounds(w * 3 / 8, 20, 500, 100);
        this.textStepOneInfo.setBounds(w / 4 - 20, 30, 1000, 200);
        
        this.textStepTwo.setBounds(w * 3 / 8, 20, 500, 100);
        this.textStepTwoInfo.setBounds(w / 4 - 40, 30, 1000, 200);
        
        this.isManual.setBounds(w - 150, 0, 150, 20);
        this.isManual.setVisible(false);

        // KeyListener kl = new JCardScanListener(this);
        // this.addKeyListener(kl);
        this.in = new InputCollector(this);
        this.in.setEnabled(true);
        this.setupKeyListener(this);
        this.setVisible(true);
        this.setFocusable(true);
    }
    
    private void toggleTextVisibility(boolean stepOne) {
        this.textStepOne.setVisible(stepOne);
        this.textStepOneInfo.setVisible(stepOne);
        this.textStepTwo.setVisible(!stepOne);
        this.textStepTwoInfo.setVisible(!stepOne);
    }

    private void setupKeyListener(JComponent c) {
        for (int i = 0; i < 10; i++) {
            c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(i + ""), i);
            c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(KeyStroke.getKeyStroke((int) Utils.getField(KeyEvent.class, null, "VK_NUMPAD" + i), 0), i);

            final int j = i;
            c.getActionMap().put(i, new AbstractAction() {
                private static final long serialVersionUID = 1148616746542133372L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!in.add(j)) {
                        GUIBase.this.scanID(in.toString());
                        in.setCollecting(false);
                    }
                }
            });
        }
        
        // manual
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(11 + ""), 11);
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, 0), 11);
        c.getActionMap().put(11, new AbstractAction() {
            private static final long serialVersionUID = 783045519366944315L;

            @Override
            public void actionPerformed(ActionEvent e) {
                in.toggleManual();
            }
        });

        // console
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(12 + ""), 12);
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 12);
        c.getActionMap().put(12, new AbstractAction() {
            private static final long serialVersionUID = 7830455674444315L;

            @Override
            public void actionPerformed(ActionEvent e) {
                new GUIConsole(instance);
            }
        });

        // dash for grad students
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(13 + ""), 13);
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), 13);
        c.getActionMap().put(13, new AbstractAction() {
            private static final long serialVersionUID = 78304556274444315L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!in.add("-")) {
                    GUIBase.this.scanID(in.toString());
                    in.setCollecting(false);
                }
            }
        });
    }
    
    private void addPracticeRoomButton(int id, String title, int x, int y, int width, int height) {
        if (this.buttons == null) {
            this.buttons = new HashMap<>();
        }

        JButton butt = new JButton(title, new ImageIcon(PracticeRoomButton.LONG_NORMAL.getImage()));
        butt.setPressedIcon(new ImageIcon(PracticeRoomButton.LONG_PRESSED.getImage()));
        butt.setSelectedIcon(new ImageIcon(PracticeRoomButton.LONG_SELECTED.getImage()));
        butt.setVerticalTextPosition(JButton.CENTER);
        butt.setHorizontalTextPosition(JButton.CENTER);
        
        butt.setActionCommand("select_" + id);
        butt.addActionListener(new PRButtonListener(this, butt));
        butt.setBounds(x, y, width, height);
        butt.setName(id + "");
        butt.setBorder(BorderFactory.createEmptyBorder());
        butt.setContentAreaFilled(false);
        //butt.setBackground(YELLOW);
        //butt.setBorder(new ButtonBorder(Color.DARK_GRAY, Color.BLACK, Color.GRAY, Color.WHITE));
        this.add(butt);
        this.buttons.put(id, butt);
        butt.setVisible(true);
    }

    private void addPracticeRoomButton(int roomNo, int x, int y, int width, int height) {
        if (this.buttons == null) {
            this.buttons = new HashMap<>();
        }

        JButton butt = new JButton(getTitle(roomNo, null), new ImageIcon(PracticeRoomButton.NORMAL_NORMAL.getImage()));
        butt.setPressedIcon(new ImageIcon(PracticeRoomButton.NORMAL_PRESSED.getImage()));
        butt.setSelectedIcon(new ImageIcon(PracticeRoomButton.NORMAL_SELECTED.getImage()));
        butt.setDisabledIcon(new ImageIcon(PracticeRoomButton.NORMAL_DISABLED.getImage()));
        butt.setVerticalTextPosition(JButton.CENTER);
        butt.setHorizontalTextPosition(JButton.CENTER);
        
        butt.setActionCommand("select_" + roomNo);
        butt.addActionListener(new PRButtonListener(this, butt));
        butt.setBounds(x, y, width, height);
        butt.setName(roomNo + "");
        //butt.setBackground(PRButtonListener.DESELECTED);
        //butt.setBorder(new ButtonBorder(Color.DARK_GRAY, Color.BLACK, Color.GRAY, Color.WHITE));
        butt.setBorder(BorderFactory.createEmptyBorder());
        butt.setContentAreaFilled(false);
        this.add(butt);
        this.buttons.put(roomNo, butt);
        butt.setVisible(true);
    }

    private String getTitle(int roomNo, String time) {
        String s = "" + roomNo;
        if (roomNo > 111)
            s += "\\nPIANO";
        else if (roomNo == 109)
            s += "\\nDRUM";
        
        if (time != null) {
            s += "\\n" + time;
        }
        return Utils.format("<font color=\"white\">" + s + "</font>", 20, "Corbel", true);
    }
    
    public void setTimeForRoom(int room, String time) {
        JButton butt = this.getButtonByID(room);
        butt.setText(this.getTitle(room, time));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("refresh") && System.currentTimeMillis() - this.lastSync > 10000) {
            this.lastSync = System.currentTimeMillis();
            Main.getRoomHandler().synchronize();
        }
    }

}
