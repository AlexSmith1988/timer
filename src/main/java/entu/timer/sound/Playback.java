package entu.timer.sound;

import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.stream.Stream.of;
import static javax.sound.midi.ShortMessage.CONTROL_CHANGE;
import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

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
        final int note = 30;
        of(
                        midiEvent(shortMessage(CONTROL_CHANGE, 0, note), 0),
                        midiEvent(shortMessage(NOTE_ON, note, 127), 0),
                        midiEvent(shortMessage(NOTE_ON, note + 3, 127), 0),
                        midiEvent(shortMessage(NOTE_OFF, note, 127), 4),
                        midiEvent(shortMessage(NOTE_OFF, note + 3, 127), 4))
                .forEach(track::add);

        sequencer.open();
        sequencer.setSequence(seq);
        sequencer.addMetaEventListener(playbackState);

        commonPool()
                .submit(
                        () -> {
                            while (true) {
                                final int queuedPlays = queuedPlaybacks.get();
                                if (queuedPlays > 0 && !playbackState.isPlaying()) {
                                    queuedPlaybacks.decrementAndGet();
                                    start();
                                }

                                sleepABit();
                            }
                        });
    }

    private MidiEvent midiEvent(final ShortMessage shortMessage, final int tick) {
        return new MidiEvent(shortMessage, tick);
    }

    private ShortMessage shortMessage(final int command, final int data1, final int data2)
            throws InvalidMidiDataException {
        return new ShortMessage(command, 0, data1, data2);
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
