package database;

import models.Contact;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactsOperations {

    // Contact ekleme
    public static void addContact(Contact contact) {
        String sql = "INSERT INTO contacts (first_name, middle_name, last_name, nickname, phone_primary, phone_secondary, email, linkedin_url, birth_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, contact.getFirstName());
            pstmt.setString(2, contact.getMiddleName());
            pstmt.setString(3, contact.getLastName());
            pstmt.setString(4, contact.getNickname());
            pstmt.setString(5, contact.getPhonePrimary());
            pstmt.setString(6, contact.getPhoneSecondary());
            pstmt.setString(7, contact.getEmail());
            pstmt.setString(8, contact.getLinkedinUrl());
            pstmt.setDate(9, contact.getBirthDate());

            int rows = pstmt.executeUpdate();
            System.out.println(rows + " kayıt eklendi.");

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    contact.setContactId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Contact listeleme
    public static List<Contact> listContacts() {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Contact c = new Contact(
                    rs.getInt("contact_id"),
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name"),
                    rs.getString("nickname"),
                    rs.getString("phone_primary"),
                    rs.getString("phone_secondary"),
                    rs.getString("email"),
                    rs.getString("linkedin_url"),
                    rs.getDate("birth_date")
                );
                contacts.add(c);
                System.out.println(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    // Contact güncelleme (phone ve email)
    public static void updateContact(int contactId, String newPhone, String newEmail) {
        String sql = "UPDATE contacts SET phone_primary = ?, email = ? WHERE contact_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPhone);
            pstmt.setString(2, newEmail);
            pstmt.setInt(3, contactId);

            int rows = pstmt.executeUpdate();
            System.out.println(rows + " kayıt güncellendi.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Contact silme
    public static void deleteContact(int contactId) {
        String sql = "DELETE FROM contacts WHERE contact_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, contactId);
            int rows = pstmt.executeUpdate();
            System.out.println(rows + " kayıt silindi.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
