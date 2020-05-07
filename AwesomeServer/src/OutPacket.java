import java.io.Serializable;

public class OutPacket<T> implements Serializable {
    T value;

    public void setValue(T value) {
        this.value = value;
    }
}
