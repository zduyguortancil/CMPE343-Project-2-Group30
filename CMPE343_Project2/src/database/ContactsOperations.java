package database;

import models.Contact;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class ContactsOperations {

    // ============================================================
    // SAFE STRING (NULL → —)
    // ============================================================
    private static String safe(String value) {
        return (value == null || value.trim().isEmpty()) ? "—" : value;
    }

    // ============================================================
    // UNIQUE CHECKS (FOR NEW CONTACT INSERTION)
    // ============================================================

    public static boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM contacts WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean phoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM contacts WHERE phone_primary = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean linkedinExists(String link) {
        String sql = "SELECT COUNT(*) FROM contacts WHERE linkedin_url = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, link);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    /** Checks if the nickname already exists in the database. */
    public static boolean nicknameExists(String nickname) {
        String sql = "SELECT COUNT(*) FROM contacts WHERE nickname = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // ============================================================
    // UNIQUE CHECKS (FOR UPDATE FLOW - USED BY MAIN.JAVA)
    // ============================================================

    /** Checks if email exists for any contact *except* the one being updated. */
    public static boolean emailExistsExcept(String email, int excludeId) {
        String sql = "SELECT COUNT(*) FROM contacts WHERE email = ? AND contact_id <> ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email); ps.setInt(2, excludeId); ResultSet rs = ps.executeQuery(); rs.next(); return rs.getInt(1) > 0;
        } catch (Exception e) { return true; } // Return true on error to prevent data collision
    }

    /** Checks if phone exists for any contact *except* the one being updated. */
    public static boolean phoneExistsExcept(String phone, int excludeId) {
        String sql = "SELECT COUNT(*) FROM contacts WHERE phone_primary = ? AND contact_id <> ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone); ps.setInt(2, excludeId); ResultSet rs = ps.executeQuery(); rs.next(); return rs.getInt(1) > 0;
        } catch (Exception e) { return true; } // Return true on error to prevent data collision
    }

    // ============================================================
    // LIST CONTACTS
    // ============================================================
    public static void listContacts() {
        String query = "SELECT contact_id, first_name, middle_name, last_name, nickname, " +
                "phone_primary, phone_secondary, email, linkedin_url, birth_date, " +
                "job_title, created_at, updated_at FROM contacts ORDER BY contact_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.isBeforeFirst()) { System.out.println("\nNo contacts found."); return; }

            System.out.println("\n==========================================================================================================================================================================================================================");
            System.out.printf(
                    "%-5s %-10s %-10s %-10s %-10s %-14s %-14s %-25s %-40s %-12s %-25s %-25s\n",
                    "ID", "First", "Middle", "Last", "Nick", "Phone1", "Phone2",
                    "Email", "LinkedIn", "Birth", "Created", "Updated"
            );
            System.out.println("==========================================================================================================================================================================================================================");

            while (rs.next()) {
                System.out.printf(
                    "%-5d %-10s %-10s %-10s %-10s %-14s %-14s %-25s %-40s %-12s %-25s %-25s\n",
                    rs.getInt("contact_id"),
                    safe(rs.getString("first_name")), safe(rs.getString("middle_name")),
                    safe(rs.getString("last_name")), safe(rs.getString("nickname")),
                    safe(rs.getString("phone_primary")), safe(rs.getString("phone_secondary")),
                    safe(rs.getString("email")), safe(rs.getString("linkedin_url")),
                    safe(String.valueOf(rs.getDate("birth_date"))), safe(String.valueOf(rs.getTimestamp("created_at"))),
                    safe(String.valueOf(rs.getTimestamp("updated_at")))
                );
            }
            System.out.println("==========================================================================================================================================================================================================================");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ============================================================
    // ADD CONTACT
    // ============================================================
    public static void addContact(Contact c) {
        String sql = "INSERT INTO contacts (" +
                "first_name, middle_name, last_name, nickname, phone_primary, phone_secondary, " +
                "email, linkedin_url, birth_date, job_title, created_at, updated_at" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getFirstName()); ps.setString(2, c.getMiddleName());
            ps.setString(3, c.getLastName()); ps.setString(4, c.getNickname());
            ps.setString(5, c.getPhonePrimary()); ps.setString(6, c.getPhoneSecondary());
            ps.setString(7, c.getEmail()); ps.setString(8, c.getLinkedinUrl());
            if (c.getBirthDate() != null) ps.setDate(9, Date.valueOf(c.getBirthDate()));
            else ps.setNull(9, Types.DATE);
            ps.setString(10, c.getJobTitle());

            int rows = ps.executeUpdate();
            System.out.println(rows + " contact(s) inserted.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ============================================================
    // UPDATE CONTACT
    // ============================================================
    public static void updateContact(int id, String phone, String email) {
        StringBuilder sql = new StringBuilder("UPDATE contacts SET ");
        List<Object> params = new ArrayList<>();

        if (phone != null) { sql.append("phone_primary = ?, "); params.add(phone); }
        if (email != null) { sql.append("email = ?, "); params.add(email); }

        if (params.isEmpty()) { System.out.println("Nothing to update."); return; }
        
        sql.delete(sql.length() - 2, sql.length());
        sql.append(", updated_at = NOW() WHERE contact_id = ?"); 
        params.add(id);

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            int rows = ps.executeUpdate();
            System.out.println(rows + " contact(s) updated.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ============================================================
    // DELETE CONTACT
    // ============================================================
    public static void deleteContact(int id) {
        String sql = "DELETE FROM contacts WHERE contact_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows + " contact(s) deleted.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ============================================================
    // SEARCH RESULT HELPER
    // ============================================================
    private static void displaySearchResults(ResultSet rs) throws SQLException {
        if (!rs.isBeforeFirst()) { System.out.println("\nNo matching contacts found."); return; }
        System.out.println("\n==============================================================");
        System.out.printf("%-5s %-12s %-12s %-12s %-12s %-22s\n", "ID", "First", "Last", "Nick", "Phone1", "Email");
        System.out.println("==============================================================");
        while (rs.next()) {
            System.out.printf("%-5d %-12s %-12s %-12s %-12s %-22s\n",
                    rs.getInt("contact_id"), safe(rs.getString("first_name")), safe(rs.getString("last_name")),
                    safe(rs.getString("nickname")), safe(rs.getString("phone_primary")), safe(rs.getString("email")));
        }
        System.out.println("==============================================================");
    }

    // ============================================================
    // SINGLE AND MULTI-FIELD SEARCH
    // ============================================================

    public static void searchBySelectedField(String fieldName, String query) {
        List<String> allowedFields = Arrays.asList("first_name", "last_name", "nickname", "phone_primary", "email", "job_title");
        if (!allowedFields.contains(fieldName)) { System.out.println("Invalid search field specified."); return; }
        String sql = "SELECT contact_id, first_name, last_name, nickname, phone_primary, email FROM contacts WHERE " 
                     + fieldName + " LIKE ? ORDER BY contact_id";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%"); ResultSet rs = ps.executeQuery();
            displaySearchResults(rs); 
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void executeUserDefinedSearch(String whereClause) {
        String sql = "SELECT contact_id, first_name, last_name, nickname, phone_primary, email FROM contacts WHERE " 
                     + whereClause + " ORDER BY contact_id";
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            displaySearchResults(rs); 
        } catch (SQLException e) { System.out.println("ERROR: Failed to execute user-defined search. Check your query syntax."); e.printStackTrace(); }
    }

    // ============================================================
    // SORT CONTACTS
    // ============================================================
    public static void sortContacts(String field, String order) {

        List<String> allowed = Arrays.asList("first_name", "last_name", "birth_date");

        if (!allowed.contains(field)) { System.out.println("Invalid sort field."); return; }
        if (!order.equalsIgnoreCase("ASC") && !order.equalsIgnoreCase("DESC")) { System.out.println("Invalid order."); return; }

        String sql = "SELECT * FROM contacts ORDER BY " + field + " " + order;

        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n==============================================================");
            System.out.printf("%-5s %-12s %-12s %-12s %-22s\n", "ID", "First", "Last", "Birth", "Email");
            System.out.println("==============================================================");

            while (rs.next()) {
                System.out.printf("%-5d %-12s %-12s %-12s %-22s\n",
                        rs.getInt("contact_id"), safe(rs.getString("first_name")), safe(rs.getString("last_name")),
                        safe(String.valueOf(rs.getDate("birth_date"))), safe(rs.getString("email")));
            }
            System.out.println("==============================================================");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ============================================================
    // STATISTICS
    // ============================================================
    public static void showStatistics() {
        System.out.println("\n--- CONTACT STATISTICS ---");
        try (Connection conn = DBConnection.getConnection()) {
            // 1. Total records
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) AS cnt FROM contacts")) {
                rs.next(); System.out.println("1. Total Records: " + rs.getInt("cnt"));
            }
            // 2. Contacts with LinkedIn URL
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
                         "SELECT COUNT(*) AS cnt FROM contacts WHERE linkedin_url IS NOT NULL AND linkedin_url <> ''")) {
                rs.next(); System.out.println("2. Contacts with LinkedIn: " + rs.getInt("cnt"));
            }
            // 3. Count of individuals sharing the same first or last name
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
                         "SELECT COUNT(DISTINCT T1.contact_id) FROM contacts T1 JOIN contacts T2 ON T1.contact_id <> T2.contact_id AND (T1.first_name = T2.first_name OR T1.last_name = T2.last_name)")) {
                rs.next(); System.out.println("3. Individuals with Same First/Last Name: " + rs.getInt(1));
            }
            // 4. Oldest contact
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
                         "SELECT first_name, last_name, birth_date FROM contacts WHERE birth_date IS NOT NULL ORDER BY birth_date ASC LIMIT 1")) {
                if (rs.next()) { System.out.println("4. Oldest Contact: " + safe(rs.getString("first_name")) + " " + safe(rs.getString("last_name")) + " (" + rs.getDate("birth_date") + ")"); }
            }
            // 5. Youngest contact
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
                         "SELECT first_name, last_name, birth_date FROM contacts WHERE birth_date IS NOT NULL ORDER BY birth_date DESC LIMIT 1")) {
                if (rs.next()) { System.out.println("5. Youngest Contact: " + safe(rs.getString("first_name")) + " " + safe(rs.getString("last_name")) + " (" + rs.getDate("birth_date") + ")"); }
            }
            // 6. Average Age of Contacts
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(
                         "SELECT AVG(DATEDIFF(NOW(), birth_date) / 365.25) AS avg_age FROM contacts WHERE birth_date IS NOT NULL")) {
                if (rs.next()) {
                    double avgAge = rs.getDouble("avg_age");
                    System.out.printf("6. Average Age of Contacts: %.2f years\n", avgAge);
                }
            }
        } catch (SQLException e) { System.err.println("Error fetching statistics."); e.printStackTrace(); }
    }
}