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
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Transport;

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
        setResidenceDetailsToTable();
        setCodeResidenceToChbreForm();
        setRoomsDetailsToTable();
        setNumeroChbreToAssignForm();
        setMatricEtudiantToAssignForm();
        setMatricEtudiantToPaiementForm();
        setAssignDetailsToTable();
        setPaiementDetailsToTable();
        txtFrom.setText("teamglap27@gmail.com");
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
                barDataset.setValue(rs.getString("nationalite"), rs.getDouble("nbreTotal"));
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
        clearTableStudent();
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

    public void setResidenceDetailsToTable() {

        clearTableResidence();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM public.residences ORDER BY id_residence ASC");

            while (rs.next()) {
                String codeResid = rs.getString("code_residence");
                String nomResid = rs.getString("nom");
                String adresse = rs.getString("adresse");
                String capacite = String.valueOf(rs.getInt("capacite"));
                String equipment = rs.getString("equipements");

                Object[] rowDataTable = {codeResid, nomResid, adresse, capacite, equipment};
                model = (DefaultTableModel) tblDetailsResidences.getModel();
                model.addRow(rowDataTable);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void setPaiementDetailsToTable() {

        clearTablePaiement();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM public.paiements ORDER BY id_paiement ASC");

            while (rs.next()) {
                String idUser = String.valueOf(rs.getInt("id_utilisateur"));
                String montant = String.valueOf(rs.getInt("montant"));
                String datePaiement = rs.getString("date_paiement");
                String typePaiement = rs.getString("type_paiement");
                String statut = rs.getString("statut");

                Object[] rowDataTable = {idUser, montant, datePaiement, typePaiement, statut};
                model = (DefaultTableModel) tblPaiement.getModel();
                model.addRow(rowDataTable);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void setRoomsDetailsToTable() {

        clearTableRoom();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM public.chambres ORDER BY id_chambre ASC");

            while (rs.next()) {
                String numeroChbre = rs.getString("numero");
                String idResid = rs.getString("residence_id");
                String statutChbre = rs.getString("statut");
                String codeResid = rs.getString("code_residence");

                Object[] rowDataTable = {idResid, codeResid, numeroChbre, statutChbre};
                model = (DefaultTableModel) tblRoomsDetails.getModel();
                model.addRow(rowDataTable);

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

    public void setCodeResidenceToChbreForm() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id_residence FROM residences");

            while (rs.next()) {
                String idResid = rs.getString("id_residence");
                cboIdResidence.addItem(idResid);
            }
            // Fermeture des ressources
            rs.close();
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setAssignDetailsToTable() {

        clearTableAssignation();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM public.assignations ORDER BY id_assignation ASC");

            while (rs.next()) {
                String idChbre = String.valueOf(rs.getInt("id_chambre"));
                String idEtud = String.valueOf(rs.getInt("id_utilisateur"));
                String dateAssignation = rs.getString("date_assignation");
                String dateLiberation = rs.getString("date_liberation");

                Object[] rowDataTable = {idChbre, idEtud, dateAssignation, dateLiberation};
                model = (DefaultTableModel) tblAssignationDetails.getModel();
                model.addRow(rowDataTable);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setNumeroChbreToAssignForm() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT numero FROM chambres");

            while (rs.next()) {
                String numChbre = rs.getString("numero");
                cboANumChbre.addItem(numChbre);
            }
            // Fermeture des ressources
            rs.close();
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMatricEtudiantToAssignForm() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT matricule FROM utilisateurs WHERE UPPER(fonction) LIKE '%ETUDIANT%' ");

            while (rs.next()) {
                String matriculeEtud = rs.getString("matricule");
                cboAMatricEtudiant.addItem(matriculeEtud);
            }
            // Fermeture des ressources
            rs.close();
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void setMatricEtudiantToPaiementForm() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id_utilisateur FROM utilisateurs WHERE UPPER(fonction) LIKE '%ETUDIANT%' ");

            while (rs.next()) {
                String matriculeEtud = rs.getString("id_utilisateur");
                cboIdEtudiant.addItem(matriculeEtud);
            }
            // Fermeture des ressources
            rs.close();
            st.close();
            conn.close();

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

    public boolean addResidence() {
        boolean isAdded = false;

        String nomResid = TxtResidenceName.getText();
        String adresse = TxtAdresseRes.getText();
        int capacite = Integer.parseInt(TxtCapasiteResid.getText());
        String equipment = txtEquipementRes.getText();
        String codeResistence = TxtCodeResidence.getText();

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO public.residences(nom, adresse, capacite, equipements, code_residence)  VALUES(?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, nomResid);
            pst.setString(2, adresse);
            pst.setInt(3, capacite);
            pst.setString(4, equipment);
            pst.setString(5, codeResistence);

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

    public boolean addRooms() {
        boolean isAdded = false;

        int idResid = Integer.parseInt(cboIdResidence.getSelectedItem().toString());
        String codeResid = cboCodeReidence.getSelectedItem().toString();
        String numeroChbre = txtNumeroChbre.getText().toUpperCase();
        String statutChbre = cboStatutChbre.getSelectedItem().toString().toLowerCase();

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO public.chambres(numero, residence_id, statut, code_residence)  VALUES(?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, numeroChbre);
            pst.setInt(2, idResid);
            pst.setString(3, statutChbre);
            pst.setString(4, codeResid);

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

    public boolean addAssignStudent() {
        boolean isAdded = false;

        int idChbre = Integer.parseInt(cboIdChambre.getSelectedItem().toString().toUpperCase());
        int idEtudiant = Integer.parseInt(cboIdUser.getSelectedItem().toString().toUpperCase());

        java.util.Date date = txtDateAssignation.getDatoFecha();
        Long l1 = date.getTime();
        java.sql.Date Date_assign = new java.sql.Date(l1);

        java.util.Date date1 = txtDateLiberation.getDatoFecha();
        Long l12 = date1.getTime();
        java.sql.Date Date_liberation = new java.sql.Date(l12);

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO public.assignations(id_chambre, id_utilisateur, date_assignation, date_liberation)  VALUES(?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, idChbre);
            pst.setInt(2, idEtudiant);
            pst.setDate(3, Date_assign);
            pst.setDate(4, Date_liberation);

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
    
    public boolean addPaiement() {
        boolean isAdded = false;

        int idEtudiant = Integer.parseInt(cboIdEtudiant.getSelectedItem().toString());
        int montant = Integer.parseInt(txtMontant.getText());
        String statut = cboStatutPaiement.getSelectedItem().toString();
        String typePaiemnt = txtTypePaiement.getText();
        java.util.Date date = txtDatePaiement.getDatoFecha();
        Long l1 = date.getTime();
        java.sql.Date DatePaiement = new java.sql.Date(l1);


        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO public.paiements(id_utilisateur, montant, date_paiement, type_paiement, statut)  VALUES(?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, idEtudiant);
            pst.setInt(2, montant);
            pst.setDate(3, DatePaiement);
            pst.setString(4, typePaiemnt);
            pst.setString(5, statut);

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

    public boolean updateRooms() {
        boolean isUpdated = false;

        int idResid = Integer.parseInt(cboIdResidence.getSelectedItem().toString());
        String codeResid = cboCodeReidence.getSelectedItem().toString();
        String numeroChbre = txtNumeroChbre.getText().toUpperCase();
        String statutChbre = cboStatutChbre.getSelectedItem().toString().toLowerCase();

        String sql = "UPDATE public.chambres SET residence_id = ?, statut = ?, code_residence = ? WHERE numero = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idResid);
            pst.setString(2, statutChbre);
            pst.setString(3, codeResid);
            pst.setString(4, numeroChbre);

            int rowCount = pst.executeUpdate();
            isUpdated = rowCount > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isUpdated;
    }
    
    public boolean updatePaiement() {
        boolean isUpdated = false;

        int idEtudiant = Integer.parseInt(cboIdEtudiant.getSelectedItem().toString());
        int montant = Integer.parseInt(txtMontant.getText());
        String statut = cboStatutPaiement.getSelectedItem().toString();
        String typePaiemnt = txtTypePaiement.getText();
        java.util.Date date = txtDatePaiement.getDatoFecha();
        Long l1 = date.getTime();
        java.sql.Date DatePaiement = new java.sql.Date(l1);

        String sql = "UPDATE public.paiements SET montant = ?, date_paiement = ?, type_paiement = ?, statut = ? WHERE id_utilisateur = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, montant);
            pst.setDate(2, DatePaiement);
            pst.setString(3, typePaiemnt);
            pst.setString(4, statut);
            pst.setInt(5, idEtudiant);

            int rowCount = pst.executeUpdate();
            isUpdated = rowCount > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isUpdated;
    }

    public boolean deleteRooms(String numero) {
        boolean isDeleted = false;

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM public.chambres WHERE numero = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            // Set the matricule parameter
            pst.setString(1, numero);

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
    
    public boolean deletePaiement(int id_utilisateur) {
        boolean isDeleted = false;

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM public.paiements WHERE id_utilisateur = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            // Set the matricule parameter
            pst.setInt(1, id_utilisateur);

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

    public boolean deleteAssignation(int idEtudiant) {
        boolean isDeleted = false;

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM public.assignations WHERE id_utilisateur = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            // Set the matricule parameter
            pst.setInt(1, idEtudiant);

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

    //Methode to Update residence
    public boolean updateResidence() {
        boolean isUpdated = false;

        String codeResid = TxtCodeResidence.getText();
        String nomResid = TxtResidenceName.getText();
        String adresseResid = TxtAdresseRes.getText();
        int capaciteResid = Integer.parseInt(TxtCapasiteResid.getText());
        String equipementResid = txtEquipementRes.getText();

        String sql = "UPDATE public.residences SET nom = ?, adresse = ?, capacite = ?, equipements = ? WHERE code_residence = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, nomResid);
            pst.setString(2, adresseResid);
            pst.setInt(3, capaciteResid);
            pst.setString(4, equipementResid);
            pst.setString(5, codeResid);

            int rowCount = pst.executeUpdate();
            isUpdated = rowCount > 0;

        } catch (SQLException e) {
            e.printStackTrace();
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

    //Methode to delete Residence
    public boolean deleteResidence(String code_residence) {
        boolean isDeleted = false;

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM public.residences WHERE code_residence = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            // Set the matricule parameter
            pst.setString(1, code_residence);

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
    public void clearTableStudent() {
        model = (DefaultTableModel) tblManageStudent.getModel();
        model.setRowCount(0);
    }

    public void clearTableAssignation() {
        model = (DefaultTableModel) tblAssignationDetails.getModel();
        model.setRowCount(0);
    }

    public void clearTableResidence() {
        model = (DefaultTableModel) tblDetailsResidences.getModel();
        model.setRowCount(0);
    }

    public void clearTablePaiement() {
        model = (DefaultTableModel) tblPaiement.getModel();
        model.setRowCount(0);
    }

    public void clearTableRoom() {
        model = (DefaultTableModel) tblRoomsDetails.getModel();
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

    public void clearAssignationForm() {

        txtDateAssignation.setDatoFecha(null);
        txtDateLiberation.setDatoFecha(null);
        cboANumChbre.setSelectedIndex(0);
        cboAMatricEtudiant.setSelectedIndex(0);
        cboIdChambre.setSelectedIndex(0);
        cboIdUser.setSelectedIndex(0);
    }

    //Methode to clear Residence form
    public void clearResidenceForm() {
        TxtCodeResidence.setText("");
        TxtResidenceName.setText("");
        TxtAdresseRes.setText("");
        TxtCapasiteResid.setText("");
        txtEquipementRes.setText("");
        cboCodeReidence.setSelectedIndex(0);
    }
    
     //Methode to clear Paiement form
    public void clearPaiementForm() {
        cboIdEtudiant.setSelectedIndex(0);
        txtDatePaiement.setDatoFecha(null);
        txtMontant.setText("");
        txtTypePaiement.setText("");
        cboStatutPaiement.setSelectedIndex(0);
    }

    //Methode to clear Residence form
    public void clearRoomsForm() {
        cboIdResidence.setSelectedIndex(0);
        cboCodeReidence.setSelectedIndex(0);
        txtNumeroChbre.setText("");
        cboStatutChbre.setSelectedIndex(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        materialShadowCircle1 = new efectos.MaterialShadowCircle();
        rSPopuMenu1 = new rojeru_san.complementos.RSPopuMenu();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblLoginName = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
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
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        TxtResidenceName = new app.bolivia.swing.JCTextField();
        TxtAdresseRes = new app.bolivia.swing.JCTextField();
        TxtCapasiteResid = new app.bolivia.swing.JCTextField();
        jPanel15 = new javax.swing.JPanel();
        btnDeleteResidence = new necesario.RSMaterialButtonCircle();
        btnResidenceAdd = new necesario.RSMaterialButtonCircle();
        btnUpdateResidence = new necesario.RSMaterialButtonCircle();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtEquipementRes = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblDetailsResidences = new rojeru_san.complementos.RSTableMetro();
        jLabel19 = new javax.swing.JLabel();
        TxtCodeResidence = new app.bolivia.swing.JCTextField();
        jLabel61 = new javax.swing.JLabel();
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
        btnPrint = new javax.swing.JButton();
        pnlManageRoom = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        txtNumeroChbre = new app.bolivia.swing.JCTextField();
        cboIdResidence = new javax.swing.JComboBox<>();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        cboStatutChbre = new javax.swing.JComboBox<>();
        btnDeleteRoom = new necesario.RSMaterialButtonCircle();
        btnAddRoom = new necesario.RSMaterialButtonCircle();
        btnUpdateRoom = new necesario.RSMaterialButtonCircle();
        cboCodeReidence = new javax.swing.JComboBox<>();
        jLabel56 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblAssignationDetails = new rojeru_san.complementos.RSTableMetro();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblRoomsDetails = new rojeru_san.complementos.RSTableMetro();
        jPanel18 = new javax.swing.JPanel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        cboANumChbre = new javax.swing.JComboBox<>();
        cboAMatricEtudiant = new javax.swing.JComboBox<>();
        btnDeleteAssign = new necesario.RSMaterialButtonCircle();
        btnAssign = new necesario.RSMaterialButtonCircle();
        btnUpdateAssign = new necesario.RSMaterialButtonCircle();
        cboIdChambre = new javax.swing.JComboBox<>();
        cboIdUser = new javax.swing.JComboBox<>();
        txtDateAssignation = new rojeru_san.componentes.RSDateChooser();
        txtDateLiberation = new rojeru_san.componentes.RSDateChooser();
        pnlManageChat = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        BtnChat = new necesario.RSMaterialButtonCircle();
        pnlManageStats = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        txtMontant = new app.bolivia.swing.JCTextField();
        cboIdEtudiant = new javax.swing.JComboBox<>();
        txtDatePaiement = new rojeru_san.componentes.RSDateChooser();
        txtTypePaiement = new app.bolivia.swing.JCTextField();
        cboStatutPaiement = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        addPaiement = new necesario.RSMaterialButtonCircle();
        deletePaiement = new necesario.RSMaterialButtonCircle();
        updatePaiement = new necesario.RSMaterialButtonCircle();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblPaiement = new rojeru_san.complementos.RSTableMetro();
        pnlManageSupport = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        cboFrom = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        txtTo = new javax.swing.JTextField();
        txtSubject = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtArMessage = new javax.swing.JTextArea();
        txtFrom = new javax.swing.JTextField();
        btnSendMessage = new javax.swing.JButton();

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

        jLabel44.setFont(new java.awt.Font("Helvetica Neue", 0, 65)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(51, 51, 51));
        jLabel44.setText("l");
        jPanel1.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 0, 20, 70));

        jLabel49.setFont(new java.awt.Font("Helvetica Neue", 0, 65)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(51, 51, 51));
        jLabel49.setText("l");
        jPanel1.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 10, 20, 40));

        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/bell_26px.png"))); // NOI18N
        jPanel1.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 20, 30, 30));

        jLabel51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search_26px.png"))); // NOI18N
        jPanel1.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 20, 30, 30));

        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/group_message_26px.png"))); // NOI18N
        jPanel1.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(836, 20, 30, 30));

        jLabel53.setFont(new java.awt.Font("Helvetica Neue", 0, 65)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(51, 51, 51));
        jLabel53.setText("l");
        jPanel1.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 10, 20, 40));

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
        jLabel9.setText("  Paiement");

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
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

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
        jLabel13.setText("   Support");

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

        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBackground(new java.awt.Color(102, 102, 255));

        jLabel25.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("MANAGE RESIDENCES");

        jLabel57.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(255, 255, 255));
        jLabel57.setText("Residence");

        jLabel58.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(255, 255, 255));
        jLabel58.setText("Adresse");

        jLabel59.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(255, 255, 255));
        jLabel59.setText("Capacité");

        jLabel60.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(255, 255, 255));
        jLabel60.setText("Equipement");

        TxtResidenceName.setPlaceholder("nom residence");

        TxtAdresseRes.setPlaceholder("Adresse residence");

        TxtCapasiteResid.setPlaceholder("capacité");

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Operation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(255, 0, 102))); // NOI18N

        btnDeleteResidence.setBackground(new java.awt.Color(255, 51, 51));
        btnDeleteResidence.setText("DELETE");
        btnDeleteResidence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteResidenceActionPerformed(evt);
            }
        });

        btnResidenceAdd.setBackground(new java.awt.Color(255, 51, 51));
        btnResidenceAdd.setText("ADD");
        btnResidenceAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResidenceAddActionPerformed(evt);
            }
        });

        btnUpdateResidence.setBackground(new java.awt.Color(255, 51, 51));
        btnUpdateResidence.setText("UPDATE");
        btnUpdateResidence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateResidenceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnResidenceAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteResidence, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateResidence, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnResidenceAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(btnUpdateResidence, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(btnDeleteResidence, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        txtEquipementRes.setColumns(20);
        txtEquipementRes.setRows(5);
        jScrollPane5.setViewportView(txtEquipementRes);

        tblDetailsResidences.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Code residence", "Residence", "Adresse", "Capacité", "Equipements"
            }
        ));
        tblDetailsResidences.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDetailsResidencesMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tblDetailsResidences);

        jLabel19.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Liste des residences");

        TxtCodeResidence.setPlaceholder("code residence");

        jLabel61.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(255, 255, 255));
        jLabel61.setText("Code");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel58)
                                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel59)
                                    .addComponent(jLabel60, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TxtResidenceName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TxtAdresseRes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(TxtCapasiteResid, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(TxtCodeResidence, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(153, 153, 153)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1084, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel61)
                            .addComponent(TxtCodeResidence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel57)
                            .addComponent(TxtResidenceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel58)
                            .addComponent(TxtAdresseRes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TxtCapasiteResid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel59))
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jLabel60))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel13.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1120, 690));

        javax.swing.GroupLayout pnlManageResidenceLayout = new javax.swing.GroupLayout(pnlManageResidence);
        pnlManageResidence.setLayout(pnlManageResidenceLayout);
        pnlManageResidenceLayout.setHorizontalGroup(
            pnlManageResidenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlManageResidenceLayout.setVerticalGroup(
            pnlManageResidenceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        btnPrint.setBackground(new java.awt.Color(0, 204, 204));
        btnPrint.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        btnPrint.setForeground(new java.awt.Color(255, 51, 51));
        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        pnlManageStudent.add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 390, 110, 30));

        jTabbedPane1.addTab("tab3", pnlManageStudent);

        jPanel16.setBackground(new java.awt.Color(102, 102, 255));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Manage Rooms", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(255, 51, 51))); // NOI18N

        jLabel23.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel23.setText("ID Residence");

        txtNumeroChbre.setPlaceholder("numero chambre");

        cboIdResidence.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select Id --" }));
        cboIdResidence.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboIdResidenceItemStateChanged(evt);
            }
        });

        jLabel54.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel54.setText("Numero chambe");

        jLabel55.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel55.setText("Statut");

        cboStatutChbre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select statut --", "libre", "occupee" }));

        btnDeleteRoom.setBackground(new java.awt.Color(255, 51, 51));
        btnDeleteRoom.setText("DELETE");
        btnDeleteRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteRoomActionPerformed(evt);
            }
        });

        btnAddRoom.setBackground(new java.awt.Color(255, 51, 51));
        btnAddRoom.setText("ADD");
        btnAddRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRoomActionPerformed(evt);
            }
        });

        btnUpdateRoom.setBackground(new java.awt.Color(255, 51, 51));
        btnUpdateRoom.setText("UPDATE");
        btnUpdateRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateRoomActionPerformed(evt);
            }
        });

        cboCodeReidence.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select code --" }));
        cboCodeReidence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCodeReidenceActionPerformed(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel56.setText("Code Residence");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addComponent(btnUpdateRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(btnDeleteRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(cboCodeReidence, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel55, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)))
                                .addGap(26, 26, 26)
                                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNumeroChbre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboIdResidence, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboStatutChbre, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(cboIdResidence, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(cboCodeReidence, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNumeroChbre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboStatutChbre, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55))
                .addGap(26, 26, 26)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        jPanel16.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 530, 320));

        tblAssignationDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id Chambre", "Id Etudiant", "Date Entrée", "Date Sortie"
            }
        ));
        tblAssignationDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAssignationDetailsMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(tblAssignationDetails);

        jPanel16.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 530, 1090, 170));

        tblRoomsDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id Residence", "Code Residence", "N° Chambre", "Statut"
            }
        ));
        tblRoomsDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRoomsDetailsMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(tblRoomsDetails);

        jPanel16.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 1090, 170));

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Assignation Etudiant", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(255, 51, 51))); // NOI18N

        jLabel62.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel62.setText("Numero Chambre");

        jLabel63.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel63.setText("Matricule Etudiant");

        jLabel64.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel64.setText("Date assignation");

        jLabel65.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel65.setText("Date Liberation");

        cboANumChbre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- Select chbre ---" }));
        cboANumChbre.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboANumChbreItemStateChanged(evt);
            }
        });

        cboAMatricEtudiant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select matricule --" }));
        cboAMatricEtudiant.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboAMatricEtudiantItemStateChanged(evt);
            }
        });

        btnDeleteAssign.setBackground(new java.awt.Color(255, 51, 51));
        btnDeleteAssign.setText("DELETE");
        btnDeleteAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteAssignActionPerformed(evt);
            }
        });

        btnAssign.setBackground(new java.awt.Color(255, 51, 51));
        btnAssign.setText("Assign");
        btnAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignActionPerformed(evt);
            }
        });

        btnUpdateAssign.setBackground(new java.awt.Color(255, 51, 51));
        btnUpdateAssign.setText("UPDATE");

        cboIdChambre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select --" }));

        cboIdUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select--" }));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel64, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel63, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cboANumChbre, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboAMatricEtudiant, 0, 158, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboIdChambre, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboIdUser, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(txtDateAssignation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDateLiberation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(btnAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(btnUpdateAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(btnDeleteAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(cboANumChbre, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboIdChambre, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(cboAMatricEtudiant, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboIdUser, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDateAssignation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel64)
                        .addGap(12, 12, 12)))
                .addGap(17, 17, 17)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDateLiberation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel65)
                        .addGap(13, 13, 13)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        jPanel16.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, 540, 320));

        javax.swing.GroupLayout pnlManageRoomLayout = new javax.swing.GroupLayout(pnlManageRoom);
        pnlManageRoom.setLayout(pnlManageRoomLayout);
        pnlManageRoomLayout.setHorizontalGroup(
            pnlManageRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageRoomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, 1118, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlManageRoomLayout.setVerticalGroup(
            pnlManageRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlManageRoomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("tab4", pnlManageRoom);

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Chat_design.jpg"))); // NOI18N

        BtnChat.setBackground(new java.awt.Color(255, 51, 51));
        BtnChat.setText("Demarrer le Chat");
        BtnChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnChatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtnChat, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(286, 286, 286)
                        .addComponent(BtnChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel24)))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlManageChatLayout = new javax.swing.GroupLayout(pnlManageChat);
        pnlManageChat.setLayout(pnlManageChatLayout);
        pnlManageChatLayout.setHorizontalGroup(
            pnlManageChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageChatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlManageChatLayout.setVerticalGroup(
            pnlManageChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageChatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("tab5", pnlManageChat);

        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Infos Paiement", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 18), new java.awt.Color(255, 51, 51))); // NOI18N

        cboIdEtudiant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select--" }));
        cboIdEtudiant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboIdEtudiantActionPerformed(evt);
            }
        });

        cboStatutPaiement.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select--", "paye", "non paye" }));

        jLabel15.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel15.setText("ID Etudiant");

        jLabel66.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel66.setText("Montant");

        jLabel67.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel67.setText("Date Paiement");

        jLabel69.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel69.setText("Type paiement");

        jLabel70.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel70.setText("Statut");

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Operations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 1, 14), new java.awt.Color(255, 51, 51))); // NOI18N

        addPaiement.setBackground(new java.awt.Color(255, 51, 51));
        addPaiement.setText("ADD");
        addPaiement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPaiementActionPerformed(evt);
            }
        });

        deletePaiement.setBackground(new java.awt.Color(255, 51, 51));
        deletePaiement.setText("DELETE");
        deletePaiement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletePaiementActionPerformed(evt);
            }
        });

        updatePaiement.setBackground(new java.awt.Color(255, 51, 51));
        updatePaiement.setText("UPDATE");
        updatePaiement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatePaiementActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addPaiement, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deletePaiement, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
            .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                    .addContainerGap(33, Short.MAX_VALUE)
                    .addComponent(updatePaiement, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(27, 27, 27)))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addPaiement, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 96, Short.MAX_VALUE)
                .addComponent(deletePaiement, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
            .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel21Layout.createSequentialGroup()
                    .addGap(83, 83, 83)
                    .addComponent(updatePaiement, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(99, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                            .addComponent(jLabel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMontant, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboIdEtudiant, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel69, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                            .addComponent(jLabel70, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cboStatutPaiement, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTypePaiement, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDatePaiement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(48, 48, 48)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboIdEtudiant, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtMontant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel66))
                                .addGap(20, 20, 20)
                                .addComponent(jLabel67)
                                .addGap(49, 49, 49)
                                .addComponent(jLabel69))
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGap(70, 70, 70)
                                .addComponent(txtDatePaiement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtTypePaiement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cboStatutPaiement, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel70)))))
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        jPanel19.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 710, 370));

        tblPaiement.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id Etudiant", "Montant", "Date paiement", "Type Paiement", "Statut"
            }
        ));
        tblPaiement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPaiementMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(tblPaiement);

        jPanel19.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 550, 1100, 150));

        javax.swing.GroupLayout pnlManageStatsLayout = new javax.swing.GroupLayout(pnlManageStats);
        pnlManageStats.setLayout(pnlManageStatsLayout);
        pnlManageStatsLayout.setHorizontalGroup(
            pnlManageStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageStatsLayout.createSequentialGroup()
                .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlManageStatsLayout.setVerticalGroup(
            pnlManageStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageStatsLayout.createSequentialGroup()
                .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, 719, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("tab7", pnlManageStats);

        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel48.setFont(new java.awt.Font("Helvetica Neue", 1, 24)); // NOI18N
        jLabel48.setText("Email Sending Form");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(131, Short.MAX_VALUE)
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(149, 149, 149))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 550, -1));

        cboFrom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel18.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel18.setText("From");

        jLabel45.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel45.setText("To");

        jLabel46.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel46.setText("Subject");

        jLabel47.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel47.setText("Message");

        txtArMessage.setColumns(20);
        txtArMessage.setRows(5);
        jScrollPane4.setViewportView(txtArMessage);

        btnSendMessage.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        btnSendMessage.setText("Send");
        btnSendMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendMessageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSendMessage)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(80, Short.MAX_VALUE))
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addComponent(jLabel18)
                            .addGap(55, 55, 55)
                            .addComponent(cboFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addComponent(jLabel45)
                            .addGap(78, 78, 78)
                            .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addComponent(jLabel46)
                            .addGap(35, 35, 35)
                            .addComponent(txtSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addComponent(jLabel47)
                            .addGap(23, 23, 23)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 369, Short.MAX_VALUE)
                .addComponent(btnSendMessage)
                .addGap(37, 37, 37))
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addGap(7, 7, 7)
                            .addComponent(jLabel18))
                        .addComponent(cboFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(20, 20, 20)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addGap(7, 7, 7)
                            .addComponent(jLabel45))
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(17, 17, 17)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel46)
                        .addComponent(txtSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(30, 30, 30)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(jLabel47))
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel9.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 550, 490));

        javax.swing.GroupLayout pnlManageSupportLayout = new javax.swing.GroupLayout(pnlManageSupport);
        pnlManageSupport.setLayout(pnlManageSupportLayout);
        pnlManageSupportLayout.setHorizontalGroup(
            pnlManageSupportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlManageSupportLayout.setVerticalGroup(
            pnlManageSupportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlManageSupportLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 622, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 103, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab6", pnlManageSupport);

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
        Object passwordValue = model.getValueAt(rowNo, 8);
        if (passwordValue != null) {
            txtPassword.setText(passwordValue.toString());
        } else {
            txtPassword.setText(""); // Pour éviter une exception si la valeur est null
        }

        
//        txtPassword.setText(model.getValueAt(rowNo, 8).toString());

    }//GEN-LAST:event_tblManageStudentMouseClicked

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5433/gestion_residence", "postgres", "Reinge");
            JasperDesign jdesign = JRXmlLoader.load("/Users/charlyrenard/NetBeansProjects/GestionResidence/src/gestionresidence/studentReport.jrxml");

            String query = "SELECT * FROM utilisateurs WHERE UPPER(fonction) LIKE '%ETUDIANT%' ORDER BY matricule";

            JRDesignQuery updateQuery = new JRDesignQuery();
            updateQuery.setText(query);

            jdesign.setQuery(updateQuery);

            JasperReport jreport = JasperCompileManager.compileReport(jdesign);
            JasperPrint jprint = JasperFillManager.fillReport(jreport, null, conn);

            JasperViewer.viewReport(jprint);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnSendMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendMessageActionPerformed
        String ToEmail = txtTo.getText().toLowerCase();
        String[] ArrEmail = ToEmail.split(",");
        String FromEmail = txtFrom.getText().toLowerCase(); // teamglap27@gmail.com
        String FromEmailPassword = "tuls qsda mtgt aogf"; // You email Password
        String Subjects = txtSubject.getText();

        for (String singleEmail : ArrEmail) {
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

//            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication(FromEmail, FromEmailPassword);
//                }
//            });
            // Crée une session mail authentifiée
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FromEmail, FromEmailPassword);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(FromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(singleEmail));
                message.setSubject(Subjects);
                message.setText(txtArMessage.getText());
                Transport.send(message);
                System.out.println("Message sent successfully!");
            } catch (SendFailedException e) {
                System.err.println("Send failed: " + e.getMessage());
                e.printStackTrace();
            } catch (MessagingException e) {
                System.err.println("MessagingException: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }


    }//GEN-LAST:event_btnSendMessageActionPerformed

    private void BtnChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnChatActionPerformed
        // Lancer les deux classes Administration et Etudiant
        new Thread(() -> new Administration()).start(); // Démarrer dans un nouveau thread
        new Thread(() -> new Etudiant()).start();        // Démarrer dans un nouveau thread

    }//GEN-LAST:event_BtnChatActionPerformed

    private void btnResidenceAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResidenceAddActionPerformed
        if (addResidence() == true) {
            JOptionPane.showMessageDialog(this, "Residence added !");
            setResidenceDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Residence addition Failed !");
        }
        clearResidenceForm();
    }//GEN-LAST:event_btnResidenceAddActionPerformed

    private void btnDeleteResidenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteResidenceActionPerformed
        // TODO add your handling code here:
        String codeResid = TxtCodeResidence.getText();  // Assuming you have a field for matricule
        boolean success = deleteResidence(codeResid);

        if (success) {
            JOptionPane.showMessageDialog(this, "Residence deleted successfully !");
            setResidenceDetailsToTable();
            clearResidenceForm();
        } else {
            JOptionPane.showMessageDialog(this, "Residence deletion Failed !");
        }
        clearResidenceForm();
    }//GEN-LAST:event_btnDeleteResidenceActionPerformed

    private void tblDetailsResidencesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetailsResidencesMouseClicked

        int rowNo = tblDetailsResidences.getSelectedRow();
        TableModel modelT = tblDetailsResidences.getModel();

        TxtCodeResidence.setText(modelT.getValueAt(rowNo, 0).toString());
        TxtResidenceName.setText(modelT.getValueAt(rowNo, 1).toString());
        TxtAdresseRes.setText(modelT.getValueAt(rowNo, 2).toString());
        TxtCapasiteResid.setText(modelT.getValueAt(rowNo, 3).toString());
        txtEquipementRes.setText(modelT.getValueAt(rowNo, 4).toString());
    }//GEN-LAST:event_tblDetailsResidencesMouseClicked

    private void btnUpdateResidenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateResidenceActionPerformed

        if (updateResidence() == true) {
            JOptionPane.showMessageDialog(this, "Residence updated !");
            setResidenceDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Residence updation Failed !");
        }
        clearResidenceForm();
    }//GEN-LAST:event_btnUpdateResidenceActionPerformed

    private void cboCodeReidenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCodeReidenceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboCodeReidenceActionPerformed

    private void cboIdResidenceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboIdResidenceItemStateChanged

        int id = Integer.parseInt(cboIdResidence.getSelectedItem().toString());

        cboCodeReidence.removeAllItems();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT TRIM(code_residence) AS code_residence FROM residences WHERE id_residence ='" + id + "' ");

            while (rs.next()) {
                String codeResid = rs.getString("code_residence");
                boolean exists = false;
                for (int i = 0; i < cboCodeReidence.getItemCount(); i++) {
                    if (cboCodeReidence.getItemAt(i).equals(codeResid)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    cboCodeReidence.addItem(codeResid);
                }
            }
            // Fermeture des ressources
            rs.close();
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cboIdResidenceItemStateChanged

    private void btnAddRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRoomActionPerformed

        if (addRooms() == true) {
            JOptionPane.showMessageDialog(this, "Room added !");
            //clearTable();
            setRoomsDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Room addition Failed !");
        }

        clearRoomsForm();
    }//GEN-LAST:event_btnAddRoomActionPerformed

    private void btnDeleteRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteRoomActionPerformed

        String numerChbre = txtNumeroChbre.getText();  // Assuming you have a field for matricule
        boolean success = deleteRooms(numerChbre);

        if (success) {
            JOptionPane.showMessageDialog(this, "Room deleted successfully !");
            setRoomsDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Room deletion Failed !");
        }
        clearRoomsForm();
    }//GEN-LAST:event_btnDeleteRoomActionPerformed

    private void btnUpdateRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateRoomActionPerformed

        if (updateRooms() == true) {
            JOptionPane.showMessageDialog(this, "Room updated !");
            setRoomsDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Room updation Failed !");
        }
        clearRoomsForm();
    }//GEN-LAST:event_btnUpdateRoomActionPerformed

    // Méthode pour vérifier si un item existe déjà dans le ComboBox
    private boolean itemExistsInComboBox(JComboBox<String> comboBox, String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }
    
    private void tblAssignationDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAssignationDetailsMouseClicked
        // TODO add your handling code here:
        int rowNo = tblAssignationDetails.getSelectedRow();
        TableModel modelT = tblAssignationDetails.getModel();
        
        cboIdChambre.setSelectedItem(modelT.getValueAt(rowNo, 0).toString());
        cboIdUser.setSelectedItem(modelT.getValueAt(rowNo, 1).toString());
       
        // Retrieve the date from the JTable and parse it
        String dateStr = modelT.getValueAt(rowNo, 2).toString();  // The date in the format YYYY-MM-DD
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Format in JTable

        try {
            java.util.Date utilDate = sdf.parse(dateStr);  // Parse the date from String
            txtDateAssignation.setDatoFecha(utilDate);  // Set the date in RSDateChooser
        } catch (ParseException e) {
            e.printStackTrace();  // Handle parsing exception
        }

        String dateString = modelT.getValueAt(rowNo, 3).toString();  // The date in the format YYYY-MM-DD
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  // Format in JTable

        try {
            java.util.Date utilDate1 = sdf1.parse(dateString);  // Parse the date from String
            txtDateLiberation.setDatoFecha(utilDate1);  // Set the date in RSDateChooser
        } catch (ParseException e) {
            e.printStackTrace();  // Handle parsing exception
        }

    }//GEN-LAST:event_tblAssignationDetailsMouseClicked

    private void tblRoomsDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRoomsDetailsMouseClicked

        int rowNo = tblRoomsDetails.getSelectedRow();
        TableModel modelT = tblRoomsDetails.getModel();

        cboIdResidence.setSelectedItem(modelT.getValueAt(rowNo, 0).toString());
        cboCodeReidence.setSelectedItem(modelT.getValueAt(rowNo, 1).toString());
        txtNumeroChbre.setText(modelT.getValueAt(rowNo, 2).toString());
        cboStatutChbre.setSelectedItem(modelT.getValueAt(rowNo, 3).toString());
    }//GEN-LAST:event_tblRoomsDetailsMouseClicked

    private void btnAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignActionPerformed
        // TODO add your handling code here:
        if (addAssignStudent() == true) {
            JOptionPane.showMessageDialog(this, "Student  assigned !");
            setAssignDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Studnt assignation Failed !");
        }
        clearAssignationForm();
    }//GEN-LAST:event_btnAssignActionPerformed

    private void cboANumChbreItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboANumChbreItemStateChanged
        // TODO add your handling code here:
        String numeroChbre = cboANumChbre.getSelectedItem().toString();

        cboIdChambre.removeAllItems();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT id_chambre  FROM chambres WHERE numero ='" + numeroChbre + "' ");

            while (rs.next()) {
                String codeResid = rs.getString("id_chambre");
                boolean exists = false;
                for (int i = 0; i < cboIdChambre.getItemCount(); i++) {
                    if (cboIdChambre.getItemAt(i).equals(codeResid)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    cboIdChambre.addItem(codeResid);
                }
            }
            // Fermeture des ressources
            rs.close();
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cboANumChbreItemStateChanged

    private void cboAMatricEtudiantItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboAMatricEtudiantItemStateChanged
        // TODO add your handling code here:
        String matricEtud = cboAMatricEtudiant.getSelectedItem().toString();

        cboIdUser.removeAllItems();
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT id_utilisateur  FROM utilisateurs WHERE matricule ='" + matricEtud + "' ");

            while (rs.next()) {
                String codeResid = rs.getString("id_utilisateur");
                boolean exists = false;
                for (int i = 0; i < cboIdUser.getItemCount(); i++) {
                    if (cboIdUser.getItemAt(i).equals(codeResid)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    cboIdUser.addItem(codeResid);
                }
            }
            // Fermeture des ressources
            rs.close();
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cboAMatricEtudiantItemStateChanged

    private void btnDeleteAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteAssignActionPerformed
        // TODO add your handling code here:
        int idEtud = Integer.parseInt(cboIdUser.getSelectedItem().toString());
        boolean success = deleteAssignation(idEtud);

        if (success) {
            JOptionPane.showMessageDialog(this, "Assignation deleted successfully !");
            setStudentDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Assignation deletion Failed !");
        }
        clearAssignationForm();
    }//GEN-LAST:event_btnDeleteAssignActionPerformed

    private void cboIdEtudiantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboIdEtudiantActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboIdEtudiantActionPerformed

    private void addPaiementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPaiementActionPerformed
        // TODO add your handling code here:
        if (addPaiement() == true) {
            JOptionPane.showMessageDialog(this, "Paiement  added !");
            setPaiementDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Paiement  addition Failed !");
        }
        clearPaiementForm();
    }//GEN-LAST:event_addPaiementActionPerformed

    private void tblPaiementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPaiementMouseClicked
        // TODO add your handling code here:
        int rowNo = tblPaiement.getSelectedRow();
        TableModel modelT = tblPaiement.getModel();

        cboIdEtudiant.setSelectedItem(modelT.getValueAt(rowNo, 0).toString());
        txtMontant.setText(modelT.getValueAt(rowNo, 1).toString());
        
         // Retrieve the date from the JTable and parse it
        String dateStr = modelT.getValueAt(rowNo, 2).toString();  // The date in the format YYYY-MM-DD
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Format in JTable

        try {
            java.util.Date utilDate = sdf.parse(dateStr);  // Parse the date from String
            txtDatePaiement.setDatoFecha(utilDate);  // Set the date in RSDateChooser
        } catch (ParseException e) {
            e.printStackTrace();  // Handle parsing exception
        }
        
        txtTypePaiement.setText(modelT.getValueAt(rowNo, 3).toString());
        cboStatutPaiement.setSelectedItem(modelT.getValueAt(rowNo, 4).toString());
        
    }//GEN-LAST:event_tblPaiementMouseClicked

    private void updatePaiementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatePaiementActionPerformed
        // TODO add your handling code here:
        
        if (updatePaiement() == true) {
            JOptionPane.showMessageDialog(this, "Paiement updated !");
            setPaiementDetailsToTable(); //To change
        } else {
            JOptionPane.showMessageDialog(this, "Paiement updation Failed !");
        }
        clearPaiementForm();//To change
        
    }//GEN-LAST:event_updatePaiementActionPerformed

    private void deletePaiementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePaiementActionPerformed
        // TODO add your handling code here:
        
        int idEtudiant = Integer.parseInt(cboIdEtudiant.getSelectedItem().toString());  // Assuming you have a field for matricule
        boolean success = deletePaiement(idEtudiant);

        if (success) {
            JOptionPane.showMessageDialog(this, "Paiement deleted successfully !");
            setPaiementDetailsToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Paiement deletion Failed !");
        }
        clearPaiementForm();
        
    }//GEN-LAST:event_deletePaiementActionPerformed

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        // TODO add your handling code here:
        System.exit(0);
        
    }//GEN-LAST:event_jLabel10MouseClicked

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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DashBoard().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private necesario.RSMaterialButtonCircle BtnChat;
    private app.bolivia.swing.JCTextField TxtAdresseRes;
    private app.bolivia.swing.JCTextField TxtCapasiteResid;
    private app.bolivia.swing.JCTextField TxtCodeResidence;
    private app.bolivia.swing.JCTextField TxtResidenceName;
    private necesario.RSMaterialButtonCircle addPaiement;
    private necesario.RSMaterialButtonCircle btnAdd;
    private necesario.RSMaterialButtonCircle btnAddRoom;
    private necesario.RSMaterialButtonCircle btnAssign;
    private necesario.RSMaterialButtonCircle btnDelete;
    private necesario.RSMaterialButtonCircle btnDeleteAssign;
    private necesario.RSMaterialButtonCircle btnDeleteResidence;
    private necesario.RSMaterialButtonCircle btnDeleteRoom;
    private javax.swing.JButton btnPrint;
    private necesario.RSMaterialButtonCircle btnResidenceAdd;
    private javax.swing.JButton btnSendMessage;
    private necesario.RSMaterialButtonCircle btnUpdate;
    private necesario.RSMaterialButtonCircle btnUpdateAssign;
    private necesario.RSMaterialButtonCircle btnUpdateResidence;
    private necesario.RSMaterialButtonCircle btnUpdateRoom;
    private javax.swing.JComboBox<String> cboAMatricEtudiant;
    private javax.swing.JComboBox<String> cboANumChbre;
    private javax.swing.JComboBox<String> cboCodeReidence;
    private javax.swing.JComboBox<String> cboFrom;
    private javax.swing.JComboBox<String> cboIdChambre;
    private javax.swing.JComboBox<String> cboIdEtudiant;
    private javax.swing.JComboBox<String> cboIdResidence;
    private javax.swing.JComboBox<String> cboIdUser;
    private javax.swing.JComboBox<String> cboSexe;
    private javax.swing.JComboBox<String> cboStatutChbre;
    private javax.swing.JComboBox<String> cboStatutPaiement;
    private necesario.RSMaterialButtonCircle deletePaiement;
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
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    public javax.swing.JLabel lblLoginName;
    private javax.swing.JLabel lblNbreResidence;
    private javax.swing.JLabel lblNbreRoom;
    private javax.swing.JLabel lblNbreStudent;
    private javax.swing.JLabel lblRoomFree;
    private efectos.MaterialShadowCircle materialShadowCircle1;
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
    private rojeru_san.complementos.RSPopuMenu rSPopuMenu1;
    private rojeru_san.complementos.RSTableMetro tblAssignationDetails;
    private rojeru_san.complementos.RSTableMetro tblDetailsResidences;
    private rojeru_san.complementos.RSTableMetro tblManageStudent;
    private rojeru_san.complementos.RSTableMetro tblPaiement;
    private rojeru_san.complementos.RSTableMetro tblRommStatut;
    private rojeru_san.complementos.RSTableMetro tblRoomsDetails;
    private rojeru_san.complementos.RSTableMetro tblStudentDetails;
    private javax.swing.JTextArea txtArMessage;
    private rojeru_san.componentes.RSDateChooser txtDate;
    private rojeru_san.componentes.RSDateChooser txtDateAssignation;
    private rojeru_san.componentes.RSDateChooser txtDateLiberation;
    private rojeru_san.componentes.RSDateChooser txtDatePaiement;
    private javax.swing.JTextArea txtEquipementRes;
    private app.bolivia.swing.JCTextField txtFonction;
    private javax.swing.JTextField txtFrom;
    private app.bolivia.swing.JCTextField txtMail;
    private app.bolivia.swing.JCTextField txtMatricule;
    private app.bolivia.swing.JCTextField txtMontant;
    private app.bolivia.swing.JCTextField txtNationalite;
    private app.bolivia.swing.JCTextField txtNom;
    private app.bolivia.swing.JCTextField txtNumeroChbre;
    private javax.swing.JPasswordField txtPassword;
    private app.bolivia.swing.JCTextField txtPrenom;
    private javax.swing.JTextField txtSubject;
    private javax.swing.JTextField txtTo;
    private app.bolivia.swing.JCTextField txtTypePaiement;
    private necesario.RSMaterialButtonCircle updatePaiement;
    // End of variables declaration//GEN-END:variables
}
