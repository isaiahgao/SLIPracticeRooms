package sli.isaiahgao.gui;

import java.awt.event.ActionEvent;

import sli.isaiahgao.Main;
import sli.isaiahgao.Utils;
import sli.isaiahgao.data.FullName;
import sli.isaiahgao.data.UserData;

public class GUIAddInfoUseOnce extends GUIAddInfo {

    private static final long serialVersionUID = -8885159911533375810L;

    public GUIAddInfoUseOnce(Main instance, String userId, GUIBase base) {
        super(instance, base, "Use Once", userId, Utils.format("Please enter your information below.<br>Your information will not be saved,<br>and you will have to fill out this form<br>each time you return in the future.", 12, "verdana", true), "OK");
    }
    
    @Override
    protected void setup() {
        super.setup();
        this.textTitle.setBounds(20, 30, 340, 100);
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
                    new GUIAcceptPolicy(this.instance, this, usd, false);
                    /*this.instance.sendMessage("You have elected to use the practice room once!", this.frame, new Runnable() {
                        @Override
                        public void run() {
                            GUIAddInfoUseOnce.this.base.confirmAction(usd);
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
