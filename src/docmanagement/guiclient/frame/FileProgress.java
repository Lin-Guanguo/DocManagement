package docmanagement.guiclient.frame;

import docmanagement.guiclient.background.FileTask;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileProgress extends JPanel {
    public static final int DEFAULT_DISTANCE = 10;

    private final JPanel contentPanel = new JPanel();
    private int taskCount = 0;

    private final java.util.Timer flushTimer = new java.util.Timer();

    private static class taskAndProgress{
        public FileTask task;
        public JProgressBar progressBar;

        public taskAndProgress(FileTask task, JProgressBar progressBar) {
            this.task = task;
            this.progressBar = progressBar;
        }
    }
    private final ConcurrentLinkedQueue<taskAndProgress> taskList = new ConcurrentLinkedQueue<>();

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
        progressPanel.add(new JButton("取消"),
                new GBC(3,0)
                        .setWeight(0,0)
        );

        taskList.add(new taskAndProgress(task, progress));
    }

    private void flush(){
        taskList.forEach(taskAndProgress -> {
            var task = taskAndProgress.task;
            if(task.isDone()){
                taskList.remove(taskAndProgress);
            }else{
                var progress = taskAndProgress.progressBar;
                EventQueue.invokeLater(()->
                        progress.setValue(task.getProgress()));
            }
        });
    }
}
