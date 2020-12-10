package docmanagement.guiclient.frame.tool;

import docmanagement.shared.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumSet;

public class DialogBuilder extends JDialog {
    public static final int DEFAULT_DISTANCE = 10;
    public static final int DEFAULT_TEXT_WIDTH = 15;

    private ArrayList<Object> input = new ArrayList<>();
    private int lineCounter = 0;
    private int maxWidth = 0;

    protected ActionListener CLOSE_DIALOG = e -> {this.setVisible(false);};

    protected DialogBuilder(String title){
        this.setTitle(title);
        this.setLayout(new GridBagLayout());
    }

    protected void displayIni(){
        this.pack();
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (int)((screenSize.getWidth() - this.getWidth()) / 2),
                (int)((screenSize.getHeight() - this.getHeight()) / 2)
        );
    }

    public void display(){
        for(var o : input){
            if(o instanceof JTextField){
                var text = (JTextField)o;
                text.setText("");
            }
        }
        EventQueue.invokeLater(()->this.setVisible(true));
    }

    protected void addLabelAndTextFiled(String label){
        this.add(new JLabel(label),
                new GBC(0, lineCounter)
                        .setWeight(0, GBC.DEFAULT_WEIGHTY)
                        .setInsets(DEFAULT_DISTANCE));
        var text = new JTextField(DEFAULT_TEXT_WIDTH);
        input.add(text);
        this.add(text,
                new GBC(1, lineCounter)
                        .setFill(GridBagConstraints.HORIZONTAL)
                        .setInsets(2*DEFAULT_DISTANCE, 0));
        lineCounter++;
        maxWidth = Integer.max(2, maxWidth);
    }

    protected void addLabelAndPasswordFiled(String label){
        this.add(new JLabel(label),
                new GBC(0, lineCounter)
                        .setWeight(0,GBC.DEFAULT_WEIGHTY)
                        .setInsets(DEFAULT_DISTANCE));
        var text = new JPasswordField(DEFAULT_TEXT_WIDTH);
        input.add(text);
        this.add(text,
                new GBC(1, lineCounter)
                        .setFill(GridBagConstraints.HORIZONTAL)
                        .setInsets(2*DEFAULT_DISTANCE, 0));
        lineCounter++;
        maxWidth = Integer.max(2, maxWidth);
    }

    protected enum PathMode {
        OPEN, SAVE
    }

    protected void addLabelAndPathFiled(String label, PathMode pathMode){
        addLabelAndTextFiled(label);
        var text = (JTextField)input.get(input.size()-1);

        var pathButton = new JButton("...");
        this.add(pathButton,
                new GBC(3,lineCounter - 1)
                        .setWeight(0,GBC.DEFAULT_WEIGHTY)
                        .setInsets(DEFAULT_DISTANCE, 0));

        switch (pathMode){
            case OPEN ->
                    pathButton.addActionListener(event -> {
                        var fc = new JFileChooser();
                        var state = fc.showOpenDialog(this);
                        if (state == JFileChooser.APPROVE_OPTION) {
                            text.setText(fc.getSelectedFile().toString());
                        }
                    });
            case SAVE ->
                    pathButton.addActionListener(event -> {
                        var fc = new JFileChooser();
                        var state = fc.showSaveDialog(this);
                        if (state == JFileChooser.APPROVE_OPTION) {
                            text.setText(fc.getSelectedFile().toString());
                        }
                    });
        }
        lineCounter++;
        maxWidth = Integer.max(3, maxWidth);
    }

    protected void addOkAndCancelButton(String ok, String cancel, ActionListener okListener, ActionListener cancelListener){
        var buttonPanel = new JPanel();
        var okButton = new JButton(ok);
        var cancelButton = new JButton(cancel);
        this.add(buttonPanel,
                new GBC(0, lineCounter, maxWidth + 1,1)
                .setFill(GridBagConstraints.BOTH));

        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(okButton,
                new GBC(0,0).setInsets(0,DEFAULT_DISTANCE));
        buttonPanel.add(cancelButton,
                new GBC(1,0).setInsets(0,DEFAULT_DISTANCE));
        okButton.addActionListener(okListener);
        cancelButton.addActionListener(cancelListener);
        lineCounter++;
    }

    private static class ChoosingRole{
        public User.Role role;
    }

    protected void addRoleChooser(){
        var buttonGroup = new ButtonGroup();
        var roleButtonPanel = new JPanel();
        roleButtonPanel.setLayout(new GridBagLayout());

        this.add(roleButtonPanel,
                new GBC(0,lineCounter, maxWidth,1)
                        .setFill(GridBagConstraints.HORIZONTAL));
        roleButtonPanel.add(new JLabel("身份: "),
                new GBC(0,0).setInsets(0,DEFAULT_DISTANCE));

        var choosingRole = new ChoosingRole();
        input.add(choosingRole);

        boolean isFirst = true;
        for(var role : EnumSet.allOf(User.Role.class)) {
            if(role != User.Role.IGNORE){
                var button = new JRadioButton(role.toString());
                button.addActionListener(event->{
                    choosingRole.role = role;
                });
                roleButtonPanel.add(button, new GBC(GridBagConstraints.RELATIVE,0));
                buttonGroup.add(button);

                if(isFirst){
                    buttonGroup.setSelected(button.getModel(), true);
                    choosingRole.role = role;
                    isFirst = false;
                }
            }
        };
        lineCounter++;
    }

    protected String getText(int index){
        return ((JTextField)input.get(index)).getText();
    }

    protected char[] getPassword(int index){
        return ((JPasswordField)input.get(index)).getPassword();
    }

    protected User.Role getChoosingRole(int index){
        return ((ChoosingRole)input.get(index)).role;
    }
}
