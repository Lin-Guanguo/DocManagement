package docmanagement.guiclient.frame;

import docmanagement.guiclient.background.FileTask;

import javax.swing.*;
import java.awt.*;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

public class FileProgress extends JPanel {
    public static final int DEFAULT_DISTANCE = 10;

    private final JPanel contentPanel = new JPanel();
    private int taskCount = 0;

    private final java.util.Timer flushTimer = new java.util.Timer();

    private static class runningTask {
        public FileTask task;
        public JProgressBar progressBar;
        public JButton cancelButton;

        public runningTask(FileTask task, JProgressBar progressBar, JButton cancelButton) {
            this.task = task;
            this.progressBar = progressBar;
            this.cancelButton = cancelButton;
        }
    }
    private final ConcurrentLinkedQueue<runningTask> taskList = new ConcurrentLinkedQueue<>();

    public FileProgress(){
        this.setLayout(new GridBagLayout());
        contentPanel.setLayout(new GridBagLayout());
        var scrollPane = new JScrollPane(contentPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane, new GBC().setFill(GridBagConstraints.BOTH));

        flushTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                flush();
            }
        }, 500, 500);
    }

    public void addProgress(FileTask task){
        var progressPanel = new JPanel();
        progressPanel.setLayout(new GridBagLayout());
        contentPanel.add(progressPanel,
                new GBC(0, taskCount++)
                        .setWeight(100,0)
                        .setInsets(DEFAULT_DISTANCE)
                        .setFill(GridBagConstraints.HORIZONTAL)
        );

        progressPanel.add(new JLabel(task.getName()),
                new GBC(0, 0)
                        .setWeight(0,0)
        );

        var progress = new JProgressBar();
        progressPanel.add(progress,
                new GBC(1,0)
                        .setWeight(100,0)
                        .setInsets(2 * DEFAULT_DISTANCE, 0)
                        .setFill(GridBagConstraints.HORIZONTAL)
        );
        var button = new JButton("取消");
        progressPanel.add(button,
                new GBC(3,0)
                        .setWeight(0,0)
        );
        button.addActionListener(event-> task.cancel(true));

        taskList.add(new runningTask(task, progress, button));
    }

    private void flush(){
        taskList.forEach(runningTask -> {
            var task = runningTask.task;
            var progress = runningTask.progressBar;
            EventQueue.invokeLater(()->
                    progress.setValue(task.getProgress()));
            if(task.isDone() || task.isCancelled()){
                taskList.remove(runningTask);
                EventQueue.invokeLater(()->runningTask.cancelButton.setEnabled(false));
            }
        });
    }
}
