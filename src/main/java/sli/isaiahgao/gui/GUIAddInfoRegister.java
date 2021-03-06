package sli.isaiahgao.gui;

import java.awt.event.ActionEvent;

import sli.isaiahgao.Main;
import sli.isaiahgao.Utils;
import sli.isaiahgao.data.FullName;
import sli.isaiahgao.data.UserData;

public class GUIAddInfoRegister extends GUIAddInfo {
    
    public static void main(String[] args) {
        Main main = new Main();
        new GUIAddInfoRegister(main);
    }

    private static final long serialVersionUID = -8885159911533375810L;

    public GUIAddInfoRegister(Main main) {
        super(main, main.getBaseGUI(), "Register", "123", Utils.format("Register with<br>JHUnions", 24, "Corbel", true), "Confirm");
    }
    
    public GUIAddInfoRegister(Main instance, String userId, GUIBase base) {
        super(instance, base, "Register", userId, Utils.format("Register with<br>JHUnions", 24, "Corbel", true), "Register");
    }
    
    @Override
    protected void setup() {
        super.setup();
        this.textTitle.setBounds(70, 30, 370, 100);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("cancel")) {
            this.frame.dispose();
        } else if (e.getActionCommand().equals("ok")) {
            if (this.argsFilled()) {
                // only accept if they've filled all areas
                if (this.promptPhoneNumber.getText().length() != 10) {
                    this.instance.sendMessage("Invalid phone number.");
                    return;
                }
                
                try {
                    final UserData usd = new UserData(userId, new FullName(promptFirstName.getText(), promptLastName.getText()), promptJHED.getText(), Long.parseLong(promptPhoneNumber.getText()));
                    new GUIAcceptPolicy(this.instance, this, usd, true);
                    /*final UserData usd = new UserData(userId, new FullName(promptFirstName.getText(), promptLastName.getText()), promptJHED.getText(), Long.parseLong(promptPhoneNumber.getText()));
                    Main.getUserHandler().push(usd);
                    this.instance.sendMessage("You have successfully registered with JHUnions!", this.frame, new Runnable() {
                        @Override
                        public void run() {
                            GUIAddInfoRegister.this.base.confirmAction(usd);
                        }
                    });*/
                } catch (Exception ex) {
                    this.instance.sendMessage("Invalid info. Please try again.");
                }
                return;
            }
            
            // otherwise send error message
            this.instance.sendMessage("Please fill out all fields.");
        }
    }

}
