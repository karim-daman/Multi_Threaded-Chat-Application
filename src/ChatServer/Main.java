/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.*/
package ChatServer;

import ChatServer.GUI.ClientGui;
import ChatServer.GUI.ServerGui;

/**
 *
 * @author Karim
 */
public class Main {

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        ServerGui server = new ServerGui();
        server.setContentPane(server.getContentPane());
        server.pack();
        server.setVisible(true);

        ClientGui c1 = new ClientGui();
        c1.setContentPane(c1.getContentPane());
        c1.pack();
        c1.setVisible(true);

        ClientGui c2 = new ClientGui();
        c2.setContentPane(c2.getContentPane());
        c2.pack();
        c2.setVisible(true);

//        ClientGui c3 = new ClientGui();
//        c3.setContentPane(c3.getContentPane());
//        c3.pack();
//        c3.setVisible(true);
//
//        ClientGui c4 = new ClientGui();
//        c4.setContentPane(c4.getContentPane());
//        c4.pack();
//        c4.setVisible(true);
    }
}
