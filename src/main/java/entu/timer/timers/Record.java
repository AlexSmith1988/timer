package entu.timer.timers;

import static java.time.Instant.now;
import static java.time.LocalDateTime.ofInstant;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static java.time.format.DateTimeFormatter.ofLocalizedTime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Record {

    private static final DateTimeFormatter dateTimeFormatter =
            ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter timeOnlyFormatter =
            ofLocalizedTime(FormatStyle.SHORT)
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault());

    private final int id;
    private final Instant start;
    private Instant finish;
    private final int durationSeconds;

    Record(int id, Instant start, int durationSeconds) {
        this.id = id;
        this.start = start;
        this.durationSeconds = durationSeconds;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    boolean idInRange(final int from, final int to) {
        return from <= id && id <= to;
    }

    public int id() {
        return id;
    }

    @Override
    public String toString() {
        if (finish == null) {
            return printUnfinished();
        }
        return printFinished();
    }

    private String printUnfinished() {
        return id
                + ": "
                + (durationSeconds - start.until(now(), ChronoUnit.SECONDS))
                + " seconds left;  duration "
                + durationSeconds
                + ";  "
                + dateTimeFormatter.format(start);
    }

    private String printFinished() {
        return id
                + ":  duration "
                + durationSeconds
                + " seconds;  "
                + dateTimeFormatter.format(start)
                + " - "
                + formattedFinish();
    }

    private String formattedFinish() {
        if (sameDate(start, finish)) return timeOnlyFormatter.format(finish);
        return dateTimeFormatter.format(finish);
    }

    private boolean sameDate(final Instant first, final Instant second) {
        return toDate(first).isEqual(toDate(second));
    }

    private LocalDate toDate(final Instant instant) {
        return ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
    }

    void finish() {
        this.finish = now();
    }
}
