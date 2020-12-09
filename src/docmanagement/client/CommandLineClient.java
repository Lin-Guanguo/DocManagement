package docmanagement.client;


import docmanagement.shared.Doc;
import docmanagement.shared.User;
import docmanagement.shared.requestandmessage.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

public class CommandLineClient {
    private static final String host;
    private static final int port;

    static {
        var properties = new Properties();
        try {
            properties.load(Files.newInputStream(Path.of("clientconfig", "clientdata.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        host = properties.getProperty("serverhost");
        port = Integer.parseInt(properties.getProperty("port"));
    }

    private static final InputStream cmdIn = System.in;
    private static final Scanner scannerCmdIn = new Scanner(cmdIn);
    private static final PrintStream cmdOut = System.out;
    private static final PrintStream cmdErr = System.err;

    private List<Operation> permittedOperation;
    private User user  = null;

    public CommandLineClient() {
        exitTag:
        for(;;){
            loginSucceedTag:
            for(;;){
                List<Operation> loginList = List.of(
                        ClientLocalOperation.LOGIN,
                        ClientLocalOperation.EXIT);
                printOperationMenu(loginList);
                int num = readNumber(1, loginList.size()+1);
                switch ((ClientLocalOperation)loginList.get(num-1)){
                    case LOGIN -> {
                        try {
                            if(login()){
                                break loginSucceedTag;
                            }
                        } catch (ServerMessageException | SocketException  e) {
                            cmdErr.println(e.getMessage());
                        }
                    }
                    case EXIT -> {
                        break exitTag;
                    }
                    default -> {
                        assert false;
                    }
                }
            }

            try{
                permittedOperation = getPermission();
            } catch (ServerMessageException | SocketException e) {
                cmdOut.println(e.getMessage());
                continue exitTag;
            }
            permittedOperation.add(ClientLocalOperation.EXIT);
            printOperationMenu(permittedOperation);

            readOperationTag:
            for(;;){
                int num = readNumber(1, permittedOperation.size()+1);
                Operation operation = permittedOperation.get(num-1);
                if(operation.getClass().equals(ClientLocalOperation.class)){
                    if (operation == ClientLocalOperation.EXIT) {
                        break readOperationTag;
                    }
                    assert false;
                }else{
                    try{
                        if( !doServerOperation((ServerOperation) operation) ){
                            break readOperationTag;
                        };
                    } catch (ServerMessageException | SocketException e) {
                        cmdErr.println(e.getMessage());
                    }
                }
            }
            user = null;
        }
        cmdOut.println("exit");
        scannerCmdIn.close();
    }

    private boolean doServerOperation(ServerOperation operation) throws ServerMessageException, SocketException {
        switch (operation){
            case GET_PERMISSION -> printOperationMenu(permittedOperation);
            case ADD_USER -> addUserHandler();
            case DEL_USER -> delUserHandler();
            case LIST_USER -> listUserHandler();
            case UPLOAD_FILE -> uploadFileHandler();
            case DOWNLOAD_FILE -> downloadFileHandler();
            case DEL_FILE -> delFileHandler();
            case CHANGE_PASSWORD -> {
                return modifyUserHandler();}
            case MODIFY_USER -> {
                return modifyAllUserHandler(); }
            case LIST_FILE -> listFilesHandler();
            default -> {
                cmdErr.println("客户端未处理的报文类型");
            }
        }
        return true;
    }

    private AbstractMessage connectToServer(AbstractRequest request) throws ServerMessageException, SocketException {
        AbstractMessage message = null;
        try(var socket = new Socket(host, port)){
            var in = socket.getInputStream();
            var out = socket.getOutputStream();

            new ObjectOutputStream(out).writeObject(request);
            message = (AbstractMessage)new ObjectInputStream(in).readObject();
            return message;
        } catch (IOException e){
            throw new SocketException("服务器连接异常");
        }catch (ClassNotFoundException e) {
            throw new ServerMessageException("服务器返回信息异常");
        }
    }

    private boolean login() throws ServerMessageException, SocketException {
        var userRead = readUser(false);
        var message = (LoginCheckMessage)connectToServer(
                new LoginCheckRequest(userRead)
        );
        if(!message.isOk()){
            cmdOut.println("用户名或密码错误");
            return false;
        }else{
            user = new User(userRead.getName(), userRead.getPassword(), message.getRole());
            return true;
        }
    }

    private List<Operation> getPermission() throws ServerMessageException, SocketException {
        var message = (GetPermissionMessage)connectToServer(new GetPermissionRequest(user));
        return new ArrayList<>(message.getAllowed());
    }

    private void addUserHandler() throws ServerMessageException, SocketException {
        cmdOut.println("添加新用户");
        User toAdd = readUser(true);
        var message = (AddUserMessage)connectToServer(
                new AddUserRequest(user, toAdd)
        );
        if(message.isOk()){
            cmdOut.println("添加成功");
        }else{
            cmdOut.println("添加失败");
        }
    }

    private void delUserHandler() throws ServerMessageException, SocketException {
        cmdOut.print("请输入要删除用户的用户名: ");
        String name = scannerCmdIn.nextLine();
        var message = (DelUserMessage)connectToServer(
                new DelUserRequest(user, name)
        );
        if(message.isOk()){
            cmdOut.println(message.getDel());
            cmdOut.println("删除成功");
        }else{
            cmdOut.println("删除失败，该用户不存在");
        }
    }

    private boolean modifyUserHandler() throws ServerMessageException, SocketException {
        cmdOut.println("请输入新密码: ");
        var newPassword = scannerCmdIn.nextLine();
        var message = (ChangePasswordMessage)connectToServer(
                new ChangePasswordRequest(
                        user, newPassword));
        if(message.isOk()){
            System.out.println("修改成功, 请重新登陆");
            return false;
        }else{
            System.out.println("修改失败");
        }
        return true;
    }

    private boolean modifyAllUserHandler() throws ServerMessageException, SocketException{
        var toModify = readUser(true);
        var message = (ModifyUserMessage)connectToServer(
                new ModifyUserRequest(
                        user, toModify));
        if(message.isOk()){
            System.out.println("修改成功");
            if(toModify.getName().equals(user.getName())){
                System.out.println("请重新登陆");
                return false;
            }
        }else{
            System.out.println("修改失败");
        }
        return true;
    }

    private void listUserHandler() throws ServerMessageException, SocketException {
        var message = (ListUserMessage)connectToServer(
                new ListUserRequest(user)
        );
        message.getAllUser().forEach(cmdOut::println);
    }

    private void uploadFileHandler() throws SocketException, ServerMessageException {
        var path = readFile();
        if(!Files.exists(path)){
            cmdErr.println("文件不存在");
            return;
        };
        Doc doc;
        try{
            var size = Files.size(path);
            doc = readDoc(size);
        }catch (IOException e){
            e.printStackTrace();
            return;
        }

        try(var socket = new Socket(host, port)){
            var in = socket.getInputStream();
            var out = socket.getOutputStream();
            new ObjectOutputStream(out).writeObject(new UploadFileRequest(user, doc));
            var message = (UploadFileMessage)new ObjectInputStream(in).readObject();
            if(message.isOk()){
                cmdOut.println("开始传输文件");
                try(var input = new BufferedInputStream(Files.newInputStream(path))){
                    byte[] buf = new byte[1<<10];
                    int len;
                    while((len = input.read(buf)) != -1){
                        out.write(buf, 0, len);
                    }
                };
                cmdOut.println("传输完成");
            }else{
                cmdErr.println("服务器不接受上传该文件，文件id重复");
            }
        } catch (IOException e){
            throw new SocketException("服务器连接异常");
        }catch (ClassNotFoundException e) {
            throw new ServerMessageException("服务器返回信息异常");
        }
    }

    private void downloadFileHandler() throws ServerMessageException, SocketException {
        cmdOut.println("请输入文件id");
        int id = readNumber(0, Integer.MAX_VALUE);
        cmdOut.println("存储文件地址");
        var path = readFile();
        try {
            Files.createFile(path);
        } catch (java.nio.file.FileAlreadyExistsException e) {
            cmdErr.println("文件已存在");
            return;
        } catch (java.nio.file.NoSuchFileException e) {
            cmdErr.println("路径错误");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(var socket = new Socket(host, port)){
            var in = socket.getInputStream();
            var out = socket.getOutputStream();
            new ObjectOutputStream(out).writeObject(new DownloadFileRequest(user, id));
            var message = (DownloadFileMessage)new ObjectInputStream(in).readObject();
            if(message.isOk()){
                cmdOut.println(message.getDoc());
                cmdOut.println("开始传输文件");
                try(var fileOut = new BufferedOutputStream(Files.newOutputStream(path))){
                    long totalLen = message.getDoc().getFileSize();
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
                cmdOut.println("传输完成");
            }else{
                cmdErr.println("服务器不接受下载该文件，文件id不存在");
            }
        } catch (IOException e){
            throw new SocketException("服务器连接异常");
        }catch (ClassNotFoundException e) {
            throw new ServerMessageException("服务器返回信息异常");
        }
    }

    private void delFileHandler() throws SocketException, ServerMessageException{
        cmdOut.println("请输入需要删除文件的id");
        int id = readNumber(0, Integer.MAX_VALUE);
        var message = (DelFileMessage)connectToServer(
                new DelFileRequest(user, id)
        );
        if(message.isOk()){
            cmdOut.println(message.getDel());
            cmdOut.println("删除成功");
        }else{
            cmdOut.println("删除失败，该文件不存在");
        }
    }

    private void listFilesHandler() throws ServerMessageException, SocketException {
        var message = (ListFileMessage)connectToServer(
                new ListFileRequest(user)
        );
        message.getDocs().forEach(cmdOut::println);
    }

    private void printOperationMenu(List<Operation> list){
        String head;
        if(user != null){
            head = "*".repeat(10) + user.getRole() + ": " + user.getName() + "*".repeat(10);
        }else{
            head = "*".repeat(30);
        }
        int len = head.length();
        cmdOut.println(head);
        for(int i = 0; i < list.size(); ++i){
            cmdOut.println(" ".repeat(len/2 - 4) + (i+1)  + ": " + list.get(i).show());
        }
        cmdOut.println("*".repeat(len));
    }

    private User readUser(boolean readRole){
        cmdOut.println("请输入用户信息");
        cmdOut.print("name: ");
        String name = scannerCmdIn.nextLine();
        cmdOut.print("password: ");
        String password = scannerCmdIn.nextLine();

        User.Role role = User.Role.IGNORE;
        if(readRole){
            cmdOut.println("role: ");
            List<User.Role> list = new ArrayList<>(EnumSet.allOf(User.Role.class));
            list.remove(User.Role.IGNORE);
            int i = 0;
            for(i = 0; i < list.size(); ++i){
                System.out.println("        " + (i+1) + ": " +list.get(i).toString());
            }
            role = list.get(readNumber(1, i+1) - 1);
        }
        return new User(name, password, role);
    }

    private int readNumber(int begin, int end){
        for(;;){
            cmdOut.print("Number: ");
            cmdOut.flush();
            try{
                int num = scannerCmdIn.nextInt();
                scannerCmdIn.nextLine();
                if(num >= begin && num < end){
                    return num;
                }
                cmdOut.println("请输入正确数字(" + begin + " ~ " + (end - 1) + ")");
            }catch (InputMismatchException e){
                scannerCmdIn.nextLine();
                cmdOut.println("请输入正确数字(" + begin + " ~ " + (end - 1) + ")");
            }
        }
    }

    private Path readFile(){
        Path path;
        for(;;){
            cmdOut.print("请输入文件绝对地址: ");
            try {
                var inString = scannerCmdIn.nextLine();
                path = Paths.get(inString.trim());
                break;
            }catch (java.nio.file.InvalidPathException e){
                cmdErr.println("输入错误");
            }
        }
        return path;
    }

    private Doc readDoc(long fileSize){
        cmdOut.println("输入文件相关信息");
        cmdOut.println("请输入文件id: ");
        int id = readNumber(0, Integer.MAX_VALUE);
        cmdOut.print("请输入文件名: ");
        String filename = scannerCmdIn.nextLine();
        cmdOut.print("请输入文件描述: ");
        String description = scannerCmdIn.nextLine();
        return new Doc(id, user.getName(),
                new Timestamp(System.currentTimeMillis()), filename,
                description, fileSize);
    }

    public static void main(String[] args){
        new CommandLineClient();
    }
}
