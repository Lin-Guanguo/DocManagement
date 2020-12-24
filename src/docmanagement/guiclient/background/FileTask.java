package docmanagement.guiclient.background;

import javax.swing.*;

public abstract class FileTask extends SwingWorker<Void, Void> {
    private final String name;

    protected FileTask(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
