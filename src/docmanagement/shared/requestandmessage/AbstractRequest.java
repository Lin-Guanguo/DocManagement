package docmanagement.shared.requestandmessage;

import docmanagement.shared.User;

import java.io.Serializable;

public abstract class AbstractRequest implements Serializable {
    private final User user;

    public AbstractRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    /**
     * 用以确定子类的类型
     * @return 代表请求类型的枚举值
     */
    abstract public ServerOperation getType();

    @Override
    public String toString() {
        return "AbstractRequest{" +
                "user=" + user +
                '}';
    }
}
