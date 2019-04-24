package entu.timer.sound;

import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class Playback {

    private final Sequencer sequencer = MidiSystem.getSequencer();

    private final PlaybackState playbackState = new PlaybackState();

    private AtomicInteger queuedPlaybacks = new AtomicInteger();

    public Playback() throws MidiUnavailableException, InvalidMidiDataException {
        final Sequence seq = new Sequence(Sequence.PPQ, 3);
        final Track track = seq.createTrack();
        int n = 55;
        track.add(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, 0, 0, n), 0));
        track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, n, 127), 0));
        track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, n + 3, 127), 0));
        track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, n, 127), 4));
        track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, n + 3, 127), 4));

        sequencer.open();
        sequencer.setSequence(seq);
        sequencer.addMetaEventListener(playbackState);

        new Thread(
                () -> {
                    while (true) {
                        final int queuedPlays = queuedPlaybacks.get();
                        if (queuedPlays > 0 && !playbackState.isPlaying()) {
                            queuedPlaybacks.decrementAndGet();
                            start();
                        }

                        sleepABit();
                    }
                })
                .start();
    }

    private void sleepABit() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        playbackState.startedToPlay();
        sequencer.setTickPosition(0);
        sequencer.start();
    }

    public void schedule() {
        queuedPlaybacks.incrementAndGet();
    }
}
