package test;

import javax.swing.*;
import java.util.List;

public class Test {
    public static void main(String[] args){
        Thread t = new Thread(()->{
            try {
                for(int i = 0 ; i < 10; ++i){
                    Thread.sleep(1000);
                    System.out.println("tick");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
        System.out.println("main end");
        A a = new A();
        a.getProgress();
    }
}

class A extends SwingWorker<Integer, Integer>{

    @Override
    protected Integer doInBackground() throws Exception {
        setProgress(2);
        return null;
    }

    @Override
    protected void process(List<Integer> chunks) {
        super.process(chunks);
    }
}
