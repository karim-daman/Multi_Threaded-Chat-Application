package ChatServer.GUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultCaret;
import org.apache.commons.lang3.StringUtils;

public class ServerGui extends javax.swing.JFrame {

    Server server = null;
    Socket clientSocket;
    ServerHandler thread_manager;
    int server_port = 27015;
    static Thread clientThread;
    String selectedUserName = "";
    private final DefaultListModel<String> userListModel;
    String conf = "msg_unconfirmed";

    String dash = "------------------------------------------------------------ @";

    public ServerGui() {
        initComponents();
        this.setLocation(5, 5);
        this.setTitle("Server");
        btn_server_stop.setEnabled(false);

        userListModel = new DefaultListModel<>();
        lst_online_users.setModel(userListModel);

        txt_input.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                txt_input.setText("");
            }
        });

        lst_online_users.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() > 1) {

                    if (userListModel.isEmpty()) {
                        txt_area.append("No one is online" + "\n");
                    } else {
                        selectedUserName = userListModel.elementAt(lst_online_users.getSelectedIndex());
                        //txt_area.append(selectedUserName + " is selected" + "\n");
                    }
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////scroll bar to max y axis on txt area append
        DefaultCaret caret = (DefaultCaret) txt_area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        ///////////////////////////////////////////////////////////////////end//scroll bar to max y axis on txt area append

        clientThread = new Thread() {
            @Override
            public void run() {
            }

        };
    }

    public void serverStart() {
        btn_server_start.setEnabled(false);
        btn_server_stop.setEnabled(true);
        txt_area.append("Accepting client connections..." + "\n");
        server = new Server(server_port);
        server.start();
        lbl_s_status.setText("Running...");
        lbl_s_port.setText(server_port + "");
    }

    public void serverStop() {

        //disonnect all users
        for (int i = 0; i < server.handlerLst.size(); i++) {
            try {
                server.handlerLst.get(i).send("server_shutdown\n");
            } catch (IOException ex) {
                Logger.getLogger(ServerGui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        btn_server_start.setEnabled(true);
        btn_server_stop.setEnabled(false);
        lbl_s_status.setText("Off");
        txt_area.append("Server is switched off." + "\n");
        lbl_s_port.setText(" - ");

        try {
            server.serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void server_brodcast() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date now = new Date();
        String message = txt_input.getText();
        try {

            server.brodcast(message);
            txt_area.append("Server -->  ALL: " + message + "\n");
            txt_area.append(dash + simpleDateFormat.format(now) + "\n");

        } catch (IOException ex) {
            Logger.getLogger(ServerGui.class.getName()).log(Level.SEVERE, null, ex);
        }
        txt_input.setText("");
    }

    public void server_message() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date now = new Date();

        String message = txt_input.getText();
        try {
            if (!selectedUserName.isEmpty()) {

                server.sendMessage(selectedUserName, message);
                txt_area.append("Server -->  " + selectedUserName + ": " + message + "\n");
                txt_area.append(dash + simpleDateFormat.format(now) + "\n");
            } else {
                btn_send.setEnabled(true);
                JOptionPane.showMessageDialog(null, "Select a user first from the online users list.", "No user was selected", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException ex) {
            Logger.getLogger(ServerGui.class.getName()).log(Level.SEVERE, null, ex);
        }
        txt_input.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txt_area = new javax.swing.JTextArea();
        txt_input = new javax.swing.JTextField();
        btn_send = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lbl_publicIP = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lbl_s_port = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbl_s_status = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        btn_server_start = new javax.swing.JButton();
        btn_server_stop = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lst_online_users = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        btn_brodcast = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Server Info"));

        lbl_publicIP.setText("Network is Offline.");

        jLabel1.setText("Open Port:");

        lbl_s_port.setText("00000");

        jLabel2.setText("Status:");

        lbl_s_status.setText("Off");

        jLabel3.setText("Connect to Server");

        jSeparator1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_s_port))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_s_status))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_publicIP)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(lbl_s_port))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(lbl_s_status))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lbl_publicIP))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Controls"));

        btn_server_start.setText("Start Server");
        btn_server_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_server_startActionPerformed(evt);
            }
        });

        btn_server_stop.setText("Stop Server");
        btn_server_stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_server_stopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_server_start)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_server_stop, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_server_start)
                    .addComponent(btn_server_stop))
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
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jButton1.setText("Clear Text");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btn_brodcast.setText("Brodcast");
        btn_brodcast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_brodcastActionPerformed(evt);
            }
        });

        jButton2.setText("Clear Selection");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_input, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_brodcast, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                        .addComponent(btn_send, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(txt_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_brodcast)
                    .addComponent(btn_send))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_server_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_server_startActionPerformed

        ////////////////////////////////////////////////////////////////////////check public ip if connected.
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip;
            ip = in.readLine(); //you get the IP as a String
            System.out.println(ip);
            lbl_publicIP.setText(ip);
        } catch (IOException ex) {
            lbl_publicIP.setText("Network is Offline.");
        }
        ///////////////////////////////////////////////////////////////////end//check public ip if connected.

        txt_area.setText("");
        serverStart();
    }//GEN-LAST:event_btn_server_startActionPerformed

    private void btn_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sendActionPerformed

        if (txt_input.getText().isEmpty()) {
            btn_send.setEnabled(true);
            JOptionPane.showMessageDialog(null, "Empty Textfield.");
        } else {
            server_message();
        }

    }//GEN-LAST:event_btn_sendActionPerformed

    private void btn_server_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_server_stopActionPerformed
        userListModel.clear();
        lst_online_users.clearSelection();
        selectedUserName = "";
        serverStop();
    }//GEN-LAST:event_btn_server_stopActionPerformed

    private void txt_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_inputActionPerformed

    }//GEN-LAST:event_txt_inputActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        txt_area.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btn_brodcastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_brodcastActionPerformed

        if (txt_input.getText().isEmpty()) {
            btn_send.setEnabled(true);
            JOptionPane.showMessageDialog(null, "Empty Textfield.");
        } else {
            server_brodcast();
        }

    }//GEN-LAST:event_btn_brodcastActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        lst_online_users.clearSelection();
        selectedUserName = "";
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(ServerGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerGui().setVisible(true);
            }
        });

        if (clientThread == null) {

        } else {
            clientThread.start();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_brodcast;
    private javax.swing.JButton btn_send;
    private javax.swing.JButton btn_server_start;
    private javax.swing.JButton btn_server_stop;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lbl_publicIP;
    private javax.swing.JLabel lbl_s_port;
    private javax.swing.JLabel lbl_s_status;
    private javax.swing.JList lst_online_users;
    private javax.swing.JTextArea txt_area;
    private javax.swing.JTextField txt_input;
    // End of variables declaration//GEN-END:variables

    class Server extends Thread {

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date now = new Date();

        private final int serverPort;
        private ArrayList<ServerHandler> handlerLst = new ArrayList<>();
        Socket clientSocket;
        ServerSocket serverSocket;
        ServerHandler thread_manager;

        Server(int serverPort) {
            this.serverPort = serverPort;
        }

        @Override
        public void run() {

            try {
                serverSocket = new ServerSocket(serverPort);
                while (true) {
                    clientSocket = serverSocket.accept();
                    //System.out.println("Accepted a connection from " + clientSocket);
                    txt_area.append("Accepted a connection from " + clientSocket + "\n");
                    txt_area.append(dash + simpleDateFormat.format(now) + "\n");
                    thread_manager = new ServerHandler(this, clientSocket);
                    handlerLst.add(thread_manager);
                    thread_manager.start();
                }
            } catch (IOException e) {
                System.out.println("Server Shut down.");
            }
        }

        public ArrayList<ServerHandler> getHandlerLst() {
            return handlerLst;
        }

        void removeHandler(ServerHandler handler) {
            handlerLst.remove(handler);
        }

        public void brodcast(String msgText) throws IOException {

            String cmd = "message " + "Server_brodcast " + msgText + "\n";
            for (ServerHandler handlerLst1 : handlerLst) {
                handlerLst1.clientSocket.getOutputStream().write(cmd.getBytes());
            }
        }

        public void sendMessage(String sendTo, String msgText) throws IOException {
            String cmd = "message " + sendTo + " " + msgText + "\n";
            for (ServerHandler handlerLst1 : handlerLst) {
                if (handlerLst1.username.equalsIgnoreCase(sendTo)) {
                    handlerLst1.clientSocket.getOutputStream().write(cmd.getBytes());
                }
            }
        }
    }

    class ServerHandler extends Thread {

        public final Socket clientSocket;
        public String username = null;
        public final Server server;
        public OutputStream out;
        private final HashSet<String> topicSet = new HashSet<>();

        ServerHandler(Server server, Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                handleClientSocket();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void handleClientSocket() throws IOException, InterruptedException {
            this.out = clientSocket.getOutputStream();
            InputStream in = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {

                //System.out.println("+++++++++++++++++" + line);

                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if (cmd.equalsIgnoreCase("quit")) {
                        handleLogOff();
                        break;
                    } else if (cmd.equalsIgnoreCase("login")) {
                        handleLogin(out, tokens);
                    } else if (cmd.equalsIgnoreCase("message")) {
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokensMsg);
                    } else if (cmd.equalsIgnoreCase("msg_confirmed")) {
                        handleConfirmed(line);
                    } else if (cmd.equalsIgnoreCase("msg_unconfirmed")) {
                        handleNonConfirmed(line);
                    } else if (cmd.equalsIgnoreCase("brodcast")) {
                        String[] tokensMsg = StringUtils.split(line, null, 1);
                        handleBroadcast(tokensMsg, username);
                    } else if (cmd.equalsIgnoreCase("joingroup")) {
                        handleJoinGroupChat(tokens);
                    } else if (cmd.equalsIgnoreCase("leavegroup")) {
                        handleLeaveGroupChat(tokens);
                    } else {
                        String message = "unknown command: " + cmd + "\n";
                        System.out.println("cmd: " + message);
                        out.write(message.getBytes());
                    }
                }
            }
        }

        private void handleBroadcast(String[] tokensMsg, String frmUsr) throws IOException {
            String body = tokensMsg[0];
            String[] txt = StringUtils.split(body, null, 2);
            List<ServerHandler> handlerLst = server.getHandlerLst();
            for (ServerHandler handler : handlerLst) {
                String message = "message " + frmUsr + " " + "(BRODCAST) " + txt[1] + "\n";
                handler.send(message);
                //System.out.println(message);
            }
        }

        private void handleConfirmed(String line) throws IOException {
            String[] array = line.split(" ");
            String usr = array[1];
            List<ServerHandler> handlerLst = server.getHandlerLst();
            String unblock_cmd = "msg_confirmed\n";
            for (ServerHandler handler : handlerLst) {
                if (handler.username.equalsIgnoreCase(usr)) {
                    handler.clientSocket.getOutputStream().write(unblock_cmd.getBytes());
                }
            }
        }

        private void handleNonConfirmed(String line) throws IOException {
            String[] array = line.split(" ");
            String usr = array[1];
            List<ServerHandler> handlerLst = server.getHandlerLst();
            String unblock_cmd = "msg_unconfirmed\n";
            for (ServerHandler handler : handlerLst) {
                if (handler.username.equalsIgnoreCase(usr)) {
                    handler.clientSocket.getOutputStream().write(unblock_cmd.getBytes());
                }
            }
        }

        private void handleMessage(String[] tokens) throws IOException {
            //format: message <usr> text 
            //format: message #topic text 
            String sendTo = tokens[1];
            String body = tokens[2];
            String[] body_parts = body.split(";");
            String msg = body_parts[0];
            int id = Integer.parseInt(body_parts[1]);

            boolean isTopic = sendTo.charAt(0) == '#';
            List<ServerHandler> handlerLst = server.getHandlerLst();
            for (ServerHandler handler : handlerLst) {
                if (isTopic) {
                    if (handler.isMemberOfTopic(sendTo)) {
                        String message = "groupChat " + sendTo + " " + body + "\n";
                        handler.send(message);
                        //System.out.println(message);
                    }
                } else {
                    if (handler.getUserName().equalsIgnoreCase(sendTo)) {
                        String message = "message " + username  + " " + msg + ";" + id + "\n";
                        handler.send(message);
                        //System.out.println(message);
                    }
                }
            }
        }

        private void handleLogin(OutputStream out, String[] tokens) throws IOException {
            if (tokens.length == 3) {
                String user_name = tokens[1];
                String password = tokens[2];

//                boolean usr_already_exist = false;
//                
//                for (int i = 0; i < server.handlerLst.size(); i++) {
//                    if (server.handlerLst.get(i).username == null) {
//                        
//                    } else {
//                        if (server.handlerLst.get(i).username.equalsIgnoreCase(user_name)) {
//                            usr_already_exist = true;
//                        }
//                    }
//                }
//                
//                System.out.println("___________________________________________________________________________" + usr_already_exist);
//                
//                if (usr_already_exist == true) {
//                    JOptionPane pane = new JOptionPane("A client is already using this account.", JOptionPane.ERROR_MESSAGE);
//                    final JDialog dlg = pane.createDialog("Account Occupied.");
//                    dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//                    dlg.setVisible(true);
//                    System.exit(0);
//                } else {
                if (user_name.equalsIgnoreCase("karim") && password.equalsIgnoreCase("jPasswordField1")
                        || user_name.equalsIgnoreCase("guest") && password.equalsIgnoreCase("jPasswordField1")
                        || user_name.equalsIgnoreCase("guest1") && password.equalsIgnoreCase("jPasswordField1")
                        || user_name.equalsIgnoreCase("guest2") && password.equalsIgnoreCase("jPasswordField1")
                        || user_name.equalsIgnoreCase("guest3") && password.equalsIgnoreCase("jPasswordField1")
                        || user_name.equalsIgnoreCase("guest4") && password.equalsIgnoreCase("jPasswordField1")) {

                    String msg = "user_logged_in\n";
                    out.write(msg.getBytes());
                    this.username = user_name;
                    txt_area.append(user_name + " logged in.\n");
                    userListModel.addElement(user_name);
                    ArrayList<ServerHandler> hndlrs = server.getHandlerLst();
                    for (ServerHandler handler : hndlrs) {
                        if (handler.getUserName() != null) {
                            if (!user_name.equals(handler.getUserName())) {
                                String msg2 = "online " + handler.getUserName() + "\n";
                                handler.send(msg2);
                                out.write((msg2).getBytes());
                            }
                        }
                    }
                    //send all other online users current user's status
                    String onlineMsg = "online " + user_name + "\n";
                    for (ServerHandler handler : hndlrs) {
                        if (!user_name.equalsIgnoreCase(handler.getUserName())) {
                            handler.send(onlineMsg);
                            out.write((onlineMsg).getBytes());
                        }
                    }
                } else {
                    String msg = "error login\n";
                    System.out.println(msg);
                    out.write(msg.getBytes());
                }
                //}

            } else {
                String msg = "error login\n";
                System.out.println(msg);
                out.write(msg.getBytes());
            }
        }

        private void handleLogOff() throws IOException {
            //server.removeHandler(this);
            //send all other online users current user's status
            List<ServerHandler> handlerLst = server.getHandlerLst();
            String offlineMsg = "offline " + username + "\n";
            for (ServerHandler handler : handlerLst) {
                if (!username.equalsIgnoreCase(handler.getUserName())) {
                    handler.send(offlineMsg);
                    out.write((offlineMsg).getBytes());
                    System.out.println(offlineMsg);
                }
            }
            server.removeHandler(this);
            System.out.println(username + " quit session.");
            txt_area.append("Closing a connection from " + clientSocket + "\n");
            txt_area.append(username + " logged off.\n");
            userListModel.removeElement(username);
            clientSocket.close();
        }

        public void send(String msg) throws IOException {
            if (username != null) {
                out.write(msg.getBytes());
            }
        }

        public boolean isMemberOfTopic(String topic) {
            return topicSet.contains(topic);
        }

        private void handleJoinGroupChat(String[] tokens) {
            if (tokens.length > 1) {
                String topic = tokens[1];
                topicSet.add(topic);
            }
        }

        private void handleLeaveGroupChat(String[] tokens) {
            if (tokens.length > 1) {
                String topic = tokens[1];
                topicSet.remove(topic);
            }
        }

        public String getUserName() {
            return username;
        }
    }
}
