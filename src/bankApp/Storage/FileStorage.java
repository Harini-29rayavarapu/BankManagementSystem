
package bankApp.Storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private final Path dataDirectory;

    public FileStorage(String directoryName) throws IOException {
        dataDirectory = Paths.get(directoryName);
        Files.createDirectories(dataDirectory);
    }

    public List<String> readLines(String fileName) throws IOException {
        Path filePath = dataDirectory.resolve(fileName);

        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        return Files.readAllLines(filePath, StandardCharsets.UTF_8);
    }

    public void writeLines(String fileName, List<String> lines)
            throws IOException {
        Path filePath = dataDirectory.resolve(fileName);

        Files.write(
            filePath,
            lines,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public void appendLine(String fileName, String line)
            throws IOException {
        Path filePath = dataDirectory.resolve(fileName);

        Files.writeString(
            filePath,
            line + System.lineSeparator(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
    }
}
