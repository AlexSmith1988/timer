package entu.timer.timers;

import static java.time.Instant.now;

import entu.timer.output.Output;
import entu.timer.sound.Playback;
import java.time.Duration;
import java.time.Instant;

public class StopWatch {

    private final Output output;
    private final Timetable timetable;
    private final Ids ids;
    private final Playback playback;

    private Instant start;

    public StopWatch(Output output, Timetable timetable, Ids ids,
            Playback playback) {
        this.output = output;
        this.timetable = timetable;
        this.ids = ids;
        this.playback = playback;
    }

    public void start() {
        if (start != null) {
            stop();
        }
        start = now();
    }

    public void stop() {
        final Instant now = now();
        final Timer timer = new Timer(ids.nextId(), start,
                (int) Duration.between(start, now).getSeconds(),
                timetable.lastDuration()).finish();
        timetable.addRecord(timer);
        playback.schedule();
        output.print("stopwatch round: " + timer);

    }

}
