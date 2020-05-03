import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (
                ServerSocket server = new ServerSocket(8000))
        {
            System.out.println("Server Started");
            while (true)
                try (
                        Socket socket = server.accept();
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                socket.getInputStream()));
                        BufferedWriter writer =
                                new BufferedWriter(
                                        new OutputStreamWriter(
                                                socket.getOutputStream()))
                ) {
                    System.out.println("Client Connected");
                    String request = reader.readLine();
                    System.out.println("Request: " + request);
                    if (request == null) continue;
                    if (request.equals("exit")) break;
                    String response = "Hello from server " + request.length();
                    System.out.println("Response: " + response);
                    writer.write(response);
                    writer.newLine();
                    writer.flush();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
