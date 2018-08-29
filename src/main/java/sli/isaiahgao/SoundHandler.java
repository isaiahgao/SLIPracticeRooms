package sli.isaiahgao;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundHandler {
    
    public enum Sound {
        SIGN_IN,
        SIGN_OUT,
        ERROR,
        REGISTER_SUCCESSFUL,
        REGISTER_UNSUCCESSFUL;
        
        public void play() {
            try {
                InputStream is = new BufferedInputStream(this.getClass().getResourceAsStream("/sounds/" + this.toString() + ".wav"));
                Clip clip = AudioSystem.getClip();
                AudioInputStream ais = AudioSystem.getAudioInputStream(is);
                clip.open(ais);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
