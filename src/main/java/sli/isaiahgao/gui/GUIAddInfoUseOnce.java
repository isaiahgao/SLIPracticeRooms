package sli.isaiahgao.gui;

import java.awt.event.ActionEvent;

import sli.isaiahgao.Main;
import sli.isaiahgao.data.FullName;
import sli.isaiahgao.data.UserData;

@Deprecated
public class GUIAddInfoUseOnce extends GUIAddInfo {

    private static final long serialVersionUID = -8885159911533375810L;

    public GUIAddInfoUseOnce(Main instance, String userId, GUIBase base) {
        super(instance, base, "Use Once", userId, "Use the Practice Rooms once", "OK");
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
                    this.instance.sendMessage("You are now authorized to check out a practice room!", this.frame, new Runnable() {
                        @Override
                        public void run() {
                            GUIAddInfoUseOnce.this.base.confirmAction(usd);
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
