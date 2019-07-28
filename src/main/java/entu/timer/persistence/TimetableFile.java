package entu.timer.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;

final class TimetableFile {

    private static final String FILE_NAME = "timetable.log";

    private static final String FILE_PATH = Paths.get(getDirectoryForPersisting(), FILE_NAME)
            .toString();

    private static String getDirectoryForPersisting() {
        final String classBasedPath = TimetableFile.class.getProtectionDomain().getCodeSource()
                .getLocation()
                .getPath();

        final Path nonUriPath = Paths.get(new File(classBasedPath).getAbsolutePath());

        final File nonUriClassBasedFile = nonUriPath.toFile();
        if (nonUriClassBasedFile.isDirectory()) {
            return goUp(nonUriPath.toString());
        } else {
            return goUp(nonUriClassBasedFile.getParent());
        }
    }

    private static String goUp(final String path) {
        return new File(path).getParentFile().getParentFile().getParentFile().getParent();
    }

    static FileWriter getWriter() throws IOException {
        return new FileWriter(FILE_PATH);
    }

    static Reader getReader() throws FileNotFoundException {
        return new FileReader(FILE_PATH);
    }

    private TimetableFile() {
        /* No-op */
    }
}
