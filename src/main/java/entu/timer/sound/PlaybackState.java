package entu.timer.sound;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;

class PlaybackState implements MetaEventListener {

    private static final int PLAYBACK_ENDED = 47;

    private volatile boolean playing;

    @Override
    public void meta(MetaMessage meta) {
        final int metaEventType = meta.getType();
        if (metaEventType == PLAYBACK_ENDED) {
            playing = false;
        } else {
            System.out.println("Unknown meta event type during playback: " + metaEventType);
        }
    }

    void startedToPlay() {
        playing = true;
    }

    boolean isPlaying() {
        return playing;
    }
}
