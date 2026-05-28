package hki2;

import javax.sound.sampled.*;
import java.io.File;

public class Sound {

    private static Clip musicClip;
    private static boolean playing = false;

    // NHẠC NỀN
    public static void playMusic(String path) {

        try {

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(new File(path));

            musicClip = AudioSystem.getClip();

            musicClip.open(audio);

            musicClip.loop(Clip.LOOP_CONTINUOUSLY);

            musicClip.start();

            playing = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SOUND EFFECT
    public static void playEffect(String path) {

        try {

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(new File(path));

            Clip effectClip = AudioSystem.getClip();

            effectClip.open(audio);

            effectClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {

        if (musicClip != null) {

            musicClip.stop();

            playing = false;
        }
    }

    public static void toggleMusic() {

        if (musicClip != null) {

            if (playing) {

                musicClip.stop();

                playing = false;

            } else {

                musicClip.loop(Clip.LOOP_CONTINUOUSLY);

                musicClip.start();

                playing = true;
            }
        }
    }
}