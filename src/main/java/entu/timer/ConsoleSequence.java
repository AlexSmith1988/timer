package entu.timer;

import static java.lang.Integer.parseInt;
import static java.time.Instant.now;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class ConsoleSequence {
    public static void main(String[] args)
            throws InvalidMidiDataException, MidiUnavailableException {
        final Playback playback = new Playback();
        final History history = new History();
        final TimerService timerService = new TimerService(history, playback);
        new CommandLineInterface(timerService, history).start();
    }
}

class CommandLineInterface {
    private final Scanner inScanner = new Scanner(System.in);

    private final TimerService timerService;
    private final History history;

    CommandLineInterface(final TimerService timerService, final History history) {
        this.timerService = timerService;
        this.history = history;
    }

    void start() {
        while (true) {
            prompt();
            final String command = inScanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(command)) {
                System.exit(0);
            }

            if ("history".equalsIgnoreCase(command)) {
                history.print();
                continue;
            }

            if (command.charAt(command.length() - 1) == 's' && command.length() > 1) {
                final String secondsDurationStr = command.substring(0, command.length() - 1).trim();
                try {
                    timerService.addTimer(parseInt(secondsDurationStr));
                    continue;
                } catch (NumberFormatException e) {
                    System.out.println(
                            "thought to be timer command, but can't identify as a number of seconds: "
                                    + secondsDurationStr);
                }
            }

            System.out.println("Unable to identify command: " + command);
        }
    }

    private void prompt() {
        System.out.println("history, <N>s - n seconds, exit");
    }
}

class History {
    private final List<Record> records = new ArrayList<>();

    void addRecord(final Record record) {
        records.add(record);
    }

    void print() {
        records.forEach(System.out::println);
    }
}

class Record {
    private static final DateTimeFormatter dateTimeFormatter =
            ofLocalizedDateTime(FormatStyle.LONG)
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault());

    private final Instant start;
    private Instant finish;
    private final int durationSeconds;

    Record(Instant start, int durationSeconds) {
        this.start = start;
        this.durationSeconds = durationSeconds;
    }

    @Override
    public String toString() {
        if (finish != null) {
            return "started "
                    + dateTimeFormatter.format(start)
                    + "  finished "
                    + dateTimeFormatter.format(finish)
                    + "  duration "
                    + durationSeconds
                    + " seconds";
        }
        return "started "
                + dateTimeFormatter.format(start)
                + "  duration "
                + durationSeconds
                + " seconds  left till finish "
                + (durationSeconds - start.until(now(), ChronoUnit.SECONDS)) + " seconds";
    }

    public void finish() {
        this.finish = now();
    }
}

class TimerService {
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            new ScheduledThreadPoolExecutor(1);

    private final History history;
    private final Playback playback;

    TimerService(final History history, final Playback playback) {
        this.history = history;
        this.playback = playback;
    }

    void addTimer(final int seconds) {
        final Instant start = now();
        final Record record = new Record(start, seconds);
        history.addRecord(record);

        scheduledThreadPoolExecutor.schedule(() -> timeout(record), seconds, TimeUnit.SECONDS);
    }

    private void timeout(final Record record) {
        playback.schedule();
        record.finish();
        System.out.println("Timer done: " + record);
    }
}

class Playback {
    private final Sequencer sequencer = MidiSystem.getSequencer();

    private final PlaybackState playbackState = new PlaybackState();

    private AtomicInteger queuedPlaybacks = new AtomicInteger();

    Playback() throws MidiUnavailableException, InvalidMidiDataException {
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

                                sleepASecond();
                            }
                        })
                .start();
    }

    private void sleepASecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        playbackState.startedToPlay();
        sequencer.setTickPosition(0);
        sequencer.start();
    }

    void schedule() {
        queuedPlaybacks.incrementAndGet();
    }
}

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
