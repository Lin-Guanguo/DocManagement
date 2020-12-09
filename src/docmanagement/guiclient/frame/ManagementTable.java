package docmanagement.guiclient.frame;

import docmanagement.shared.Doc;
import docmanagement.shared.User;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ManagementTable extends JTable {
    private final updatableTableModel data;

    public ManagementTable(TableType tableType){
        super(tableType.newTable());
        data = (updatableTableModel)this.getModel();
    }

    public void update(Collection<?> objs){
        data.update(objs);
    }

    enum TableType{
        USER_TABLE, FILE_TABLE;
        public updatableTableModel newTable(){
            if(this == USER_TABLE){
                return new UserTableModel();
            }else{
                return new FileTableModel();
            }
        }
    }

    static abstract class updatableTableModel extends AbstractTableModel{
        abstract void update(Collection<?> users);
    }

    static class UserTableModel extends updatableTableModel {
        private static final int ColumnCount = 3;
        private static final String[] columnName =
                new String[]{"用户名", "密码", "权限"};
        private List<User> userList = new ArrayList<>();
        private int rowCount = 0;

        void update(Collection users){
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

    static class FileTableModel extends updatableTableModel {
        private static final int ColumnCount = 6;
        private static final String[] columnName =
                new String[]{"id", "创建者", "创建时间", "描述", "文件名", "大小"};
        private List<Doc> userList = new ArrayList<>();
        private int rowCount = 0;

        void update(Collection docs){
            rowCount = docs.size();
            userList = List.copyOf(docs);
            this.fireTableDataChanged();
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
                case 0 -> userList.get(rowIndex).getId();
                case 1 -> userList.get(rowIndex).getCreator();
                case 2 -> userList.get(rowIndex).getCreateTime();
                case 3 -> userList.get(rowIndex).getDescription();
                case 4 -> userList.get(rowIndex).getFilename();
                case 5 -> userList.get(rowIndex).getFileSize();
                default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
            };
        }

        @Override
        public String getColumnName(int column) {
            return columnName[column];
        }
    }
}