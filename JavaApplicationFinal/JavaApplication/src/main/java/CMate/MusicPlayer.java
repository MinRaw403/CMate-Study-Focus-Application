package CMate;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {
    private List<String> musicFilePaths;
    private int currentTrackIndex;
    private Clip clip;

    public MusicPlayer() {
        this.musicFilePaths = new ArrayList<>();
        this.currentTrackIndex = 0;
    }

    public void addTrack(String filePath) {
        musicFilePaths.add(filePath);
    }

    public void play() {
        try {
            String filePath = musicFilePaths.get(currentTrackIndex);
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void nextTrack() {
        stop();
        currentTrackIndex = (currentTrackIndex + 1) % musicFilePaths.size();
        play();
    }

    public void previousTrack() {
        stop();
        currentTrackIndex--;
        if (currentTrackIndex < 0) {
            currentTrackIndex = musicFilePaths.size() - 1;
        }
        play();
    }
}
