package docmanagement.guiclient.background;

import javax.swing.*;

public abstract class FileTask extends SwingWorker<Void, Integer> {
    private final String name;

    protected FileTask(String name) {
        this.name = name;
    }

    @Override
    abstract public Void doInBackground() throws Exception;

    public String getName() {
        return name;
    }
}
