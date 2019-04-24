package entu.timer.sound;

import entu.timer.output.Output;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;

class PlaybackState implements MetaEventListener {

    private static final int PLAYBACK_ENDED = 47;

    private final Output output;

    private volatile boolean playing;

    PlaybackState(final Output output) {
        this.output = output;
    }

    @Override
    public void meta(MetaMessage meta) {
        final int metaEventType = meta.getType();
        if (metaEventType == PLAYBACK_ENDED) {
            playing = false;
        } else {
            output.print("Unknown meta event type during playback: " + metaEventType);
        }
    }

    void startedToPlay() {
        playing = true;
    }

    boolean isPlaying() {
        return playing;
    }
}
