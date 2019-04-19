package Chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientPart implements ActionListener {
    private JTextArea messages_log;
    private JTextField inputField;
    private String name;
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private JButton sendButton = new JButton("Send");
    private String host = "localhost";
    private int port = 5555;
    Logger log = LogManager.getLogger(ClientPart.class);

    {
        name = "Katya";

        JFrame frame = new JFrame(name + "'s Chat");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        inputField = new JTextField(35);
        messages_log = new JTextArea(15, 43);
        messages_log.setEditable(false);
        messages_log.setLineWrap(true);
        messages_log.setWrapStyleWord(true);
        sendButton.addActionListener(this);
        sendButton.setBackground(Color.PINK);
        JScrollPane pane = new JScrollPane(messages_log);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.add(pane);
        panel.add(inputField);
        panel.add(sendButton);
        panel.setBackground(Color.CYAN);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setSize(500, 325);
        frame.setVisible(true);

        InitTheProcess();
    }

    private void InitTheProcess() {
        try {
            clientSocket = new Socket(host, port);
            log.info("Connection is established.");
           // System.out.println("Connection is established.");
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            Runnable r = () -> {
                while (true) {
                    try {
                        log.info("Appending");
                      //  System.out.println("Appending");
                        String s = in.readUTF();
                        appendMessage(s);
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                }
            };
            r.run();

        } catch (UnknownHostException hostEx) {
           // System.out.println("The defined host is invalid.");
            log.error("The defined host is invalid.");
            sendButton.setVisible(false);
        } catch (IOException ex) {
           // System.out.println("Smth went wrong");
            appendMessage("Smth went wrong");
            log.error("Smth went wrong");
            sendButton.setVisible(false);
            //  ex.printStackTrace();
        }
    }

    private void sendMessage(String str) {
        try {
            out.writeUTF(name + ": " + str);
            out.flush();
            log.info("This text was sent to the server: " + str);
           // System.out.println("This text was sent to the server: " + str);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void appendMessage(String str) {
        messages_log.append(str + "\n");
    }

    public void actionPerformed(ActionEvent ev) {
        String str;
        str = inputField.getText();
        if (!str.equals("") && !str.matches("\\s+")) {
            sendMessage(str);
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
       // Logger log = LogManager.getLogger(ClientPart.class);
      //  log.info(new ClientPart());
        new ClientPart();
    }
}
