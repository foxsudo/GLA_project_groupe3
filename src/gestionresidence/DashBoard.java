/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gestionresidence;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.TableModel;

/**
 *
 * @author charlyrenard
 */
public class DashBoard extends javax.swing.JFrame {

    public static DashBoard Instance;
    public JLabel lbl;

    /**
     * Creates new form DashBoard
     *
     * @param loginName
     */
    public DashBoard() {
        initComponents();
        Instance = this;
        lbl = lblLoginName;

        showPieChart();
        setStudentDetailsToTable();
        setAllRoomDetailsToTable();
        setDataToCards();
        //lblNbreStudent.setText("  "+String.valueOf(getStudentCount()));
    }

    DefaultTableModel model;

    public void showPieChart() {

        //create dataset
        DefaultPieDataset barDataset = new DefaultPieDataset();

        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS nbreTotal, UPPER(nationalite) AS nationalite "
                    + "FROM utilisateurs "
                    + "WHERE UPPER(fonction) LIKE '%ETUDIANT%' "
                    + "GROUP BY UPPER(nationalite)");
            while (rs.next()) {
                barDataset.setValue(rs.getString("nationalite"), new Double(rs.getDouble("nbreTotal")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //create chart
        JFreeChart piechart = ChartFactory.createPieChart("Students stats by Nationality", barDataset, false, true, false);//explain

        PiePlot piePlot = (PiePlot) piechart.getPlot();

        //changing pie chart blocks colors
        piePlot.setSectionPaint("IPhone 5s", new Color(255, 255, 102));
        piePlot.setSectionPaint("SamSung Grand", new Color(102, 255, 102));
        piePlot.setSectionPaint("MotoG", new Color(255, 102, 153));
        piePlot.setSectionPaint("Nokia Lumia", new Color(0, 204, 204));

        piePlot.setBackgroundPaint(Color.white);

        //create chartPanel to display chart(graph)
        ChartPanel pieChartPanel = new ChartPanel(piechart);
        pnlPieChart.removeAll();
        pnlPieChart.add(pieChartPanel, BorderLayout.CENTER);
        pnlPieChart.validate();
    }

    public void setStudentDetailsToTable() {
        clearTable();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM utilisateurs WHERE UPPER(fonction) LIKE '%ETUDIANT%' ORDER BY matricule");

            while (rs.next()) {
                String matriculeStud = rs.getString("matricule");
                String nomStud = rs.getString("nom");
                String prenomStud = rs.getString("prenom");
                String sexeStud = rs.getString("sexe");
                String nationaliteStud = rs.getString("nationalite");

                // Additional fields for the second table
                String dateNaissance = rs.getString("date_naissance");
                String emailStud = rs.getString("email");
                String fonctionStud = rs.getString("fonction");
                String motDePasse = rs.getString("mot_de_passe");

                Object[] rowDataFirstTable = {matriculeStud, nomStud, prenomStud, sexeStud, nationaliteStud};
                model = (DefaultTableModel) tblStudentDetails.getModel();
                model.addRow(rowDataFirstTable);

                // Second table (more information including password)
                Object[] rowDataSecondTable = {matriculeStud, nomStud, prenomStud, sexeStud, nationaliteStud, emailStud, fonctionStud, dateNaissance, motDePasse};
                DefaultTableModel modelSecondTable = (DefaultTableModel) tblManageStudent.getModel();
                modelSecondTable.addRow(rowDataSecondTable);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setAllRoomDetailsToTable() {

        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT r.nom AS nom_residence, c.numero AS numero_chambre, c.statut AS statut_chambre "
                    + "FROM chambres c "
                    + "JOIN residences r ON c.residence_id = r.id_residence");

            while (rs.next()) {
                String nomResidence = rs.getString("nom_residence");
                String numeroChambre = rs.getString("numero_chambre");
                String statutChambre = rs.getString("statut_chambre");

                Object[] obj = {nomResidence, numeroChambre, statutChambre};
                model = (DefaultTableModel) tblRommStatut.getModel();
                model.addRow(obj);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDataToCards() {
        Statement st = null;
        ResultSet rs = null;

        try {
            Connection conn = DBConnection.getConnection();
            st = conn.createStatement();
            // Use COUNT to get the number of students
            rs = st.executeQuery("SELECT COUNT(*) AS total_students FROM utilisateurs WHERE UPPER(fonction) LIKE '%ETUDIANT%'");
            if (rs.next()) {
                lblNbreStudent.setText("  " + Integer.toString(rs.getInt("total_students")));
            }

            // Use COUNT to get the number of rooms
            rs = st.executeQuery("SELECT COUNT(*) AS total_chambres FROM chambres");
            if (rs.next()) {
                lblNbreRoom.setText("  " + Integer.toString(rs.getInt("total_chambres")));
            }

            rs = st.executeQuery("SELECT COUNT(*) AS total_residences FROM residences");
            if (rs.next()) {
                lblNbreResidence.setText("  " + Integer.toString(rs.getInt("total_residences")));
            }

            rs = st.executeQuery("SELECT COUNT(*) AS chambres_libres FROM chambres WHERE statut = 'libre'");
            if (rs.next()) {
                lblRoomFree.setText("  " + Integer.toString(rs.getInt("chambres_libres")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Methode to add Users (student and others)
    public boolean addStudent() {
        boolean isAdded = false;

        String nom = txtNom.getText().toUpperCase();
        String matricule = txtMatricule.getText().toUpperCase();
        String prenom = txtPrenom.getText().toUpperCase();
        String sexe = cboSexe.getSelectedItem().toString().toUpperCase();
        String nationalite = txtNationalite.getText().toUpperCase();
        String mail = txtMail.getText();
        String fonction = txtFonction.getText().toUpperCase();
        String password = txtPassword.getText();
        java.util.Date date = txtDate.getDatoFecha();

        Long l1 = date.getTime();
        java.sql.Date fDate = new java.sql.Date(l1);

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO public.utilisateurs(nom, prenom, email, mot_de_passe, fonction, nationalite, date_naissance, sexe, matricule)  VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, mail);
            pst.setString(4, password);
            pst.setString(5, fonction);
            pst.setString(6, nationalite);
            pst.setDate(7, fDate);
            pst.setString(8, sexe);
            pst.setString(9, matricule);

            int rowCount = pst.executeUpdate();

            if (rowCount > 0) {
                isAdded = true;
            } else {
                isAdded = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isAdded;
    }
    //Methode to Update users

    public boolean updateUser() {
        boolean isUpdated = false;

        String nom = txtNom.getText().toUpperCase();
        String matricule = txtMatricule.getText().toUpperCase();
        String prenom = txtPrenom.getText().toUpperCase();
        String sexe = cboSexe.getSelectedItem().toString().toUpperCase();
        String nationalite = txtNationalite.getText().toUpperCase();
        String mail = txtMail.getText();
        String fonction = txtFonction.getText().toUpperCase();
        String password = new String(txtPassword.getPassword());  // Use getPassword if it's JPasswordField
        java.util.Date date = txtDate.getDatoFecha();

        if (date == null) {
            // Handle null date (you can decide to throw an exception or handle accordingly)
            JOptionPane.showMessageDialog(this, "Date is null, cannot update user !");
            return false;
        }

        java.sql.Date fDate = new java.sql.Date(date.getTime());

        String sql = "UPDATE public.utilisateurs SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, fonction = ?, nationalite = ?, date_naissance = ?, sexe = ? WHERE matricule = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, mail);
            pst.setString(4, password);  // Ensure the password is securely handled and hashed
            pst.setString(5, fonction);
            pst.setString(6, nationalite);
            pst.setDate(7, fDate);
            pst.setString(8, sexe);
            pst.setString(9, matricule);

            int rowCount = pst.executeUpdate();
            isUpdated = rowCount > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // You might want to use a logging framework
        }

        return isUpdated;
    }
//Methode to delete user

    public boolean deleteUser(String matricule) {
        boolean isDeleted = false;

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM public.utilisateurs WHERE matricule = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            // Set the matricule parameter
            pst.setString(1, matricule);

            int rowCount = pst.executeUpdate();

            // Check if any row was affected
            if (rowCount > 0) {
                isDeleted = true;
            }

        } catch (SQLException e) {
            e.printStackTrace(); // You can use logging instead in a real application
        }

        return isDeleted;
    }

    //Methode to clear Table
    public void clearTable() {
        model = (DefaultTableModel) tblManageStudent.getModel();
        model.setRowCount(0);
    }
    //Methode to clear user form

    public void clearUserForm() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtNationalite.setText("");
        txtMail.setText("");
        txtFonction.setText("");
        txtPassword.setText("");
        txtDate.setDatoFecha(null);
        cboSexe.setSelectedIndex(0);
        txtMatricule.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblLoginName = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        pnlHomePage = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        pnlResidence = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        pnlRoom = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        pnlStudent = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        pnlChat = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        pnlSupport = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        pnlLogout = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        pnlStats = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlDashboard = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblNbreStudent = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblNbreRoom = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lblNbreResidence = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lblRoomFree = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRommStatut = new rojeru_san.complementos.RSTableMetro();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStudentDetails = new rojeru_san.complementos.RSTableMetro();
        pnlPieChart = new javax.swing.JPanel();
        pnlManageResidence = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        pnlManageStudent = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        txtMatricule = new app.bolivia.swing.JCTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtNom = new app.bolivia.swing.JCTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtPrenom = new app.bolivia.swing.JCTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtNationalite = new app.bolivia.swing.JCTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        cboSexe = new javax.swing.JComboBox<>();
        txtPassword = new javax.swing.JPasswordField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        txtMail = new app.bolivia.swing.JCTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        txtFonction = new app.bolivia.swing.JCTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        txtDate = new rojeru_san.componentes.RSDateChooser();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        btnDelete = new necesario.RSMaterialButtonCircle();
        btnAdd = new necesario.RSMaterialButtonCircle();
        btnUpdate = new necesario.RSMaterialButtonCircle();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblManageStudent = new rojeru_san.complementos.RSTableMetro();
        jLabel2 = new javax.swing.JLabel();
        pnlManageRoom = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        pnlManageChat = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        pnlManageSupport = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        pnlManageStats = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(102, 102, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_menu_48px_1.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 50));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Student Residence Management ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/male_user_50px.png"))); // NOI18N
        jLabel4.setText("photo");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 10, 50, 50));

        jLabel12.setFont(new java.awt.Font("YuGothic", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Welcom,");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 20, 80, 40));

        lblLoginName.setFont(new java.awt.Font("YuGothic", 1, 18)); // NOI18N
        lblLoginName.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lblLoginName, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 20, 120, 40));

        jLabel24.setFont(new java.awt.Font("Helvetica Neue", 0, 65)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(51, 51, 51));
        jLabel24.setText("l");
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 0, 20, 70));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1350, 70));

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHomePage.setBackground(new java.awt.Color(255, 51, 51));
        pnlHomePage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlHomePageMousePressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("YuGothic", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Home_26px_2.png"))); // NOI18N
        jLabel11.setText("   Home Page");

        javax.swing.GroupLayout pnlHomePageLayout = new javax.swing.GroupLayout(pnlHomePage);
        pnlHomePage.setLayout(pnlHomePageLayout);
        pnlHomePageLayout.setHorizontalGroup(
            pnlHomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHomePageLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        pnlHomePageLayout.setVerticalGroup(
            pnlHomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHomePageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.add(pnlHomePage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 220, 60));

        pnlResidence.setBackground(new java.awt.Color(102, 102, 102));
        pnlResidence.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlResidenceMousePressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("YuGothic", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Real Estate(3).png"))); // NOI18N
        jLabel6.setText("   Manage Residences");

        javax.swing.GroupLayout pnlResidenceLayout = new javax.swing.GroupLayout(pnlResidence);
        pnlResidence.setLayout(pnlResidenceLayout);
        pnlResidenceLayout.setHorizontalGroup(
            pnlResidenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlResidenceLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlResidenceLayout.setVerticalGroup(
            pnlResidenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlResidenceLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.add(pnlResidence, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, -1, -1));

        pnlRoom.setBackground(new java.awt.Color(102, 102, 102));
        pnlRoom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlRoomMousePressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("YuGothic", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Room(2).png"))); // NOI18N
        jLabel7.setText("   Manage Rooms");

        javax.swing.GroupLayout pnlRoomLayout = new javax.swing.GroupLayout(pnlRoom);
        pnlRoom.setLayout(pnlRoomLayout);
        pnlRoomLayout.setHorizontalGroup(
            pnlRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoomLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        pnlRoomLayout.setVerticalGroup(
            pnlRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRoomLayout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.add(pnlRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, -1, -1));

        pnlStudent.setBackground(new java.awt.Color(102, 102, 102));
        pnlStudent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlStudentMousePressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("YuGothic", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Read_Online_26px.png"))); // NOI18N
        jLabel5.setText("   Manage Students");

        javax.swing.GroupLayout pnlStudentLayout = new javax.swing.GroupLayout(pnlStudent);
        pnlStudent.setLayout(pnlStudentLayout);
        pnlStudentLayout.setHorizontalGroup(
            pnlStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStudentLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        pnlStudentLayout.setVerticalGroup(
            pnlStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStudentLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(pnlStudent, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, -1, -1));

        pnlChat.setBackground(new java.awt.Color(102, 102, 102));
        pnlChat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlChatMousePressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("YuGothic", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Conference_26px.png"))); // NOI18N
        jLabel8.setText("   Chat");

        javax.swing.GroupLayout pnlChatLayout = new javax.swing.GroupLayout(pnlChat);
        pnlChat.setLayout(pnlChatLayout);
        pnlChatLayout.setHorizontalGroup(
            pnlChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChatLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(87, Short.MAX_VALUE))
        );
        pnlChatLayout.setVerticalGroup(
            pnlChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChatLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.add(pnlChat, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, -1, -1));

        pnlSupport.setBackground(new java.awt.Color(102, 102, 102));
        pnlSupport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlSupportMousePressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("YuGothic", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Sell_26px.png"))); // NOI18N
        jLabel9.setText("   Support");

        javax.swing.GroupLayout pnlSupportLayout = new javax.swing.GroupLayout(pnlSupport);
        pnlSupport.setLayout(pnlSupportLayout);
        pnlSupportLayout.setHorizontalGroup(
            pnlSupportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSupportLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(77, Short.MAX_VALUE))
        );
        pnlSupportLayout.setVerticalGroup(
            pnlSupportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSupportLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.add(pnlSupport, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 340, -1, 60));

        pnlLogout.setBackground(new java.awt.Color(51, 51, 51));

        jLabel10.setFont(new java.awt.Font("YuGothic", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Exit_26px.png"))); // NOI18N
        jLabel10.setText("   Logout");

        javax.swing.GroupLayout pnlLogoutLayout = new javax.swing.GroupLayout(pnlLogout);
        pnlLogout.setLayout(pnlLogoutLayout);
        pnlLogoutLayout.setHorizontalGroup(
            pnlLogoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLogoutLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );
        pnlLogoutLayout.setVerticalGroup(
            pnlLogoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLogoutLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel10)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel2.add(pnlLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 670, -1, 60));

        pnlStats.setBackground(new java.awt.Color(102, 102, 102));
        pnlStats.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlStatsMousePressed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("YuGothic", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Sell_26px.png"))); // NOI18N
        jLabel13.setText("   Stats");

        javax.swing.GroupLayout pnlStatsLayout = new javax.swing.GroupLayout(pnlStats);
        pnlStats.setLayout(pnlStatsLayout);
        pnlStatsLayout.setHorizontalGroup(
            pnlStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStatsLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(77, Short.MAX_VALUE))
        );
        pnlStatsLayout.setVerticalGroup(
            pnlStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStatsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel2.add(pnlStats, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 400, -1, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 220, 730));

        pnlDashboard.setBackground(new java.awt.Color(255, 255, 255));
        pnlDashboard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 51, 51)));
        jPanel3.setPreferredSize(new java.awt.Dimension(260, 1));

        lblNbreStudent.setFont(new java.awt.Font("Silom", 0, 48)); // NOI18N
        lblNbreStudent.setForeground(new java.awt.Color(51, 51, 51));
        lblNbreStudent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_People_50px.png"))); // NOI18N
        lblNbreStudent.setText("  ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblNbreStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblNbreStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pnlDashboard.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 230, 140));

        jLabel14.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 51, 51));
        jLabel14.setText("Student Details");
        pnlDashboard.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 470, 220, 30));

        jLabel16.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setText("No. Of Rooms");
        pnlDashboard.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 20, 220, -1));

        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(102, 102, 255)));
        jPanel4.setPreferredSize(new java.awt.Dimension(260, 1));

        lblNbreRoom.setFont(new java.awt.Font("Silom", 0, 48)); // NOI18N
        lblNbreRoom.setForeground(new java.awt.Color(51, 51, 51));
        lblNbreRoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_People_50px.png"))); // NOI18N
        lblNbreRoom.setText("  ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addComponent(lblNbreRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblNbreRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pnlDashboard.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, 230, 140));

        jLabel17.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 51, 51));
        jLabel17.setText("No. Of Residences");
        pnlDashboard.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 20, 240, -1));

        jPanel5.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 51, 51)));
        jPanel5.setPreferredSize(new java.awt.Dimension(260, 1));

        lblNbreResidence.setFont(new java.awt.Font("Silom", 0, 48)); // NOI18N
        lblNbreResidence.setForeground(new java.awt.Color(51, 51, 51));
        lblNbreResidence.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_People_50px.png"))); // NOI18N
        lblNbreResidence.setText("  ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lblNbreResidence, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblNbreResidence, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        pnlDashboard.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 60, 230, 140));

        jLabel20.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(51, 51, 51));
        jLabel20.setText("No. Of RoomFree");
        pnlDashboard.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 20, 220, -1));

        jPanel6.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(102, 102, 255)));
        jPanel6.setPreferredSize(new java.awt.Dimension(260, 1));

        lblRoomFree.setFont(new java.awt.Font("Silom", 0, 48)); // NOI18N
        lblRoomFree.setForeground(new java.awt.Color(51, 51, 51));
        lblRoomFree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_People_50px.png"))); // NOI18N
        lblRoomFree.setText("  ");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lblRoomFree, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblRoomFree, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        pnlDashboard.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 60, 230, 140));

        tblRommStatut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Residence", "No Chambre", "Statut"
            }
        ));
        tblRommStatut.setColorBackgoundHead(new java.awt.Color(102, 102, 255));
        tblRommStatut.setColorBordeFilas(new java.awt.Color(102, 102, 255));
        tblRommStatut.setRowHeight(25);
        tblRommStatut.setRowSelectionAllowed(false);
        jScrollPane1.setViewportView(tblRommStatut);

        pnlDashboard.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, 500, 190));

        jLabel21.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(51, 51, 51));
        jLabel21.setText("No. Of Students");
        pnlDashboard.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 220, 30));

        jLabel22.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(51, 51, 51));
        jLabel22.setText("Rooms statut");
        pnlDashboard.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 230, 220, 30));

        tblStudentDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Matricule", "Nom", "Prenom", "Sexe", "Nationalité"
            }
        ));
        tblStudentDetails.setColorBackgoundHead(new java.awt.Color(102, 102, 255));
        tblStudentDetails.setColorBordeFilas(new java.awt.Color(102, 102, 255));
        tblStudentDetails.setRowHeight(25);
        tblStudentDetails.setRowSelectionAllowed(false);
        jScrollPane2.setViewportView(tblStudentDetails);

        pnlDashboard.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 520, 1050, 190));

        pnlPieChart.setLayout(new java.awt.BorderLayout());
        pnlDashboard.add(pnlPieChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 220, 445, 290));

        jTabbedPane1.addTab("tab1", pnlDashboard);

        jLabel25.setText("MRes");

        javax.swing.GroupLayout pnlManageResidenceLayout = new javax.swing.GroupLayout(pnlManageResidence);
        pnlManageResidence.setLayout(pnlManageResidenceLayout);
        pnlManageResidenceLayout.setHorizontalGroup(
            pnlManageResidenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageResidenceLayout.createSequentialGroup()
                .addGap(351, 351, 351)
                .addComponent(jLabel25)
                .addContainerGap(745, Short.MAX_VALUE))
        );
        pnlManageResidenceLayout.setVerticalGroup(
            pnlManageResidenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageResidenceLayout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(jLabel25)
                .addContainerGap(527, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab2", pnlManageResidence);

        pnlManageStudent.setBackground(new java.awt.Color(255, 255, 255));
        pnlManageStudent.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel7.setBackground(new java.awt.Color(102, 102, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(1130, 400));

        jLabel33.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("Matricule :");

        txtMatricule.setBackground(new java.awt.Color(102, 102, 255));
        txtMatricule.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtMatricule.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        txtMatricule.setPlaceholder("Entrer matricule...");
        txtMatricule.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMatriculeFocusLost(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Matricule.png"))); // NOI18N

        jLabel26.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Nom :");

        txtNom.setBackground(new java.awt.Color(102, 102, 255));
        txtNom.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtNom.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        txtNom.setPlaceholder("Entrer nom...");
        txtNom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNomFocusLost(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Account_50px.png"))); // NOI18N

        jLabel28.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Prenom :");

        txtPrenom.setBackground(new java.awt.Color(102, 102, 255));
        txtPrenom.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtPrenom.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        txtPrenom.setPlaceholder("Entrer prenom...");

        jLabel29.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Account_50px.png"))); // NOI18N

        jLabel30.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Nationalité :");

        txtNationalite.setBackground(new java.awt.Color(102, 102, 255));
        txtNationalite.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtNationalite.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        txtNationalite.setPlaceholder("Entrer nationalité...");

        jLabel31.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Globe.png"))); // NOI18N

        jLabel34.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("Password :");

        jLabel35.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("Sexe :");

        cboSexe.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select--", "FEMININ", "MASCULIN", "Feminin", "Masculin" }));

        txtPassword.setBackground(new java.awt.Color(102, 102, 255));
        txtPassword.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        txtPassword.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));

        jLabel36.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Secure_50px.png"))); // NOI18N

        jLabel37.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("Email :");

        txtMail.setBackground(new java.awt.Color(102, 102, 255));
        txtMail.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtMail.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        txtMail.setPlaceholder("Enter Email...");

        jLabel38.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Email(2).png"))); // NOI18N

        jLabel39.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setText("Fonction :");

        txtFonction.setBackground(new java.awt.Color(102, 102, 255));
        txtFonction.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txtFonction.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        txtFonction.setPlaceholder("Enter contact number...");

        jLabel40.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Role.png"))); // NOI18N

        jLabel41.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setText("Date de naissance :");

        txtDate.setBackground(new java.awt.Color(102, 102, 255));
        txtDate.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(102, 102, 255)));
        txtDate.setColorBackground(new java.awt.Color(102, 102, 255));
        txtDate.setColorButtonHover(new java.awt.Color(0, 0, 0));
        txtDate.setFormatoFecha("dd/MM/yyyy");
        txtDate.setFuente(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtDate.setPlaceholder("Select birthday date");

        jLabel42.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 255, 255));
        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Birth Date.png"))); // NOI18N

        jLabel43.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 255, 255));
        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Secure_50px.png"))); // NOI18N

        btnDelete.setBackground(new java.awt.Color(255, 51, 51));
        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnAdd.setBackground(new java.awt.Color(255, 51, 51));
        btnAdd.setText("ADD");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(255, 51, 51));
        btnUpdate.setText("UPDATE");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMatricule, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrenom, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addGap(10, 10, 10)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNationalite, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboSexe, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(42, 42, 42))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMail, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFonction, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(30, 30, 30))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addGap(8, 8, 8)
                                .addComponent(txtMail, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addGap(8, 8, 8)
                                .addComponent(txtFonction, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel41)
                                .addGap(8, 8, 8)
                                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(txtMatricule, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel33))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jLabel27))
                                .addComponent(jLabel26)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jLabel29))
                                .addComponent(jLabel28)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(txtPrenom, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jLabel31))
                                .addComponent(jLabel30)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(txtNationalite, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addComponent(jLabel34)
                                    .addGap(8, 8, 8)
                                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel43))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addComponent(jLabel35)
                                    .addGap(8, 8, 8)
                                    .addComponent(cboSexe, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel36)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
        );

        pnlManageStudent.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 1130, 360));

        jPanel8.setBackground(new java.awt.Color(51, 51, 51));
        jPanel8.setPreferredSize(new java.awt.Dimension(1130, 10));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1130, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        pnlManageStudent.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        tblManageStudent.setForeground(new java.awt.Color(102, 102, 102));
        tblManageStudent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Matricule", "Nom", "Prenom", "Sexe", "Nationalité", "Email", "Fonction", "D. Naissance", "Password"
            }
        ));
        tblManageStudent.setColorBackgoundHead(new java.awt.Color(112, 112, 255));
        tblManageStudent.setColorBordeFilas(new java.awt.Color(112, 112, 255));
        tblManageStudent.setRowHeight(25);
        tblManageStudent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblManageStudentMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblManageStudent);

        pnlManageStudent.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, 1110, 270));

        jLabel2.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        jLabel2.setText("Studends deatils");
        pnlManageStudent.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 240, 40));

        jTabbedPane1.addTab("tab3", pnlManageStudent);

        jLabel23.setText("MRoom");

        javax.swing.GroupLayout pnlManageRoomLayout = new javax.swing.GroupLayout(pnlManageRoom);
        pnlManageRoom.setLayout(pnlManageRoomLayout);
        pnlManageRoomLayout.setHorizontalGroup(
            pnlManageRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageRoomLayout.createSequentialGroup()
                .addGap(394, 394, 394)
                .addComponent(jLabel23)
                .addContainerGap(691, Short.MAX_VALUE))
        );
        pnlManageRoomLayout.setVerticalGroup(
            pnlManageRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageRoomLayout.createSequentialGroup()
                .addGap(219, 219, 219)
                .addComponent(jLabel23)
                .addContainerGap(489, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab4", pnlManageRoom);

        jLabel19.setText("chat");

        javax.swing.GroupLayout pnlManageChatLayout = new javax.swing.GroupLayout(pnlManageChat);
        pnlManageChat.setLayout(pnlManageChatLayout);
        pnlManageChatLayout.setHorizontalGroup(
            pnlManageChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageChatLayout.createSequentialGroup()
                .addGap(357, 357, 357)
                .addComponent(jLabel19)
                .addContainerGap(748, Short.MAX_VALUE))
        );
        pnlManageChatLayout.setVerticalGroup(
            pnlManageChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageChatLayout.createSequentialGroup()
                .addGap(183, 183, 183)
                .addComponent(jLabel19)
                .addContainerGap(525, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab5", pnlManageChat);

        jLabel18.setText("support");

        javax.swing.GroupLayout pnlManageSupportLayout = new javax.swing.GroupLayout(pnlManageSupport);
        pnlManageSupport.setLayout(pnlManageSupportLayout);
        pnlManageSupportLayout.setHorizontalGroup(
            pnlManageSupportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageSupportLayout.createSequentialGroup()
                .addGap(387, 387, 387)
                .addComponent(jLabel18)
                .addContainerGap(698, Short.MAX_VALUE))
        );
        pnlManageSupportLayout.setVerticalGroup(
            pnlManageSupportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageSupportLayout.createSequentialGroup()
                .addGap(172, 172, 172)
                .addComponent(jLabel18)
                .addContainerGap(536, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab6", pnlManageSupport);

        jLabel15.setText("Stats");

        javax.swing.GroupLayout pnlManageStatsLayout = new javax.swing.GroupLayout(pnlManageStats);
        pnlManageStats.setLayout(pnlManageStatsLayout);
        pnlManageStatsLayout.setHorizontalGroup(
            pnlManageStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageStatsLayout.createSequentialGroup()
                .addGap(423, 423, 423)
                .addComponent(jLabel15)
                .addContainerGap(677, Short.MAX_VALUE))
        );
        pnlManageStatsLayout.setVerticalGroup(
            pnlManageStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageStatsLayout.createSequentialGroup()
                .addGap(218, 218, 218)
                .addComponent(jLabel15)
                .addContainerGap(490, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab7", pnlManageStats);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 1130, 760));

        setSize(new java.awt.Dimension(1350, 828));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void pnlHomePageMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlHomePageMousePressed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(0);
        setStudentDetailsToTable();
        setColor(pnlHomePage);
        resetColor(pnlResidence);
        resetColor(pnlRoom);
        resetColor(pnlStudent);
        resetColor(pnlChat);
        resetColor(pnlSupport);
        resetColor(pnlStats);
    }//GEN-LAST:event_pnlHomePageMousePressed

    private void pnlResidenceMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlResidenceMousePressed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(1);
        setStudentDetailsToTable();
        resetColor(pnlHomePage);
        setColor(pnlResidence);
        resetColor(pnlRoom);
        resetColor(pnlStudent);
        resetColor(pnlChat);
        resetColor(pnlSupport);
        resetColor(pnlStats);
    }//GEN-LAST:event_pnlResidenceMousePressed

    private void pnlRoomMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlRoomMousePressed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(3);
        setStudentDetailsToTable();
        resetColor(pnlHomePage);
        resetColor(pnlResidence);
        setColor(pnlRoom);
        resetColor(pnlStudent);
        resetColor(pnlChat);
        resetColor(pnlSupport);
        resetColor(pnlStats);
    }//GEN-LAST:event_pnlRoomMousePressed

    private void pnlStudentMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlStudentMousePressed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(2);
        setStudentDetailsToTable();
        resetColor(pnlHomePage);
        resetColor(pnlResidence);
        resetColor(pnlRoom);
        setColor(pnlStudent);
        resetColor(pnlChat);
        resetColor(pnlSupport);
    }//GEN-LAST:event_pnlStudentMousePressed

    private void pnlChatMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlChatMousePressed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(4);
        setStudentDetailsToTable();
        resetColor(pnlHomePage);
        resetColor(pnlResidence);
        resetColor(pnlRoom);
        resetColor(pnlStudent);
        setColor(pnlChat);
        resetColor(pnlSupport);
        resetColor(pnlStats);
    }//GEN-LAST:event_pnlChatMousePressed

    private void pnlSupportMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlSupportMousePressed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(5);
        setStudentDetailsToTable();
        resetColor(pnlHomePage);
        resetColor(pnlResidence);
        resetColor(pnlRoom);
        resetColor(pnlStudent);
        resetColor(pnlChat);
        setColor(pnlSupport);
        resetColor(pnlStats);
    }//GEN-LAST:event_pnlSupportMousePressed

    private void pnlStatsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlStatsMousePressed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(6);
        setStudentDetailsToTable();
        resetColor(pnlHomePage);
        resetColor(pnlResidence);
        resetColor(pnlRoom);
        resetColor(pnlStudent);
        resetColor(pnlChat);
        resetColor(pnlSupport);
        setColor(pnlStats);
    }//GEN-LAST:event_pnlStatsMousePressed

    private void txtMatriculeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMatriculeFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMatriculeFocusLost

    private void txtNomFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNomFocusLost
        String nom = txtNom.getText();
        if (!txtNom.getText().isEmpty()) {
            try {
                Class.forName("org.postgresql.Driver");
                Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gestion_residence", "postgres", "Reinge");
                String query = "SELECT * FROM public.utilisateurs WHERE nom = '" + nom + "'";
                PreparedStatement pst = conn.prepareStatement(query);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "username already exist !", "Message", JOptionPane.INFORMATION_MESSAGE);
                    txtNom.setForeground(Color.red);
                } else {
                    txtNom.setForeground(Color.black);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            txtNom.setForeground(Color.black);
        }
    }//GEN-LAST:event_txtNomFocusLost

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if (addStudent() == true) {
            JOptionPane.showMessageDialog(this, "Student added !");
            //clearTable();
            setStudentDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Student addition Failed !");
        }

        clearUserForm();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (updateUser() == true) {
            JOptionPane.showMessageDialog(this, "Student updated !");
            //clearTable();
            setStudentDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Student updation Failed !");
        }
        clearUserForm();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed

        String matricule = txtMatricule.getText();  // Assuming you have a field for matricule
        boolean success = deleteUser(matricule);

        if (success) {
            JOptionPane.showMessageDialog(this, "Student deleted successfully !");
            setStudentDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Student deletion Failed !");
        }
        clearUserForm();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void tblManageStudentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblManageStudentMouseClicked

        int rowNo = tblManageStudent.getSelectedRow();
        TableModel modelT = tblManageStudent.getModel();

        txtMatricule.setText(modelT.getValueAt(rowNo, 0).toString());
        txtNom.setText(modelT.getValueAt(rowNo, 1).toString());
        txtPrenom.setText(modelT.getValueAt(rowNo, 2).toString());
        cboSexe.setSelectedItem(modelT.getValueAt(rowNo, 3).toString());
        txtNationalite.setText(modelT.getValueAt(rowNo, 4).toString());
        txtMail.setText(modelT.getValueAt(rowNo, 5).toString());
        txtFonction.setText(modelT.getValueAt(rowNo, 6).toString());
        // Retrieve the date from the JTable and parse it
        String dateStr = modelT.getValueAt(rowNo, 7).toString();  // The date in the format YYYY-MM-DD
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Format in JTable

        try {
            java.util.Date utilDate = sdf.parse(dateStr);  // Parse the date from String
            txtDate.setDatoFecha(utilDate);  // Set the date in RSDateChooser
        } catch (ParseException e) {
            e.printStackTrace();  // Handle parsing exception
        }
        txtPassword.setText(model.getValueAt(rowNo, 8).toString());

    }//GEN-LAST:event_tblManageStudentMouseClicked

    void setColor(JPanel panel) {
        panel.setBackground(new Color(255, 51, 51));
    }

    void resetColor(JPanel panel) {
        panel.setBackground(new Color(102, 102, 102));
    }

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DashBoard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private necesario.RSMaterialButtonCircle btnAdd;
    private necesario.RSMaterialButtonCircle btnDelete;
    private necesario.RSMaterialButtonCircle btnUpdate;
    private javax.swing.JComboBox<String> cboSexe;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    public javax.swing.JLabel lblLoginName;
    private javax.swing.JLabel lblNbreResidence;
    private javax.swing.JLabel lblNbreRoom;
    private javax.swing.JLabel lblNbreStudent;
    private javax.swing.JLabel lblRoomFree;
    private javax.swing.JPanel pnlChat;
    private javax.swing.JPanel pnlDashboard;
    private javax.swing.JPanel pnlHomePage;
    private javax.swing.JPanel pnlLogout;
    private javax.swing.JPanel pnlManageChat;
    private javax.swing.JPanel pnlManageResidence;
    private javax.swing.JPanel pnlManageRoom;
    private javax.swing.JPanel pnlManageStats;
    private javax.swing.JPanel pnlManageStudent;
    private javax.swing.JPanel pnlManageSupport;
    private javax.swing.JPanel pnlPieChart;
    private javax.swing.JPanel pnlResidence;
    private javax.swing.JPanel pnlRoom;
    private javax.swing.JPanel pnlStats;
    private javax.swing.JPanel pnlStudent;
    private javax.swing.JPanel pnlSupport;
    private rojeru_san.complementos.RSTableMetro tblManageStudent;
    private rojeru_san.complementos.RSTableMetro tblRommStatut;
    private rojeru_san.complementos.RSTableMetro tblStudentDetails;
    private rojeru_san.componentes.RSDateChooser txtDate;
    private app.bolivia.swing.JCTextField txtFonction;
    private app.bolivia.swing.JCTextField txtMail;
    private app.bolivia.swing.JCTextField txtMatricule;
    private app.bolivia.swing.JCTextField txtNationalite;
    private app.bolivia.swing.JCTextField txtNom;
    private javax.swing.JPasswordField txtPassword;
    private app.bolivia.swing.JCTextField txtPrenom;
    // End of variables declaration//GEN-END:variables
}
