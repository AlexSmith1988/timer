package entu.timer.timers;

import static java.time.Instant.now;

import entu.timer.output.Output;
import entu.timer.sound.Playback;
import java.time.Instant;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimerService {

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            new ScheduledThreadPoolExecutor(1);

    private final Output output;

    private final History history;
    private final Playback playback;

    public TimerService(final Output output, final History history, final Playback playback) {
        this.output = output;
        this.history = history;
        this.playback = playback;
    }

    public void addTimer(final int seconds) {
        final Instant start = now();
        final Record record = new Record(start, seconds);
        history.addRecord(record);
        output.print("Started new timer: " + record);

        scheduledThreadPoolExecutor.schedule(() -> timeout(record), seconds, TimeUnit.SECONDS);
    }

    private void timeout(final Record record) {
        playback.schedule();
        record.finish();
        output.print("Timer done: " + record);
    }
}
