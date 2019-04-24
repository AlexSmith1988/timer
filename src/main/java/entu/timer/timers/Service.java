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

    private final History history;
    private final Playback playback;
    private final IdManager idManager;

    public Service(
            final Output output,
            final History history,
            final Playback playback,
            IdManager idManager) {
        this.output = output;
        this.history = history;
        this.playback = playback;
        this.idManager = idManager;
    }

    public void addTimer(final int seconds) {
        final Instant start = now();
        final Record record = new Record(idManager.nextId(), start, seconds);
        history.addRecord(record);
        output.print("started " + record);

        scheduledThreadPoolExecutor.schedule(() -> timeout(record), seconds, TimeUnit.SECONDS);
    }

    private void timeout(final Record record) {
        playback.schedule();
        record.finish();
        output.print("finished " + record);
    }
}
