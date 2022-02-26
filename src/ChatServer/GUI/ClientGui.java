package ChatServer.GUI;

import Interfaces.MessageListener;
import Interfaces.UserStatusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Karim
 */
public class ClientGui extends javax.swing.JFrame {

    static Thread clientThread;
    Client client;
    String clientUsername = "";
    String selectedUserName = "";
    private final DefaultListModel<String> userListModel;
    public OutputStream out = null;
    InputStream in = null;
    BufferedReader reader = null;
    String dash = "------------------------------------------------------------ @";
    String confirmation = "msg_unconfirmed";
    int msg_id = 1;
    //--------------------------------------------------------------------------new vars
    int msg_counter = 0;
    int msg_limit = 3;
    ArrayList<String> msg_lst = new ArrayList<>();
    ArrayList<String> ack_lst = new ArrayList<>();
    ArrayList<String> tmp = new ArrayList<>();
    ArrayList<String> tmp2 = new ArrayList<>();

    public ClientGui() {
        initComponents();
        this.setLocation(880, 5);
        this.setTitle("Client");
        txt_msg_counter.setText(msg_counter + "");
        btn_disconnect.setEnabled(false);
        userListModel = new DefaultListModel<>();
        lst_online_users.setModel(userListModel);
        try {
            client = new Client(txt_serverIP.getText(), 27015);
            client.addMessageListeners(new MessageListener() {
                @Override
                public void onMessage(String from, String body) {
                    int randomNum = ThreadLocalRandom.current().nextInt(1, 100 + 1);
                    int threshold = (int) noise_slider.getValue();

                    String pattern = "yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    Date now = new Date();
//----------------------------------------------------------------------------------------------------------------------------------------------------------comment to disable ack
                    try {

                        if (randomNum > threshold) {
                            String confirmation = "msg_confirmed " + from + "\n";
                            client.confirmMessage(confirmation);
                        } else {

                        }

                    } catch (IOException ex) {
                        Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
                    }
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                    if (from.equalsIgnoreCase("Server_brodcast")) {
                        txt_area.append("(Server_brodcast): " + body + "\n");
                        txt_area.append(dash + simpleDateFormat.format(now) + "\n");
                    } else if (body.contains("(BRODCAST)")) {
                        String line = from + ": " + body;
                        txt_area.append(line + "\n");
                        txt_area.append(dash + simpleDateFormat.format(now) + "\n");
                    } else if (from.equalsIgnoreCase(clientUsername)) {
                        txt_area.append("(Server_message): " + body + "\n");
                        txt_area.append(dash + simpleDateFormat.format(now) + "\n");
                    } else {

                        String[] body_parts = body.split(";");
                        String msgText = body_parts[0];
                        int msgId = Integer.parseInt(body_parts[1]);

                        if (msgId == 1) {
                            String line = from + ": " + msgText;
                            if (randomNum > threshold) {
                                txt_area.append(line + "\n");
                                txt_area.append(dash + simpleDateFormat.format(now) + "\n");
                            }

                        } else {
                            String line = from + ": " + msgText;
                            txt_area.append(line + "(copy)\n");
                            txt_area.append(dash + simpleDateFormat.format(now) + "\n");
                        }
                    }
                }
            });
            client.addUserStatusListener(
                    new UserStatusListener() {
                        @Override
                        public void online(String login
                        ) {
                            if (login.equalsIgnoreCase(clientUsername)) {
                                //do nothing
                            } else {
                                txt_area.append(dash + "ONLINE  : " + login + "\n");
                                userListModel.addElement(login);
                            }
                        }

                        @Override
                        public void offline(String login
                        ) {
                            if (login.equalsIgnoreCase(clientUsername)) {
                                //do nothing
                            } else {
                                txt_area.append(dash + "OFFLINE : " + login + "\n");
                                userListModel.removeElement(login);
                            }
                        }
                    }
            );
        } catch (IOException ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        txt_input.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txt_input.setText("");
            }
        });
        txt_password.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txt_password.setText("");
            }
        });
        txt_username.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txt_username.setText("");
            }
        });
        txt_serverIP.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txt_serverIP.setText("");
            }
        });
        lst_online_users.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    if (userListModel.isEmpty()) {
                        txt_area.append("No one is online" + "\n");
                    } else {
                        selectedUserName = userListModel.elementAt(lst_online_users.getSelectedIndex());
                        txt_area.append(selectedUserName + " is selected" + "\n");
                    }
                }
            }
        });

        DefaultCaret caret = (DefaultCaret) txt_area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

//        this.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                if (client.connect()) {
//                    disconnect();
//                }
//
//                System.exit(0);//close app
//
//            }
//        });
        clientThread = new Thread() {
            @Override
            public void run() {

            }
        };
    }

    public void connect() {
        txt_area.setText("");
        clientUsername = txt_username.getText();
        String pass = txt_password.getText();
        String address = txt_serverIP.getText();

        if (validate(address) || address.equalsIgnoreCase("localhost")) {
            if (!client.connect(address, 27015)) {
                JOptionPane.showMessageDialog(null, "Error Connecting", "Failure", JOptionPane.ERROR_MESSAGE);
            } else {
                lbl_Status.setText("Connected");
                lbl_client_port.setText(client.socket.getLocalPort() + "");
                try {
                    boolean result = client.login(clientUsername, pass);
                    if (!result) {
                        JOptionPane.showMessageDialog(null, "Wrong username or password.", "Failure", JOptionPane.ERROR_MESSAGE);
                    } else {
                        btn_connect.setEnabled(false);
                        btn_disconnect.setEnabled(true);
                        txt_serverIP.setEditable(false);
                        txt_username.setEditable(false);
                        txt_password.setEditable(false);
                        txt_area.append(dash + "Welcome, " + clientUsername + ".\n");

                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClientGui.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid Address.");
        }

    }

    public void disconnect() {
        lbl_Status.setText("Disconnected");
        lbl_client_port.setText(" - ");
        try {
            client.logoff();
            txt_area.append(dash + "Goodbye.\n");
            btn_connect.setEnabled(true);
            btn_disconnect.setEnabled(false);
            userListModel.removeAllElements();
            lst_online_users.setModel(userListModel);

        } catch (IOException ex) {
            Logger.getLogger(ClientGui.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resetFrame() {
        ack_lst = new ArrayList<>();
        msg_lst = new ArrayList<>();
        msg_counter = 0;
    }

    public void message() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date now = new Date();
        String message = txt_input.getText();

        if (msg_lst.size() == msg_limit) {
            resetFrame();
        }

        initialStage(message);

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {

                        if (tmp2.size() == msg_limit - 1) {
                            System.out.println("----------------------------------------------------------------------------------------------------------------------START OF FRAME");
                            if (!msg_lst.isEmpty()) {
                                for (int i = 0; i < msg_lst.size(); i++) {
                                    System.out.println("- Message: '" + msg_lst.get(i) + "' Sent & Confirmed.");
                                }
                            } else if (!tmp.isEmpty()) {
                                for (int i = 0; i < tmp.size(); i++) {
                                    System.out.println("- Message: '" + tmp.get(i) + "' Sent & Not Confirmed. (Waiting in Queue)");
                                }
                            }

                            for (int i = 0; i < tmp2.size(); i++) {
                                try {
                                    System.out.println("- Message: '" + tmp2.get(i) + "' Sent & Confirmed. (Auto-Retransmission)");
                                    client.sendMessage(selectedUserName, tmp2.get(i), 0);
                                } catch (IOException ex) {
                                    Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            System.out.println("----------------------------------------------------------------------------------------------------------------------END  OF  FRAME\n");
                            tmp2.clear();
                            tmp2_debug.setText("");
                            resetFrame();
                            msg_debug.setText("");
                            ack_debug.setText("");
                            tmp2.addAll(tmp);
                            tmp2_debug.setText(tmp2.toString());
                            tmp.clear();
                            tmp_debug.setText("");
                        }

                        if (tmp2.size() == msg_limit - 2) {
                            if (tmp2.size() + tmp.size() + msg_lst.size() == msg_limit) {
                                System.out.println("----------------------------------------------------------------------------------------------------------------------START OF FRAME");
                                if (!msg_lst.isEmpty()) {
                                    for (int i = 0; i < msg_lst.size(); i++) {
                                        System.out.println("- Message: '" + msg_lst.get(i) + "' Sent & Confirmed.");
                                    }
                                }
                                if (!tmp.isEmpty()) {
                                    for (int i = 0; i < tmp.size(); i++) {
                                        System.out.println("- Message: '" + tmp.get(i) + "' Sent & Not Confirmed. (Waiting in Queue)");
                                    }
                                }
                                for (int i = 0; i < tmp2.size(); i++) {
                                    try {
                                        System.out.println("- Message: '" + tmp2.get(i) + "' Sent & Confirmed. (Auto-Retransmission)");
                                        client.sendMessage(selectedUserName, tmp2.get(i), 0);
                                    } catch (IOException ex) {
                                        Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                System.out.println("----------------------------------------------------------------------------------------------------------------------END  OF  FRAME\n");
                                tmp2.clear();
                                tmp2_debug.setText("");
                                resetFrame();
                                msg_debug.setText("");
                                ack_debug.setText("");
                                tmp2.addAll(tmp);
                                tmp2_debug.setText(tmp2.toString());
                                tmp.clear();
                                tmp_debug.setText("");
                            }
                        }

                        if (msg_lst.size() == msg_limit) {
                            btn_send.setEnabled(true);
                            System.out.println("----------------------------------------------------------------------------------------------------------------------START OF FRAME");
                            for (int i = 0; i < msg_lst.size(); i++) {
                                System.out.println("- Message: '" + msg_lst.get(i) + "' Sent & Confirmed.");
                            }
                            resetFrame();
                            msg_debug.setText("");
                            ack_debug.setText("");
                            System.out.println("----------------------------------------------------------------------------------------------------------------------END  OF  FRAME\n");

                        } else if (tmp.size() == msg_limit) {
                            btn_send.setEnabled(true);
                            System.out.println("----------------------------------------------------------------------------------------------------------------------START OF FRAME");
                            for (int i = 0; i < tmp.size(); i++) {
                                System.out.println("- Message: '" + tmp.get(i) + "' Sent & Confirmed. (Auto-Retransmission)");
                                try {
                                    client.sendMessage(selectedUserName, tmp.get(i), 0);
                                } catch (IOException ex) {
                                    Logger.getLogger(ClientGui.class
                                            .getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            resetFrame();
                            tmp.clear();
                            tmp_debug.setText("");
                            msg_debug.setText("");
                            ack_debug.setText("");
                            System.out.println("----------------------------------------------------------------------------------------------------------------------END  OF  FRAME\n");
                        } else if (tmp.size() + msg_lst.size() == msg_limit) {
                            btn_send.setEnabled(true);
                            tmp2.addAll(tmp);
                            tmp2_debug.setText(tmp2.toString());
                            tmp.clear();
                            tmp_debug.setText("");
                            resetFrame();
                            msg_debug.setText("");
                            ack_debug.setText("");
                        }
                    }
                },
                20);

        txt_area.append("You ---> " + selectedUserName + " :" + message + "\n");
        txt_area.append(dash + simpleDateFormat.format(now) + "\n");
        txt_input.setText("");
        msg_counter++;
        txt_msg_counter.setText(msg_counter + "");
        if (msg_counter == msg_limit) {
            btn_send.setEnabled(false);
        }
    }

    public void initialStage(String message) {
        ack_lst.add("msg_unconfirmed");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (confirmation.equalsIgnoreCase("msg_unconfirmed")) {
                    try {
                        if (msg_id == 1) {
                            client.sendMessage(selectedUserName, message, msg_id);
                            msg_id = 0;
                        } else {
                            tmp.add(message);
                            tmp_debug.setText(tmp.toString());
                            timer.cancel();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ClientGui.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (confirmation.equalsIgnoreCase("msg_confirmed")) {
                    msg_lst.add(message);
                    msg_debug.setText(msg_lst.toString());
                    ack_lst.set(ack_lst.size() - 1, "msg_confirmed");
                    timer.cancel();
                }
                ack_debug.setText(ack_lst.toString());
            }
        }, 0, 10);

        msg_id = 1;
        confirmation = "msg_unconfirmed";
    }

    public void brodcast() {
        String message = txt_input.getText();
        try {
            client.brodcast(message);

        } catch (IOException ex) {
            Logger.getLogger(ClientGui.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        txt_input.setText("");
    }

    public static boolean validate(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_area = new javax.swing.JTextArea();
        txt_input = new javax.swing.JTextField();
        btn_send = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txt_username = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txt_password = new javax.swing.JPasswordField();
        btn_disconnect = new javax.swing.JButton();
        btn_connect = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lbl_Status = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lbl_client_port = new javax.swing.JLabel();
        txt_serverIP = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lst_online_users = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        btn_send1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        noise_slider = new javax.swing.JSlider();
        txt_noise = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_msg_counter = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ack_debug = new javax.swing.JTextField();
        msg_debug = new javax.swing.JTextField();
        tmp_debug = new javax.swing.JTextField();
        tmp2_debug = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();

        jLabel7.setText("jLabel7");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txt_area.setEditable(false);
        txt_area.setColumns(20);
        txt_area.setRows(5);
        jScrollPane1.setViewportView(txt_area);

        txt_input.setText("Your Text Input Here...");
        txt_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_inputActionPerformed(evt);
            }
        });

        btn_send.setText("Send");
        btn_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sendActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Login"));

        jLabel2.setText("User Name");

        txt_username.setText("karim");
        txt_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_usernameActionPerformed(evt);
            }
        });

        jLabel1.setText("Password");

        txt_password.setText("jPasswordField1");
        txt_password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_passwordActionPerformed(evt);
            }
        });

        btn_disconnect.setText("Diconnect");
        btn_disconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_disconnectActionPerformed(evt);
            }
        });

        btn_connect.setText("Connect");
        btn_connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_connectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(btn_connect, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_disconnect, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addGap(5, 5, 5))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_password)
                    .addComponent(txt_username)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_connect)
                    .addComponent(btn_disconnect))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Client Info"));

        jLabel4.setText("Status : ");

        lbl_Status.setText("Disconnected ");

        jLabel3.setText("Client Port :");

        lbl_client_port.setText("00000");

        txt_serverIP.setText("127.0.0.1");
        txt_serverIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_serverIPActionPerformed(evt);
            }
        });

        jLabel5.setText("Server Address :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_client_port))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txt_serverIP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_Status))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txt_serverIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lbl_Status))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lbl_client_port))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Online Users"));

        lst_online_users.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lst_online_users);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
        );

        jButton1.setText("Clear Text");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btn_send1.setText("Brodcast");
        btn_send1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_send1ActionPerformed(evt);
            }
        });

        jButton2.setText("Clear Selection");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Noise"));
        jPanel4.setName(""); // NOI18N

        noise_slider.setMajorTickSpacing(10);
        noise_slider.setOrientation(javax.swing.JSlider.VERTICAL);
        noise_slider.setPaintLabels(true);
        noise_slider.setPaintTicks(true);
        noise_slider.setSnapToTicks(true);
        noise_slider.setValue(0);
        noise_slider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                noise_sliderStateChanged(evt);
            }
        });

        txt_noise.setEditable(false);
        txt_noise.setText("0");
        txt_noise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_noiseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_noise)
                    .addComponent(noise_slider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(txt_noise, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(noise_slider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(7, 7, 7))
        );

        jLabel6.setText("Message Counter:");

        txt_msg_counter.setEditable(false);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Variables"));

        jLabel8.setText("Ack List");

        jLabel9.setText("Accepted");

        jLabel10.setText("Rejected");

        ack_debug.setEditable(false);

        msg_debug.setEditable(false);

        tmp_debug.setEditable(false);

        tmp2_debug.setEditable(false);

        jLabel11.setText("Queue");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tmp_debug)
                    .addComponent(ack_debug, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(msg_debug, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tmp2_debug)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(ack_debug, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(msg_debug, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(tmp_debug, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(tmp2_debug, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_input, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_msg_counter))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 743, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_send1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                            .addComponent(btn_send, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2))
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(txt_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_send1)
                    .addComponent(jLabel6)
                    .addComponent(txt_msg_counter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_connectActionPerformed
        if (validate(txt_serverIP.getText()) == true) {
            connect();

        } else {
            JOptionPane.showMessageDialog(null, "Invalid IP Address.");
        }
    }//GEN-LAST:event_btn_connectActionPerformed

    private void txt_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_inputActionPerformed

    }//GEN-LAST:event_txt_inputActionPerformed

    private void btn_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sendActionPerformed
        //btn_send.setEnabled(false);
        if (txt_input.getText().isEmpty()) {
            btn_send.setEnabled(true);
            JOptionPane.showMessageDialog(null, "Empty Textfield.");
        } else {

            if (!selectedUserName.isEmpty()) {
                int randomNum = ThreadLocalRandom.current().nextInt(1, 100 + 1);
                int threshold = (int) noise_slider.getValue();
                if (randomNum > threshold) {
                    message();
                } else {
                    btn_send.setEnabled(true);
                    JOptionPane.showMessageDialog(null, "Select a user first from the online users list.", "No user was selected", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                btn_send.setEnabled(true);
                JOptionPane.showMessageDialog(null, "Message not sent");
            }
        }


    }//GEN-LAST:event_btn_sendActionPerformed

    private void btn_disconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_disconnectActionPerformed
        disconnect();
        txt_serverIP.setEditable(true);
        txt_username.setEditable(true);
        txt_password.setEditable(true);
    }//GEN-LAST:event_btn_disconnectActionPerformed

    private void txt_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_usernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_usernameActionPerformed

    private void txt_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_passwordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_passwordActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        txt_area.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btn_send1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_send1ActionPerformed
        if (txt_input.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Empty Textfield.");
        } else {
            brodcast();
        }
    }//GEN-LAST:event_btn_send1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        lst_online_users.clearSelection();
        selectedUserName = "";

    }//GEN-LAST:event_jButton2ActionPerformed

    private void noise_sliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_noise_sliderStateChanged
        txt_noise.setText(Integer.toString(noise_slider.getValue()));
    }//GEN-LAST:event_noise_sliderStateChanged

    private void txt_serverIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_serverIPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_serverIPActionPerformed

    private void txt_noiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_noiseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_noiseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientGui().setVisible(true);
            }
        });

        ////////////////////////////////////////////////////////////////////////___Main Client Thread
        if (clientThread == null) {

        } else {
            clientThread.start();
        }
        ////////////////////////////////////////////////////////////////__end of___Main Client Thread
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ack_debug;
    private javax.swing.JButton btn_connect;
    private javax.swing.JButton btn_disconnect;
    private javax.swing.JButton btn_send;
    private javax.swing.JButton btn_send1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_Status;
    private javax.swing.JLabel lbl_client_port;
    private javax.swing.JList lst_online_users;
    private javax.swing.JTextField msg_debug;
    private javax.swing.JSlider noise_slider;
    private javax.swing.JTextField tmp2_debug;
    private javax.swing.JTextField tmp_debug;
    private javax.swing.JTextArea txt_area;
    private javax.swing.JTextField txt_input;
    private javax.swing.JTextField txt_msg_counter;
    private javax.swing.JTextField txt_noise;
    private javax.swing.JPasswordField txt_password;
    private javax.swing.JTextField txt_serverIP;
    private javax.swing.JTextField txt_username;
    // End of variables declaration//GEN-END:variables

    class Client {

        public final String serverPublicIP;
        public final int serverPort;
        public Socket socket;
        public OutputStream out;
        public InputStream in;
        public BufferedReader bufferedIn;
        private final ArrayList<UserStatusListener> userStatusListener = new ArrayList<>();
        private final ArrayList<MessageListener> messageListener = new ArrayList<>();

        public Client(String serverIP, int serverPort) throws IOException {
            this.serverPublicIP = serverIP;
            this.serverPort = serverPort;
        }

        public boolean connect(String serverPublicIP, int port) {
            try {
                this.socket = new Socket(serverPublicIP, serverPort);
                this.out = socket.getOutputStream();
                this.in = socket.getInputStream();
                this.bufferedIn = new BufferedReader(new InputStreamReader(in));
                //System.out.println("Client port: " + socket.getLocalPort());
                return true;
            } catch (IOException ex) {
            }
            return false;
        }

        public boolean login(String user, String password) throws IOException {
            String cmd = "login " + user + " " + password + "\n";
            out.write(cmd.getBytes());
            String response = bufferedIn.readLine();
            //System.out.println(response);
            if (response.equalsIgnoreCase("user_logged_in")) {
                startMessageReader();
                return true;
            } else {
                return false;
            }
        }

        public void logoff() throws IOException {
            String cmd = "quit\n";
            out.write(cmd.getBytes());
        }

        private void startMessageReader() {
            Thread t = new Thread() {
                @Override
                public void run() {
                    readMessageLoop();
                }
            };
            t.start();
        }

        private void readMessageLoop() {
            try {
                String line;
                while ((line = bufferedIn.readLine()) != null) {
                    String[] tokens = org.apache.commons.lang3.StringUtils.split(line);
                    if (tokens != null && tokens.length > 0) {
                        String cmd = tokens[0];

                        if (cmd.equalsIgnoreCase("online")) {
                            handleOnline(tokens);
                        } else if (cmd.equalsIgnoreCase("offline")) {
                            handleOffline(tokens);
                        } else if (cmd.equalsIgnoreCase("message")) {
                            String[] tokensMsg = org.apache.commons.lang3.StringUtils.split(line, null, 3);
                            handleMessage(tokensMsg);
                        } else if (cmd.equalsIgnoreCase("msg_confirmed")) {
                            confirmation = "msg_confirmed";
                        } else if (cmd.equalsIgnoreCase("server_shutdown")) {
                            txt_area.append(dash + "Server Shutting Down\n");
                            disconnect();
                        }
                    }
                }
            } catch (Exception e) {
                //System.out.println("-------------------------------------->" + e);
                e.printStackTrace();
                try {
                    socket.close();
                } catch (Exception ex) {
                }
            }
        }

        private void handleOnline(String[] tokens) {
            String login = tokens[1];
            for (UserStatusListener listeners : userStatusListener) {
                listeners.online(login);
            }
        }

        private void handleOffline(String[] tokens) {
            String login = tokens[1];
            for (UserStatusListener listeners : userStatusListener) {
                listeners.offline(login);
            }
        }

        private void handleMessage(String[] tokens) {
            String from = tokens[1];
            String body = tokens[2];
            for (MessageListener listener : messageListener) {
                listener.onMessage(from, body);
            }
        }

        public void brodcast(String msgText) throws IOException {
            String cmd = "brodcast " + msgText + "\n";
            out.write(cmd.getBytes());
        }

        public void sendMessage(String sendTo, String msgText, int id) throws IOException {
            String cmd = "message " + sendTo + " " + msgText + ";" + id + "\n";
            out.write(cmd.getBytes());
        }

        public void confirmMessage(String confirmation) throws IOException {
            String cmd = confirmation + "\n";
            if (!cmd.isEmpty()) {
                out.write(cmd.getBytes());
            } else {
                System.out.println("CONFIRMATION EMPTY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        public void addUserStatusListener(UserStatusListener listener) {
            userStatusListener.add(listener);
        }

        public void removeUserStatusListener(UserStatusListener listener) {
            userStatusListener.remove(listener);
        }

        public void addMessageListeners(MessageListener listener) {
            messageListener.add(listener);
        }

        public void removeMessageListeners(MessageListener listener) {
            messageListener.remove(listener);
        }
    }
}
