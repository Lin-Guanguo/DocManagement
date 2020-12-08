package docmanagement.guiclient.eventhandler;

import java.nio.file.Path;

@FunctionalInterface
public interface FileHandler {
    void acceptFile(int id, String name, String description, Path path);
}
