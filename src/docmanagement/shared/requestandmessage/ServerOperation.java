package docmanagement.shared.requestandmessage;

public enum ServerOperation implements Operation {
    LOGIN_CHECK("登录检测"),
    GET_PERMISSION("展示许可权限"),
    UPLOAD_FILE("上传文件"),
    DOWNLOAD_FILE("下载文件"),
    DEL_FILE("删除文件"),
    LIST_FILE("展示文件列表"),
    ADD_USER("添加用户"),
    DEL_USER("删除用户"),
    CHANGE_PASSWORD("修改密码"),
    MODIFY_USER("修改用户信息"),
    LIST_USER("展示用户列表"),


    PERMISSION_DENIED_EXCEPTION("无操作权限"),
    WRONG_REQUEST_EXCEPTION("未知请求"),
    UNKNOWN_REQUEST_EXCEPTION("服务器无法处理该请求")
    ;
    private final String showString;

    ServerOperation(String showString){
        this.showString = showString;
    }

    @Override
    public String show() {
        return this.showString;
    }
}
