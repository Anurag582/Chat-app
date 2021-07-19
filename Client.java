import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class Client extends JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Times New Roman", Font.BOLD, 20);

    public Client() {
        try {
            System.out.println("Sending reuqest to server");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("connection done");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream());
            creatGUI();
            handleEvent();
            startReading();
            // startWriting();

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void handleEvent() {

        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // System.out.println("key released "+e.getKeyCode());
                if (e.getKeyCode() == 10) {
                    // System.out.println("you have presed enter ");
                    String contentTOSend = messageInput.getText();
                    messageArea.append("Me:" + contentTOSend + "\n");
                    out.println(contentTOSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }

        });

    }

    private void creatGUI() {

        this.setTitle("Client Messanger[END]");
        this.setSize(600, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("chat.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        this.setLayout(new BorderLayout());

        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);

    }

    public void startReading() {
        // thread read krke deta rahega
        Runnable r1 = () -> {

            System.out.println("reader started...");
            try {
                while (true) {

                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }

                    // System.out.println("server :" + msg);
                    messageArea.append("server :" + msg + "\n");

                }
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Connection is Closed");
            }

        };
        new Thread(r1).start();

    }

    public void startWriting() {
        // thread-data user se lega and send karega client tak
        Runnable r2 = () -> {
            System.out.println("writer started...");
            try {
                while (!socket.isClosed()) {

                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }

                }

            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("connection is closed");
            }

        };
        new Thread(r2).start();

    }

    public static void main(String[] args) {
        System.out.println("This is client...");
        new Client();
    }
}
