package payroll;

import java.awt.HeadlessException;
import javax.swing.JOptionPane;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;



public class loginForm extends javax.swing.JFrame {
    
MyConnection con;//to establish connection
PreparedStatement pst;//pre-compile the query
ResultSet rs;//to execute the query

    public loginForm() {
        initComponents();
        clearbtn.addActionListener(this::clearbtnActionPerformed);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        header = new javax.swing.JPanel();
        titlename = new javax.swing.JLabel();
        footer = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        rolelbl = new javax.swing.JLabel();
        usernamelbl = new javax.swing.JLabel();
        passwordlbl = new javax.swing.JLabel();
        usernametxt = new javax.swing.JTextField();
        clearbtn = new javax.swing.JButton();
        loginbtn = new javax.swing.JButton();
        rolecmb = new javax.swing.JComboBox<>();
        passwordtxt = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(500, 500));
        setMinimumSize(new java.awt.Dimension(500, 500));
        setResizable(false);

        mainPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(102, 102, 102)));
        mainPanel.setMaximumSize(new java.awt.Dimension(500, 500));
        mainPanel.setMinimumSize(new java.awt.Dimension(500, 500));
        mainPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        header.setBackground(new java.awt.Color(102, 102, 102));

        titlename.setFont(new java.awt.Font("Bahnschrift", 0, 24)); // NOI18N
        titlename.setForeground(new java.awt.Color(255, 255, 255));
        titlename.setLabelFor(header);
        titlename.setText("MotorPH's Employee and Payroll System");

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(titlename)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(titlename, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        mainPanel.add(header, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 500, 80));

        footer.setBackground(new java.awt.Color(102, 102, 102));

        jLabel1.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("<html>Office Hours: 8:30am–5:30pm, Monday through Saturday<br>Office Address: 7 Jupiter Avenue cor. F. Sandoval Jr., Bagong Nayon, Quezon City<br>Phone: (028) 911-5071 / (028) 911-5072 / (028) 911-5073<br>Email: corporate@motorph.com </html>");

        jLabel5.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("<html> Submitted by: Abegail A. Clemente<br>Section: H2102<br>MO-IT110 - Object-oriented Programming </html>");

        javax.swing.GroupLayout footerLayout = new javax.swing.GroupLayout(footer);
        footer.setLayout(footerLayout);
        footerLayout.setHorizontalGroup(
            footerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(footerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(footerLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(footerLayout.createSequentialGroup()
                        .addGroup(footerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(footerLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 169, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        footerLayout.setVerticalGroup(
            footerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(footerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(44, 44, 44))
        );

        mainPanel.add(footer, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 290, 500, 210));

        rolelbl.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        rolelbl.setText("Role:");
        mainPanel.add(rolelbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 180, -1, 23));

        usernamelbl.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        usernamelbl.setText("Username:");
        mainPanel.add(usernamelbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, 80, 23));

        passwordlbl.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        passwordlbl.setText("Password:");
        mainPanel.add(passwordlbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 150, -1, 23));

        usernametxt.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        usernametxt.setToolTipText("");
        usernametxt.setName(""); // NOI18N
        usernametxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernametxtActionPerformed(evt);
            }
        });
        mainPanel.add(usernametxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 120, 190, 25));

        clearbtn.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.borderColor"));
        clearbtn.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        clearbtn.setText("Clear");
        clearbtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        clearbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clearbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearbtnActionPerformed(evt);
            }
        });
        mainPanel.add(clearbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 220, 66, 24));

        loginbtn.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.borderColor"));
        loginbtn.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        loginbtn.setText("Login");
        loginbtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loginbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        loginbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginbtnMouseClicked(evt);
            }
        });
        loginbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginbtnActionPerformed(evt);
            }
        });
        mainPanel.add(loginbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 220, 66, 24));

        rolecmb.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        rolecmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Non-admin" }));
        rolecmb.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rolecmb.setName(""); // NOI18N
        mainPanel.add(rolecmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 180, 190, 25));

        passwordtxt.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        passwordtxt.setName(""); // NOI18N
        mainPanel.add(passwordtxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 150, 190, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void usernametxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernametxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernametxtActionPerformed

    private void clearbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearbtnActionPerformed
        // TODO add your handling code here:
            // Clear the text fields
    usernametxt.setText("");
    passwordtxt.setText("");
    rolecmb.setSelectedItem("Admin");
    }//GEN-LAST:event_clearbtnActionPerformed

    private void loginbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginbtnActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_loginbtnActionPerformed

    private void loginbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginbtnMouseClicked
         // TODO add your handling code here:
    String untxt = usernametxt.getText();//get the typed username in username textfield
    String pass = String.valueOf(passwordtxt.getPassword());
    String role = rolecmb.getSelectedItem().toString();

    if (untxt.equals("") || pass.equals("") || role.equals("Select")) {//No input in textfields
        JOptionPane.showMessageDialog(rootPane, "Some Fields are Empty.", "Error", JOptionPane.ERROR_MESSAGE);
    } else {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//jdbc
            MyConnection myConnection = MyConnection.getInstance();
            Connection con = myConnection.connect();
            pst = con.prepareStatement("select * from login where username = ? and password = ? and role = ?");//select the username, password and role in mysql table

            pst.setString(1, untxt);
            pst.setString(2, pass);
            pst.setString(3, role);

            rs = pst.executeQuery();

            if (rs.next()) {
                String userRole = rs.getString("role");
                if (role.equalsIgnoreCase("Admin") && userRole.equalsIgnoreCase("Admin")) {
                    AdminPortal ad = new AdminPortal(untxt);//redirect to Admin Page if Role = Admin
                    ad.setVisible(true);
                    setVisible(false);

                    
                } else if (role.equalsIgnoreCase("Non-admin") && userRole.equalsIgnoreCase("Non-admin")) {
                    NonAdminPortal nonAdminPortal = new NonAdminPortal(untxt); //redirect to Non-Admin Page if Role = Non-Admin
                    nonAdminPortal.setVisible(true);
                    setVisible(false);

                } else {
                    // Notify the user about invalid role
                    JOptionPane.showMessageDialog(rootPane, "Invalid role for the selected user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Notify the user about unsuccessful login
                JOptionPane.showMessageDialog(rootPane, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException | ClassNotFoundException | SQLException ex) {//SQL error
            JOptionPane.showMessageDialog(rootPane, "Error during login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_loginbtnMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new loginForm().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearbtn;
    private javax.swing.JPanel footer;
    private javax.swing.JPanel header;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton loginbtn;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel passwordlbl;
    private javax.swing.JPasswordField passwordtxt;
    private javax.swing.JComboBox<String> rolecmb;
    private javax.swing.JLabel rolelbl;
    private javax.swing.JLabel titlename;
    private javax.swing.JLabel usernamelbl;
    private javax.swing.JTextField usernametxt;
    // End of variables declaration//GEN-END:variables
}
