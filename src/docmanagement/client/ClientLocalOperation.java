package docmanagement.client;

import docmanagement.shared.requestandmessage.Operation;

enum ClientLocalOperation implements Operation {
    LOGIN("登录"),
    EXIT("退出");
    private final String showString;

    ClientLocalOperation(String showString) {
        this.showString = showString;
    }

    @Override
    public String show() {
        return this.showString;
    }

}
