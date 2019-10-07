import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server
 */
public class Main {
    public final static Logger LOGGER = Logger.getLogger(Main.class.getName());
    static {
        try {
            Handler handler = new FileHandler("log.log");
            handler.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            LOGGER.info("Server starts...");
            final ServerSocket serverSocket = new ServerSocket(8080);
            ExecutorService es = Executors.newFixedThreadPool(10);

            for (int i = 0; i < 5; i++) {
                LOGGER.info("Thread: #" + i + "starts");
                es.submit(new Thread(() -> {
                    try {
                        while (true) {
                            Socket clientSocket = serverSocket.accept();
                            //Request
                            InputStreamReader input = new InputStreamReader(clientSocket.getInputStream());
                            StringBuilder sb = new StringBuilder(); //придут байты парсим на строки
                            while (input.ready()) sb.append((char) input.read());
                            LOGGER.info("read request");
                            System.out.println(sb);

                            //Response
                            OutputStreamWriter outputStream = new OutputStreamWriter(clientSocket.getOutputStream());
                            String sbResponse = "HTTP/1.1 200 OK\n" +
                                    "Cache-Control: no-cache\n" +
                                    "Connection: Close:\n" +
                                    "Content-Type: application/json\n\n" +
                                    "{\"ok\": \"" + (sb.toString().length() > 30 ? "too long" :
                                    sb.toString()) + "\"}";

                            Thread.sleep(100);
                            outputStream.write(sbResponse);
                            outputStream.flush();
                            LOGGER.info("Sent Message");
                            input.close();
                            clientSocket.close();
                        }

                    } catch (IOException | InterruptedException e ){
                        e.printStackTrace();
                    }
                }));
            }
            Thread.sleep(1000000);
            serverSocket.close();
            LOGGER.info("Server is closed");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
