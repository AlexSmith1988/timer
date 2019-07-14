package entu.timer.timers;

import static java.time.Instant.now;
import static java.time.LocalDateTime.ofInstant;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static java.time.format.DateTimeFormatter.ofLocalizedTime;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Timer {

    public static final int NOT_A_DURATION = 0;

    public static final DateTimeFormatter DATE_TIME_FORMATTER =
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
    private int previousDuration;

    Timer(final int id, final Instant start, final int durationSeconds,
            final int previousDuration) {
        this.id = id;
        this.start = start;
        this.durationSeconds = durationSeconds;
        this.previousDuration = previousDuration;
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
                + (durationSeconds - start.until(now(), SECONDS))
                + " seconds left" + printDiv() + ";  duration "
                + durationSeconds
                + ";  "
                + DATE_TIME_FORMATTER.format(start);
    }

    private String printDiv() {
        if (previousDuration == NOT_A_DURATION) {
            return "";
        }
        int additive = durationSeconds - previousDuration;
        return " (" + (additive > 0 ? "+" : "") + additive + " " + String
                .format("%.1f", 1.0 * durationSeconds / previousDuration) + ")";
    }

    private String printFinished() {
        return id
                + ":  duration "
                + durationSeconds
                + " seconds" + printDiv() + ";  "
                + DATE_TIME_FORMATTER.format(start)
                + " - "
                + formattedFinish();
    }

    private String formattedFinish() {
        if (sameDate(start, finish)) {
            return timeOnlyFormatter.format(finish);
        }
        return DATE_TIME_FORMATTER.format(finish);
    }

    private boolean sameDate(final Instant first, final Instant second) {
        return toDate(first).isEqual(toDate(second));
    }

    private LocalDate toDate(final Instant instant) {
        return ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
    }

    Timer finish() {
        this.finish = now();
        return this;
    }
}
