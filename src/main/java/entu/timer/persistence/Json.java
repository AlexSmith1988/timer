package entu.timer.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import entu.timer.timers.Timer;
import entu.timer.timers.Timetable;
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

    public static void serialize(final Timetable timetable) throws IOException {
        try (FileWriter writer = TimetableFile.getWriter()) {
            GSON.toJson(timetable, writer);
        }
    }

    public static Timetable deserialize() {
        try (final Reader fileReader = TimetableFile.getReader()) {
            return GSON.fromJson(fileReader, Timetable.class);
        } catch (JsonSyntaxException jse) {
            try (final Reader fileReader = TimetableFile.getReader()) {
                return new Timetable(0, GSON.fromJson(fileReader, TIMERS_TYPE));
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load history", e);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load history", e);
        }
    }

    private Json() {
        /* no-op */
    }
}
