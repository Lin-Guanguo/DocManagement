package docmanagement.shared;

import java.io.Serializable;
import java.sql.Timestamp;

public class Doc implements Serializable {
    private final int id;
    private final String creator;
    private final Timestamp createTime;
    private final String description;
    private final String filename;
    private final long fileSize;

    public Doc(int id, String creator, Timestamp timestamp, String filename, String description, long fileSize) {
        this.id = id;
        this.creator = creator;
        this.createTime = timestamp;
        this.description = description;
        this.filename=filename;
        this.fileSize = fileSize;
    }

    public int getId() {
        return id;
    }

    public String getCreator() {
        return creator;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public String getDescription() {
        return description;
    }

    public String getFilename() {
        return filename;
    }

    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String toString() {
        return "Doc{" +
                "id=" + id +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", description='" + description + '\'' +
                ", filename='" + filename + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}
