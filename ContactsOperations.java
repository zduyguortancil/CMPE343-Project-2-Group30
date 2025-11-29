package database;

import models.Contact;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContactsOperations {

    // List all contacts
    public static void listContacts() {
        String sql = "SELECT * FROM contacts ORDER BY contact_id";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\nID | First | Middle | Last | Nick | Phone1 | Phone2 | Email | LinkedIn | Birth | Job Title");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s%n",
                        rs.getInt("contact_id"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("nickname"),
                        rs.getString("phone_primary"),
                        rs.getString("phone_secondary"),
                        rs.getString("email"),
                        rs.getString("linkedin_url"),
                        rs.getDate("birth_date"),
                        rs.getString("job_title")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add new contact
    public static void addContact(Contact contact) {
        String sql = "INSERT INTO contacts " +
                "(first_name, middle_name, last_name, nickname, phone_primary, phone_secondary, " +
                "email, linkedin_url, birth_date, created_at, updated_at, job_title) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contact.getFirstName());
            ps.setString(2, contact.getMiddleName());
            ps.setString(3, contact.getLastName());
            ps.setString(4, contact.getNickname());
            ps.setString(5, contact.getPhonePrimary());
            ps.setString(6, contact.getPhoneSecondary());
            ps.setString(7, contact.getEmail());
            ps.setString(8, contact.getLinkedinUrl());

            // Şimdilik birth_date'i boş geçiyoruz (NULL). İstersen LocalDate ekleyebilirsin.
            if (contact.getBirthDate() != null) {
                ps.setDate(9, Date.valueOf(contact.getBirthDate()));
            } else {
                ps.setNull(9, Types.DATE);
            }

            ps.setString(10, contact.getJobTitle());

            int rows = ps.executeUpdate();
            System.out.println(rows + " contact(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update primary phone + email (nullable)
    public static void updateContact(int contactId, String newPhone, String newEmail) {
        StringBuilder sql = new StringBuilder("UPDATE contacts SET ");
        List<Object> params = new ArrayList<>();

        if (newPhone != null) {
            sql.append("phone_primary = ?, ");
            params.add(newPhone);
        }
        if (newEmail != null) {
            sql.append("email = ?, ");
            params.add(newEmail);
        }

        if (params.isEmpty()) {
            System.out.println("Nothing to update.");
            return;
        }

        sql.append("updated_at = NOW(), ");
        // sondaki virgülü sil
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE contact_id = ?");
        params.add(contactId);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            int rows = ps.executeUpdate();
            System.out.println(rows + " contact(s) updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete contact
    public static void deleteContact(int id) {
        String sql = "DELETE FROM contacts WHERE contact_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows + " contact(s) deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Simple search: by first_name
    public static void searchContactsByFirstName(String firstName) {
        String sql = "SELECT * FROM contacts WHERE first_name LIKE ? ORDER BY contact_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + firstName + "%");
            ResultSet rs = ps.executeQuery();

            System.out.println("\nID | First | Last | Nick | Phone1 | Email");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %s | %s | %s | %s%n",
                        rs.getInt("contact_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("nickname"),
                        rs.getString("phone_primary"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sort contacts by selected field & order
    public static void sortContacts(String field, String order) {
        List<String> allowed = List.of("first_name", "last_name", "birth_date");
        if (!allowed.contains(field)) {
            System.out.println("Invalid sort field.");
            return;
        }
        if (!order.equalsIgnoreCase("ASC") && !order.equalsIgnoreCase("DESC")) {
            System.out.println("Invalid sort order.");
            return;
        }

        String sql = "SELECT * FROM contacts ORDER BY " + field + " " + order;

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\nID | First | Last | Birth Date | Email");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %s | %s | %s%n",
                        rs.getInt("contact_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("birth_date"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Basic statistics for Manager
    public static void showStatistics() {
        try (Connection conn = DBConnection.getConnection()) {

            // total contacts
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) AS cnt FROM contacts")) {
                if (rs.next()) {
                    System.out.println("Total contacts: " + rs.getInt("cnt"));
                }
            }

            // contacts with LinkedIn
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) AS cnt FROM contacts WHERE linkedin_url IS NOT NULL AND linkedin_url <> ''")) {
                if (rs.next()) {
                    System.out.println("Contacts with LinkedIn URL: " + rs.getInt("cnt"));
                }
            }

            // oldest & youngest (by birth_date)
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT first_name, last_name, birth_date FROM contacts " +
                                 "WHERE birth_date IS NOT NULL ORDER BY birth_date ASC LIMIT 1")) {
                if (rs.next()) {
                    System.out.printf("Oldest contact: %s %s (%s)%n",
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getDate("birth_date"));
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT first_name, last_name, birth_date FROM contacts " +
                                 "WHERE birth_date IS NOT NULL ORDER BY birth_date DESC LIMIT 1")) {
                if (rs.next()) {
                    System.out.printf("Youngest contact: %s %s (%s)%n",
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getDate("birth_date"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
