package Chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class ServerPart {
    private ServerSocket serverSocket;
    private ArrayList<ServerThreads> allClients = new ArrayList<>();
    private int port = 5555;
    Logger log = LogManager.getLogger(ServerPart.class);

    {
        InitServer();
    }

    private void InitServer() {
        try {
            serverSocket = new ServerSocket(port);
            // System.out.println("Server is running.");
            log.info("Server is running.");
            while (true) {
                Socket clSocket = serverSocket.accept();
                allClients.add(new ServerThreads(clSocket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

      //  Logger log = LogManager.getLogger(ServerPart.class);
      //  log.info(new ServerPart());
          new ServerPart();
    }

    private class ServerThreads extends Thread {
        private DataInputStream in;
        private DataOutputStream out;
        private Socket socket;

        public ServerThreads(Socket socket) {
            this.socket = socket;
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                start();
            } catch (IOException ex) {
               // ex.printStackTrace();
                log.error("Data Streams exception!");
            }
        }

        public void run() {
            String str;
            while (true) {
                try {
                    str = in.readUTF();
                    for (ServerThreads threads : allClients) {
                        log.info("The client sent this: " + str);
                        log.info("Im sending this to clients: " + str);
                      //  System.out.println("The client sent this: " + str);
                       // System.out.println("Im sending this to clients: " + str);
                        threads.sendMessage(str);
                    }
                } catch (IOException ex) {
                    allClients.remove(this);
                    System.out.println(allClients.size());
                   // System.out.println("Nothing to send");
                    log.warn("Nothing to send");
                    break;
                }
            }
        }

        private void sendMessage(String str) throws IOException {
            out.writeUTF(str);
            out.flush();
        }
    }

}
