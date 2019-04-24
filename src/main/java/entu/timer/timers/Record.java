package entu.timer.timers;

import static java.time.Instant.now;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Record {

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

    public int getDurationSeconds() {
        return durationSeconds;
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
