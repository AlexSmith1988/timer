package entu.timer.timers;

import java.util.ArrayList;
import java.util.List;

public class History {

    private final List<Record> records = new ArrayList<>();

    void addRecord(final Record record) {
        records.add(record);
    }

    public void print() {
        records.forEach(System.out::println);
    }

    public List<Record> last(final int amount) {
        final int recordsAmount = records.size();

        int fromIndex = recordsAmount - amount - 1;
        if (fromIndex <= 0) {
            return records;
        }
        return records.subList(fromIndex, recordsAmount);
    }
}
