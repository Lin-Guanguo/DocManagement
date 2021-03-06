package docmanagement.server;

import docmanagement.server.data.DataProcessing;
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
    private InputStream socketIn = null;
    private OutputStream socketOut = null;
    private AbstractRequest request = null;
    private final DataProcessing dataProcessing;

    public ConnectHandler(Socket socket, DataProcessing dataProcessing) {
        this.socket = socket;
        this.dataProcessing = dataProcessing;
    }

    @Override
    public void run() {
        try{
            socket.setSoTimeout(10000); //IO堵塞超时设置
            socketIn = socket.getInputStream();
            socketOut = socket.getOutputStream();
            var objectInputStream = new ObjectInputStream(socketIn);
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
                    " : " + request.getType().show());

            switch (request.getType()){
                case LOGIN_CHECK -> loginCheckHandler();
                case GET_PERMISSION -> getPermissionHandler();
                case ADD_USER -> addUserHandler();
                case DEL_USER -> delUserHandler();
                case LIST_USER -> listUserHandler();
                case UPLOAD_FILE -> uploadFileHandler();
                case DOWNLOAD_FILE -> downloadFileHandler();
                case DEL_FILE -> delFileHandler();
                case CHANGE_PASSWORD -> ChangePasswordHandler();
                case MODIFY_USER -> modifyUserHandler();
                case LIST_FILE -> listFilesHandler();
                default -> {
                    writeObjects(new UnknownRequestExceptionMessage());
                    throw new UnknownRequestException("服务器未处理该请求类型");
                }
            }
        } catch (PermissionDeniedException | WrongRequestException | UnknownRequestException e) {
            System.err.println(e.getMessage());
        } catch (java.net.SocketTimeoutException e){
            System.err.println("io堵塞超时");
        } catch (IOException e) {
            System.err.println("socket连接异常");
        } catch (SQLException e) {
            System.err.println("MySQL数据量连接异常");
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println(socket.getRemoteSocketAddress() +
                        " : close");
            } catch (IOException e) {
                System.err.println("socket关闭异常");
            }
        }
    }

    private void writeObjects(Object... objects) throws IOException {
        var objectOutputStream = new ObjectOutputStream(socketOut);
        for(var obj : objects){
            objectOutputStream.writeObject(obj);
        }
    }

    private boolean checkPermissions() throws SQLException {
        if(request.getType() == ServerOperation.LOGIN_CHECK){
            return true;
        }
        var user = dataProcessing.getUser(request.getUser().getName());
        return user != null &&
                user.equals(request.getUser()) &&
                dataProcessing.getUserPermission(request.getUser().getRole()).contains(request.getType());
    }

    private void loginCheckHandler() throws IOException, SQLException {
        var user = request.getUser();
        var serverUser = dataProcessing.getUser(user.getName());
        if(serverUser == null || !serverUser.equals(user)){
            writeObjects(new LoginCheckMessage(false, null));
        }else{
            writeObjects(new LoginCheckMessage(true, serverUser.getRole()));
        }
    }

    private void getPermissionHandler() throws IOException{
        var pac = new GetPermissionMessage(dataProcessing.getUserPermission(request.getUser().getRole()));
        writeObjects(pac);
    }

    private void addUserHandler() throws IOException, SQLException {
        var concreteRequest = (AddUserRequest)request;
        boolean ok = dataProcessing.addUser(concreteRequest.getToAdd());

        writeObjects(new AddUserMessage(ok));
    }

    private void delUserHandler() throws IOException, SQLException {
        var concreteRequest = (DelUserRequest)request;
        var delUser = dataProcessing.delUser(concreteRequest.getDelUserName());
        writeObjects(new DelUserMessage(delUser != null, delUser));
    }

    private void ChangePasswordHandler() throws IOException, SQLException {
        var concreteRequest = (ChangePasswordRequest)request;
        var user = concreteRequest.getUser();
        var newPassword = concreteRequest.getNewPassword();
        var isOk = dataProcessing.modifyUser(
                new User(user.getName(), newPassword, user.getRole()));
        writeObjects(new ChangePasswordMessage(isOk));
    }

    private void modifyUserHandler() throws IOException, SQLException {
        var concreteRequest = (ModifyUserRequest)request;
        var user = concreteRequest.getUser();
        var toModify = concreteRequest.getToModify();
        var isOk = dataProcessing.modifyUser(toModify);
        writeObjects(new ModifyUserMessage(isOk));
    }

    private void listUserHandler() throws IOException, SQLException {
        writeObjects(new ListUserMessage(dataProcessing.listUsers()));
    }

    private void uploadFileHandler() throws IOException, SQLException {
        var concreteRequest = (UploadFileRequest)request;
        var doc = concreteRequest.getDoc();
        if(dataProcessing.addFile(doc)){
            writeObjects(new UploadFileMessage(true));

            var path = Path.of(Server.FILE_PATH, Integer.toString(doc.getId()));
            Files.createFile(path);
            try(var fileOut = new BufferedOutputStream(Files.newOutputStream(path))){
                var totalLen = doc.getFileSize();
                final int bufSize = 1<<10;
                byte[] buf = new byte[bufSize];
                while(totalLen > bufSize){
                    socketIn.readNBytes(buf, 0, bufSize);
                    fileOut.write(buf);
                    totalLen -= bufSize;
                }
                if(totalLen > 0){
                    socketIn.readNBytes(buf, 0, (int)totalLen);
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
        var doc = dataProcessing.getFile(id);
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
                        socketOut.write(buf, 0, len);
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
        var delFile = dataProcessing.delFile(concreteRequest.getFileId());
        if(delFile != null){
            var path = Path.of(Server.FILE_PATH, Integer.toString(delFile.getId()));
            Files.delete(path);
        }
        writeObjects(new DelFileMessage(delFile != null, delFile));
    }

    private void listFilesHandler() throws IOException, SQLException {
        writeObjects(new ListFileMessage(
                dataProcessing.listFile()
        ));
    }
}
