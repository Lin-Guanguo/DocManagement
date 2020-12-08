package docmanagement.shared.requestandmessage;

public interface Operation {
    default String show(){
        return this.toString();
    };
}
