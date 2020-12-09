package docmanagement.guiclient.eventhandler.file;

import java.nio.file.Path;

@FunctionalInterface
public interface FileHandler {
    void acceptFile(int id, String name, String description, Path path);
}
