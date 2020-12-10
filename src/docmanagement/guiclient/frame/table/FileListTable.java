package docmanagement.guiclient.frame.table;

import docmanagement.shared.Doc;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileListTable extends JTable {
    private final FileTableModel data;

    public FileListTable(){
        super(new FileTableModel());
        data = (FileTableModel) this.getModel();
    }

    public void update(Collection<Doc> objs){
        data.update(objs);
    }

    static class FileTableModel extends AbstractTableModel {
        private static final int ColumnCount = 6;
        private static final String[] columnName =
                new String[]{"id", "创建者", "创建时间", "描述", "文件名", "大小"};
        private List<Doc> userList = new ArrayList<>();
        private int rowCount = 0;

        void update(Collection<Doc> docs){
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
