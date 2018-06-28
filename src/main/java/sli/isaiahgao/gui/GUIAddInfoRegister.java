package sli.isaiahgao.gui;

import java.awt.event.ActionEvent;

import sli.isaiahgao.Main;
import sli.isaiahgao.data.FullName;
import sli.isaiahgao.data.UserData;

public class GUIAddInfoRegister extends GUIAddInfo {

    private static final long serialVersionUID = -8885159911533375810L;

    public GUIAddInfoRegister(Main instance, String userId, GUIBase base) {
        super(instance, base, "Register", userId, "Register with JHUnions", "Register");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("cancel")) {
            this.frame.dispose();
        } else if (e.getActionCommand().equals("ok")) {
            if (this.argsFilled()) {
                // only accept if they've filled all areas
                try {
                    final UserData usd = new UserData(userId, new FullName(promptFirstName.getText(), promptLastName.getText()), promptJHED.getText(), Long.parseLong(promptPhoneNumber.getText()));
                    Main.getUserHandler().push(usd);
                    this.instance.sendMessage("You have successfully registered with JHUnions!", this.frame, new Runnable() {
                        @Override
                        public void run() {
                            GUIAddInfoRegister.this.base.confirmAction(usd);
                        }
                    });
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
