package docmanagement.shared;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private final String name;
    private final String password;
    private final Role role;

    public User(String name, String password, Role role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return name.equals(user.name) &&
                password.equals(user.password) &&
                (role == user.role || user.role == Role.IGNORE || role == Role.IGNORE);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password, role);
    }

    public enum Role {
        OPERATOR, BROWSER, ADMINISTRATOR, IGNORE
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
