package docmanagement.guiclient.frame.table;

import docmanagement.shared.User;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserListTable extends JTable {
    private final UserTableModel data;

    public UserListTable(){
        super(new UserTableModel());
        data = (UserTableModel) this.getModel();
    }

    public void update(Collection<User> objs){
        data.update(objs);
    }

    static class UserTableModel extends AbstractTableModel {
        private static final int ColumnCount = 3;
        private static final String[] columnName =
                new String[]{"用户名", "密码", "权限"};
        private List<User> userList = new ArrayList<>();
        private int rowCount = 0;

        void update(Collection<User> users){
            rowCount = users.size();
            userList = List.copyOf(users);
            EventQueue.invokeLater(this::fireTableDataChanged);
        }

        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public int getColumnCount() {
            return ColumnCount;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return switch (columnIndex){
                case 0 -> userList.get(rowIndex).getName();
                case 1 -> userList.get(rowIndex).getPassword();
                case 2 -> userList.get(rowIndex).getRole();
                default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
            };
        }

        @Override
        public String getColumnName(int column) {
            return columnName[column];
        }
    }
}
