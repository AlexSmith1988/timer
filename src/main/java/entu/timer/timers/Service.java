package entu.timer.timers;

import static java.time.Instant.now;

import entu.timer.output.Output;
import entu.timer.sound.Playback;
import java.time.Instant;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Service {

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            new ScheduledThreadPoolExecutor(1);

    private final Output output;

    private final Timetable timetable;
    private final Playback playback;
    private final Ids ids;

    public Service(
            final Output output,
            final Timetable timetable,
            final Playback playback,
            Ids ids) {
        this.output = output;
        this.timetable = timetable;
        this.playback = playback;
        this.ids = ids;
    }

    public void addTimer(final int seconds, final int previousSeconds) {
        final Instant start = now();
        final Timer timer = new Timer(ids.nextId(), start, seconds, previousSeconds);
        timetable.addRecord(timer);
        output.print("started " + timer);

        scheduledThreadPoolExecutor.schedule(() -> timeout(timer), seconds, TimeUnit.SECONDS);
    }

    private void timeout(final Timer timer) {
        playback.schedule();
        timer.finish();
        output.print("finished " + timer);
    }
}
