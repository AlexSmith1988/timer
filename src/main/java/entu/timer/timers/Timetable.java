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

    public Stream<Timer> get(final int fromId, final int toId) {
        return timers.stream()
                .filter(timer -> timer.idInRange(fromId, toId));
    }
}
