package main;

import function.Client;
import function.Method;
import java.awt.Color;
import java.io.File;
import java.net.ServerSocket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

//Hàm dựng lớp Main thuộc server
public class Main extends javax.swing.JFrame {

    //Khởi tạo các Components cho giao diện đồ họa
    public Main() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdStart = new javax.swing.JButton();
        cmdStop = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt = new javax.swing.JTextArea();
        lbStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        cmdStart.setBackground(new java.awt.Color(102, 255, 102));
        cmdStart.setText("Kết nối Server");
        cmdStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdStartActionPerformed(evt);
            }
        });

        cmdStop.setBackground(new java.awt.Color(255, 153, 153));
        cmdStop.setText("Đóng Server");
        cmdStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdStopActionPerformed(evt);
            }
        });

        txt.setEditable(false);
        txt.setColumns(20);
        txt.setRows(5);
        jScrollPane1.setViewportView(txt);

        lbStatus.setForeground(new java.awt.Color(255, 51, 51));
        lbStatus.setText("Trạng thái Server ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmdStart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 315, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmdStart)
                        .addComponent(cmdStop)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    //Khởi tạo ServerSocket và Thread
    private ServerSocket server;
    private Thread run;
    
    //Hàm khởi động Server
    private void startServer() throws Exception {
        //setClients vào ArrayList thông qua function Method
        Method.setClients(new ArrayList<>());
        File f = new File("data");
        for (File fs : f.listFiles()) {
            fs.delete();
        }
        //khởi tạo thread mới trong multi threading
        run = new Thread(() -> {
            try {
                //Thiết lập cổng kết nối mặc định là 5000 (có thể thay đổi theo giá trị mình muốn)
                server = new ServerSocket(5000);
                //trạng thái Server -> màu xanh lá
                lbStatus.setForeground(Color.GREEN);
                Method.setTxt(txt);
                //Hiển thị nội dung Server đang khởi động thông qua function Method
                txt.setText("Server đang khởi động ...\n");
                //Khi true thì chấp nhận kết nối client với server
                while (true) {
                    new Client(server.accept());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Main.this, e, "Error", JOptionPane.ERROR_MESSAGE);
                //e.printStackTrace();
            }
        });
        run.start();
    }

    
    //Hàm đóng Server 
    private void stopServer() throws Exception {
        // Khởi tạo thông báo Dialog muốn dừng server hay không
        int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn dừng server ngay bây giờ", "Đóng Server", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            //Hiển thị Dialog nếu chọn Yes sẽ thực hiện những dòng sau:
            lbStatus.setForeground(new Color(255, 51, 51));
            //thay đổi màu của trạng thái server từ xanh -> đỏ
            txt.setText("Server hiện đã dừng ...");
            //Hiển thị text: Server hiện đã dừng
            run.interrupt();
            //ngắt kết nối với server
            server.close();
        }
    }
    
    //Hàm hiển thị thông báo xóa dữ liệu tệp trước khi khởi động server
    private void cmdStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStartActionPerformed
        try {
            int c = JOptionPane.showConfirmDialog(this, "Tệp trong dữ liệu sẽ bị xóa khi máy chủ khởi động", "Khởi động Server", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (c == JOptionPane.YES_OPTION) {
                //Nếu chọn YES sẽ trả về function startServer xử lí
                startServer();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_cmdStartActionPerformed

    //Hàm thông báo đóng server khi click vào button Đóng Server
    private void cmdStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStopActionPerformed
        try {
            stopServer();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_cmdStopActionPerformed

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdStart;
    private javax.swing.JButton cmdStop;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JTextArea txt;
    // End of variables declaration//GEN-END:variables
}
