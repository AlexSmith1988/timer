package entu.timer.timers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Timetable {

    private final List<Timer> timers = new ArrayList<>();

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
}
