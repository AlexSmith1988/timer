package entu.timer.timers;

import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

import entu.timer.output.Output;
import entu.timer.persistence.Json;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Timetable {

    private final List<Timer> timers = new ArrayList<>();
    private Output output;

    public Timetable(Output output) {
        this.output = output;
        try {
            timers.addAll(Json.deserialize());
        } catch (IOException e) {
            output.print("Unable to load timetable history: " + e.getMessage());
        }
    }

    public void persist() {
        try {
            Json.serialize(timers);
        } catch (IOException e) {
            output.print("Unable to save timetable history: " + e.getMessage());
        }
    }

    void addRecord(final Timer timer) {
        timers.add(timer);
    }

    public void print() {
        timers.forEach(System.out::println);
    }

    public List<Timer> last(final int amount) {
        final int recordsAmount = timers.size();

        int fromIndex = recordsAmount - amount - 1;
        if (fromIndex <= 0) {
            return timers;
        }
        return timers.subList(fromIndex, recordsAmount);
    }

    public int lastDuration() {
        if (timers.isEmpty()) {
            return Timer.NOT_A_DURATION;
        }
        return timers.get(timers.size() - 1).getDurationSeconds();
    }

    public int lastIncrement() {
        if (timers.size() < 2) {
            return lastDuration();
        }
        return timers.get(timers.size() - 1).getDurationSeconds() - timers.get(timers.size() - 2)
                .getDurationSeconds();
    }

    public Stream<Timer> get(final int fromId, final int toId) {
        return timers.stream()
                .filter(timer -> timer.idInRange(fromId, toId));
    }

    public Stream<Timer> get() {
        return timers.stream();
    }

    public Stream<Timer> getRun() {
        return get().filter(timer -> timer.getRunId() == Timer.currentRunId());
    }

    public static void main(String[] args) {
        System.out.println();
    }

    private static boolean today(final Instant instant) {
        return ofInstant(instant, systemDefault())
                .isAfter(LocalDate.now().atStartOfDay());
    }

    public Stream<Timer> getToday() {
        return get().filter(timer -> today(timer.getStart()));
    }
}
