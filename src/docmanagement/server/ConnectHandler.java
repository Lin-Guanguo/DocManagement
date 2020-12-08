package docmanagement.server;

import docmanagement.server.data.ServerData;
import docmanagement.server.exception.PermissionDeniedException;
import docmanagement.server.exception.UnknownRequestException;
import docmanagement.server.exception.WrongRequestException;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ConnectHandler implements Runnable {
    private final Socket socket;
    private InputStream in = null;
    private OutputStream out = null;
    private AbstractRequest request = null;
    private final ServerData serverData;

    public ConnectHandler(Socket socket, ServerData serverData) {
        this.socket = socket;
        this.serverData = serverData;
    }

    @Override
    public void run() {
        try{
            socket.setSoTimeout(10000); //IO堵塞超时设置
            in = socket.getInputStream();
            out = socket.getOutputStream();
            var objectInputStream = new ObjectInputStream(in);
            Object obj;
            try{
                obj = objectInputStream.readObject();
                if(!(obj instanceof AbstractRequest)) {
                    throw new WrongRequestException();
                }
            }catch (ClassNotFoundException | WrongRequestException e){
                writeObjects(new WrongRequestExceptionMessage());
                throw new WrongRequestException("错误的请求格式");
            }

            request = (AbstractRequest)obj;
            if(!checkPermissions()){
                writeObjects(new PermissionDeniedExceptionMessage());
                throw new PermissionDeniedException("非法请求");
            }

            System.out.println(socket.getRemoteSocketAddress() +
                    " : " + request.getType().show() +
                    ", time = " + new Timestamp(System.currentTimeMillis()).toString());
            switch (request.getType()){
                case LOGIN_CHECK -> loginCheckHandler();
                case GET_PERMISSION -> getPermissionHandler();
                case ADD_USER -> addUserHandler();
                case DEL_USER -> delUserHandler();
                case LIST_USER -> listUserHandler();
                case UPLOAD_FILE -> uploadFileHandler();
                case DOWNLOAD_FILE -> downloadFileHandler();
                case DEL_FILE -> delFileHandler();
                case MODIFY_USER -> modifyUserHandler();
                case MODIFY_ALL_USER -> modifyAllUserHandler();
                case LIST_FILE -> listFilesHandler();
                default -> {
                    writeObjects(new UnknownRequestExceptionMessage());
                    throw new UnknownRequestException("服务器未处理该请求类型");
                }
            }
        } catch (PermissionDeniedException | WrongRequestException | UnknownRequestException e) {
            System.err.println(e.getMessage());
        } catch (java.net.SocketException e){
            System.err.println("io堵塞超时");
        } catch (IOException e) {
            System.err.println("socket连接异常");
        } catch (SQLException e) {
            System.err.println("MySQL数据量连接异常");
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("socket关闭异常");
            }
        }
    }

    private void writeObjects(Object... objects) throws IOException {
        var objectOutputStream = new ObjectOutputStream(out);
        for(var obj : objects){
            objectOutputStream.writeObject(obj);
        }
    }

    private boolean checkPermissions() throws SQLException {
        if(request.getType() == ServerOperation.LOGIN_CHECK){
            return true;
        }
        var user = serverData.getUser(request.getUser().getName());
        return user != null &&
                user.equals(request.getUser()) &&
                serverData.getUserPermission(request.getUser().getRole()).contains(request.getType());
    }

    private void loginCheckHandler() throws IOException, SQLException {
        var user = request.getUser();
        var serverUser = serverData.getUser(user.getName());
        if(serverUser == null || !serverUser.equals(user)){
            writeObjects(new LoginCheckMessage(false, null));
        }else{
            writeObjects(new LoginCheckMessage(true, serverUser.getRole()));
        }
    }

    private void getPermissionHandler() throws IOException{
        var pac = new GetPermissionMessage(serverData.getUserPermission(request.getUser().getRole()));
        writeObjects(pac);
    }

    private void addUserHandler() throws IOException, SQLException {
        var concreteRequest = (AddUserRequest)request;
        boolean ok = serverData.addUser(concreteRequest.getToAdd());

        writeObjects(new AddUserMessage(ok));
    }

    private void delUserHandler() throws IOException, SQLException {
        var concreteRequest = (DelUserRequest)request;
        var delUser = serverData.delUser(concreteRequest.getDelUserName());
        writeObjects(new DelUserMessage(delUser != null, delUser));
    }

    private void modifyUserHandler() throws IOException, SQLException {
        var concreteRequest = (ModifyUserRequest)request;
        var user = concreteRequest.getUser();
        var newPassword = concreteRequest.getNewPassword();
        var isOk = serverData.modifyUser(
                new User(user.getName(), newPassword, user.getRole()));
        writeObjects(new ModifyUserMessage(isOk));
    }

    private void modifyAllUserHandler() throws IOException, SQLException {
        var concreteRequest = (ModifyAllUserRequest)request;
        var user = concreteRequest.getUser();
        var toModify = concreteRequest.getToModify();
        var isOk = serverData.modifyUser(toModify);
        writeObjects(new ModifyAllUserMessage(isOk));
    }

    private void listUserHandler() throws IOException, SQLException {
        writeObjects(new ListUserMessage(serverData.listUsers()));
    }

    private void uploadFileHandler() throws IOException, SQLException {
        var concreteRequest = (UploadFileRequest)request;
        var doc = concreteRequest.getDoc();
        if(serverData.addFile(doc)){
            writeObjects(new UploadFileMessage(true));

            var path = Path.of(Server.FILE_PATH, Integer.toString(doc.getId()));
            Files.createFile(path);
            try(var fileOut = new BufferedOutputStream(Files.newOutputStream(path))){
                var totalLen = doc.getFileSize();
                final int bufSize = 1<<10;
                byte[] buf = new byte[bufSize];
                while(totalLen > bufSize){
                    in.readNBytes(buf, 0, bufSize);
                    fileOut.write(buf);
                    totalLen -= bufSize;
                }
                if(totalLen > 0){
                    in.readNBytes(buf, 0, (int)totalLen);
                    fileOut.write(buf, 0, (int)totalLen);
                }
            }
        }else{
            writeObjects(new UploadFileMessage(false));
        }
    }

    private void downloadFileHandler() throws IOException, SQLException {
        var concreteRequest = (DownloadFileRequest)request;
        var id = concreteRequest.getId();
        var doc = serverData.getFile(id);
        if(doc == null){
            writeObjects(new DownloadFileMessage(false, null));
        }else{
            var path = Path.of(Server.FILE_PATH, Integer.toString(doc.getId()));
            if(Files.exists(path)){
                writeObjects(new DownloadFileMessage(true, doc));
                try(var input = new BufferedInputStream(Files.newInputStream(path))){
                    byte[] buf = new byte[1<<10];
                    int len;
                    while((len = input.read(buf)) != -1){
                        out.write(buf, 0, len);
                    }
                };
            }else{
                writeObjects(new DownloadFileMessage(false, null));
                System.err.println("服务器文件信息异常，文件与数据库不同步");
            }
        }
    }

    private void delFileHandler() throws IOException, SQLException {
        var concreteRequest = (DelFileRequest)request;
        var delFile = serverData.delFile(concreteRequest.getFileId());
        if(delFile != null){
            var path = Path.of(Server.FILE_PATH, Integer.toString(delFile.getId()));
            Files.delete(path);
        }
        writeObjects(new DelFileMessage(delFile != null, delFile));
    }

    private void listFilesHandler() throws IOException, SQLException {
        writeObjects(new ListFileMessage(
                serverData.listFile()
        ));
    }
}
