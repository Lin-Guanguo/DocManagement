package test;

import docmanagement.guiclient.frame.GBC;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args){
        EventQueue.invokeLater(()->{
            var frame = new TestFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

class TestFrame extends JFrame{
    TestFrame(){
        this.setLayout(new GridBagLayout());
        var tabbedPane = new JTabbedPane();
        this.add(tabbedPane, new GBC().setFill(GridBagConstraints.BOTH));


        var panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        var scrollPane = new JScrollPane(panel);
        tabbedPane.add("hello", scrollPane);

        for(int i = 0; i < 30; ++i){
            var p = new JPanel();
            p.setLayout(new GridBagLayout());
            p.add(new JLabel("task"), new GBC(0,0).setWeight(0,0).setInsets(10));
            var progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
            p.add(progressBar, new GBC(1,0).setFill(GridBagConstraints.HORIZONTAL));
            panel.add(p, new GBC(0,i).setFill(GridBagConstraints.HORIZONTAL).setWeight(100,0));
        }




        this.pack();
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (int)((screenSize.getWidth() - this.getWidth()) / 2),
                (int)((screenSize.getHeight() - this.getHeight()) / 2)
        );
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600,400);
    }
}


