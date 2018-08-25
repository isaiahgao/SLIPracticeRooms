package sli.isaiahgao;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundHandler {
    
    public enum Sound {
        SIGN_IN(),
        SIGN_OUT(),
        ERROR();
        
        private Sound() {
            try {
                InputStream is = new BufferedInputStream(this.getClass().getResourceAsStream("/sounds/" + this.toString() + ".wav"));
                this.clip = AudioSystem.getClip();
                this.ais = AudioSystem.getAudioInputStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private AudioInputStream ais;
        private Clip clip;
        
        public void play() {
            /*try {
                this.clip.open(ais);
                this.clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }

}
