import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        while (true)
            try (
                    Socket socket = new Socket("127.0.0.1", 8000);
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socket.getInputStream()));
                    BufferedWriter writer =
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            socket.getOutputStream()))
            ) {
                String request = new Scanner(System.in).nextLine();
                if (request.equals("exit")) break;
                System.out.println("Request: " + request);
                writer.write(request);
                writer.newLine();
                writer.flush();
                String response = reader.readLine();
                System.out.println("Response: " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
