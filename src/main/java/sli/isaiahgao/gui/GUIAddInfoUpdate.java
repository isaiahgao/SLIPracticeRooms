package sli.isaiahgao.gui;

import java.awt.event.ActionEvent;

import sli.isaiahgao.Main;
import sli.isaiahgao.data.FullName;
import sli.isaiahgao.data.UserData;

public class GUIAddInfoUpdate extends GUIAddInfo {

    private static final long serialVersionUID = -888515991153375810L;

    public GUIAddInfoUpdate(Main instance, String userId, GUIBase base) {
        super(instance, base, "Update my Information", userId, "Update my Information", "OK");
        this.postSetup();
    }
    
    @Override
    protected void setup() {
        super.setup();
    }
    
    private void postSetup() {
        UserData usd = Main.getUserHandler().getUserData(this.userId);
        this.promptFirstName.setText(usd.getName().getFirstName());
        this.promptLastName.setText(usd.getName().getLastName());
        this.promptJHED.setText(usd.getJhed());
        this.promptPhoneNumber.setText(Long.toString(usd.getPhone()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("cancel")) {
            this.frame.dispose();
        } else if (e.getActionCommand().equals("ok")) {
            if (this.argsFilled()) {
                // only accept if they've filled all areas
                try {
                    UserData usd = new UserData(userId, new FullName(promptFirstName.getText(), promptLastName.getText()), promptJHED.getText(), Long.parseLong(promptPhoneNumber.getText()));
                    Main.getUserHandler().push(usd);
                    // TODO call add function from base
                    this.instance.sendMessage("You have successfully updated your information!", this.frame);
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
