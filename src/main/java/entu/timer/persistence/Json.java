package entu.timer.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import entu.timer.timers.Timer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

public final class Json {

    private static final class TimersTypeToken extends TypeToken<List<Timer>> {

    }

    private static Type TIMERS_TYPE = new TimersTypeToken().getType();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void serialize(final List<Timer> timers) throws IOException {
        try (FileWriter writer = TimetableFile.getWriter()) {
            GSON.toJson(timers, writer);
        }
    }

    public static List<Timer> deserialize() throws IOException {
        try (Reader fileReader = TimetableFile.getReader()) {
            return GSON.fromJson(fileReader, TIMERS_TYPE);
        }
    }

    private Json() {
        /* no-op */
    }
}
