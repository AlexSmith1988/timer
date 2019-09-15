package entu.timer.timers;

import static entu.timer.timers.Timer.currentRunId;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

import entu.timer.output.Output;
import entu.timer.persistence.Json;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Timetable {

    private transient Output output;

    private final int version;
    private final Map<Long, List<Timer>> timers;

    public Timetable(Output output) {
        this.output = output;
        final Timetable loaded = Json.deserialize();
        version = loaded.version;
        timers = loaded.timers;
    }

    public <T> Timetable(int version, List<Timer> timers) {
        this.version = version;
        this.timers = new LinkedHashMap<>();
        timers.forEach(this::addRecord);
    }

    public void persist() {
        try {
            Json.serialize(this);
        } catch (IOException e) {
            output.print("Unable to save timetable history: " + e.getMessage());
        }
    }

    void addRecord(final Timer timer) {
        timers.computeIfAbsent(currentRunId(), ignored -> new ArrayList<>());
        timers.get(currentRunId()).add(timer);
    }

    public void print() {
        timers.forEach((runId, timers) -> {
            output.print("Run: " + runId);
            timers.forEach(timer -> output.print(timer.toString()));
        });
    }

    public List<Timer> last(final int amount) {
        final List<Timer> currentRun = getRun();
        final int recordsAmount = currentRun.size();

        int fromIndex = recordsAmount - amount - 1;
        if (fromIndex <= 0) {
            return currentRun;
        }
        return currentRun.subList(fromIndex, recordsAmount);
    }

    public int lastDuration() {
        final List<Timer> run = getRun();
        if (run.isEmpty()) {
            return Timer.NOT_A_DURATION;
        }
        return run.get(run.size() - 1).getDurationSeconds();
    }

    public int lastIncrement() {
        final List<Timer> run = getRun();
        if (run.size() < 2) {
            return lastDuration();
        }
        return run.get(run.size() - 1).getDurationSeconds() - run.get(run.size() - 2)
                .getDurationSeconds();
    }

    public Stream<Timer> get() {
        return timers.entrySet().stream().flatMap(entry -> entry.getValue().stream());
    }

    public List<Timer> getRun() {
        final long runId = currentRunId();
        return timers.computeIfAbsent(runId, ignored -> new ArrayList<>());
    }

    private static boolean today(final Instant instant) {
        return ofInstant(instant, systemDefault())
                .isAfter(LocalDate.now().atStartOfDay());
    }

    public Stream<Timer> getToday() {
        return get().filter(timer -> today(timer.getStart()));
    }

    public int getVersion() {
        return version;
    }

}
