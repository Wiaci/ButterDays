import java.io.*;

public class ClientMain {
    public static void main(String[] args) {
        try {
            NiceClient niceClient = new NiceClient();
            niceClient.run();
        } catch (CtrlDException e) {
            System.out.println("\nСердечко: моя остановочка");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
