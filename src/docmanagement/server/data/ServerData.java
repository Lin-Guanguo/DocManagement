package docmanagement.server.data;

import docmanagement.shared.Doc;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.ServerOperation;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;


public class ServerData {
    static private final Map<User.Role, Set<ServerOperation>> userPermission;

    static{
        var properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(Path.of("serverconfig", "serverdata.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<User.Role, Set<ServerOperation>> tmpUserPermission = new HashMap<>();
        for(var role : EnumSet.allOf(User.Role.class)) {
            var tmp = properties.getProperty( role + "Permission");
            var set = EnumSet.noneOf(ServerOperation.class);
            for(var s : tmp.split(",")){
                set.add(ServerOperation.valueOf(s.trim()));
            };
            tmpUserPermission.put(role, Collections.unmodifiableSet(set));
        }
        userPermission = Collections.unmodifiableMap(tmpUserPermission);
    }

    private static final String listUserQuery =
            "SELECT name, password, role FROM javause.userinfo;";

    private static final String getUserQuery  =
            "SELECT name, password, role FROM javause.userinfo " +
                    "WHERE name = ?;";

    private static final String addUserUpdate =
            "INSERT INTO javause.userinfo(name, password, role) " +
                    "VALUES ( ?, ?, ?);";

    private static final String delUserUpdate =
            "DELETE FROM javause.userinfo " +
                    "WHERE name = ?;";

    private static final String listFileQuery =
            "SELECT id, creator, createTime, filename, description,  fileSize FROM javause.docinfo;";

    private static final String getFileQuery =
            "SELECT id, creator, createTime, filename, description,  fileSize FROM javause.docinfo " +
                    "WHERE id = ?;";

    private static final String addFileUpdate =
            "INSERT INTO javause.docinfo(id, creator, createTime, description, filename, fileSize) " +
                    "VALUES (?,?,?,?,?,?);";

    private static final String delFileUpdate =
            "DELETE FROM javause.docinfo " +
                    "WHERE id = ?;";

    private static final String modifyUserUpdate =
            "UPDATE javause.userinfo SET password = ?, role = ? where name = ?;";

    private final DataSource connectPool = new DatabaseConnectPool();

    public Set<ServerOperation> getUserPermission(User.Role role) {
        return userPermission.get(role);
    }

    public Collection<User> listUsers() throws SQLException {
        var connection = connectPool.getConnection();
        var state = connection.prepareStatement(listUserQuery);
        var resSet = state.executeQuery();
        Collection<User> collection = new ArrayList<>();
        while(resSet.next()){
            collection.add(new User(
                    resSet.getString(1),
                    resSet.getString(2),
                    User.Role.valueOf(resSet.getString(3))));
        }
        resSet.close();
        state.close();
        connection.close();
        return collection;
    }

    public User getUser(String userName) throws SQLException {
        var connection = connectPool.getConnection();
        var state = connection.prepareStatement(getUserQuery);
        state.setString(1, userName);
        var resSet = state.executeQuery();
        User res = null;
        if(resSet.next()){
            res = new User(
                    resSet.getString(1),
                    resSet.getString(2),
                    User.Role.valueOf(resSet.getString(3)));
        }
        resSet.close();
        state.close();
        connection.close();
        return res;
    }

    public boolean addUser(User user) throws SQLException {
        var connection = connectPool.getConnection();
        var state = connection.prepareStatement(addUserUpdate);
        state.setString(1, user.getName());
        state.setString(2, user.getPassword());
        state.setString(3, user.getRole().toString());
        boolean ok;
        try{
            state.executeUpdate();
            ok = true;
        }catch (java.sql.SQLIntegrityConstraintViolationException e){
            ok = false;
        }finally {
            state.close();
            connection.close();
        }
        return ok;
    }

    public User delUser(String userName) throws SQLException {
        var res = getUser(userName);
        if(res != null){
            var connection = connectPool.getConnection();
            var state = connection.prepareStatement(delUserUpdate);
            state.setString(1 ,userName);
            state.executeUpdate();
            state.close();
            connection.close();
        }
        return res;
    }

    public boolean modifyUser(User newOne) throws SQLException {
        var connection = connectPool.getConnection();
        var state = connection.prepareStatement(modifyUserUpdate);
        state.setString(1, newOne.getPassword());
        state.setString(2, newOne.getRole().toString());
        state.setString(3, newOne.getName());
        var res = state.executeUpdate();
        return res != 0;
    }

    public Collection<Doc> listFile() throws SQLException {
        var connection = connectPool.getConnection();
        var state = connection.prepareStatement(listFileQuery);
        var resSet = state.executeQuery();
        Collection<Doc> collection = new ArrayList<>();
        while(resSet.next()){
            collection.add(new Doc(
                    resSet.getInt(1),
                    resSet.getString(2),
                    resSet.getTimestamp(3),
                    resSet.getString(4),
                    resSet.getString(5),
                    resSet.getLong(6)
            ));
        }
        resSet.close();
        state.close();
        connection.close();
        return collection;
    }

    public boolean addFile(Doc doc) throws SQLException {
        var connection = connectPool.getConnection();
        var state = connection.prepareStatement(addFileUpdate);
        state.setInt(1, doc.getId());
        state.setString(2, doc.getCreator());
        state.setTimestamp(3, doc.getCreateTime());
        state.setString(4, doc.getDescription());
        state.setString(5, doc.getFilename());
        state.setLong(6, doc.getFileSize());
        boolean ok;
        try{
            state.executeUpdate();
            ok = true;
        }catch (java.sql.SQLIntegrityConstraintViolationException e){
            ok = false;
        }finally {
            state.close();
            connection.close();
        }
        return ok;
    }

    public Doc getFile(int id) throws SQLException {
        var connection = connectPool.getConnection();
        var state = connection.prepareStatement(getFileQuery);
        state.setInt(1, id);
        var resSet = state.executeQuery();
        Doc res = null;
        if(resSet.next()){
            res = new Doc(
                    resSet.getInt(1),
                    resSet.getString(2),
                    resSet.getTimestamp(3),
                    resSet.getString(4),
                    resSet.getString(5),
                    resSet.getLong(6)
            );
        }
        resSet.close();
        state.close();
        connection.close();
        return res;
    }

    public Doc delFile(int id) throws SQLException {
        var res = getFile(id);
        if(res != null){
            var connection = connectPool.getConnection();
            var state = connection.prepareStatement(delFileUpdate);
            state = connection.prepareStatement(delFileUpdate);
            state.setInt(1, id);
            state.executeUpdate();
            state.close();
            connection.close();
        }
        return res;
    }



}



