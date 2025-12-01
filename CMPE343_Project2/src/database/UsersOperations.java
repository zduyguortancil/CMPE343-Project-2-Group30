package database;

import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.mindrot.jbcrypt.BCrypt; 

public class UsersOperations {

    // Empty/null fields fix
    private static String safe(String val) {
        return (val == null || val.trim().isEmpty()) ? "—" : val;
    }

    private static void printSearchHeader() {
        System.out.println();
        System.out.println("==============================================================================================");
        System.out.printf("%-5s %-12s %-12s %-12s %-20s %-15s %-20s\n",
                "ID", "Username", "Name", "Surname", "Role", "Title", "Created At");
        System.out.println("==============================================================================================");
    }
    
    // ==============================
    // USER COUNT (Used for initial check in Main.java)
    // ==============================
    public static int countUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            return 0; 
        }
        return 0;
    }
    
    // ==============================
    // UNIQUE CHECK: USERNAME
    // ==============================
    /**
     * Checks if a user with the given username already exists.
     */
    public static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Return true on error to prevent non-unique data insertion
        }
    }
    
    // ==============================
    // CHECK USER ROLE (Manager Silme Koruması için Eklendi)
    // ==============================
    /**
     * Checks if the user with the given ID has the 'Manager' role.
     */
    public static boolean isUserRoleManager(int userId) {
        String sql = "SELECT role FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Rolün "Manager" olup olmadığını kontrol et
                return "Manager".equalsIgnoreCase(rs.getString("role"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Kullanıcı bulunamazsa veya hata olursa korumayı kapat
    }
    
    // ==============================
    // LIST USERS
    // ==============================
    public static void listUsers() {
        String sql = "SELECT user_id, username, name, surname, role, title, created_at FROM users ORDER BY user_id";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            printSearchHeader();
            while (rs.next()) {
                System.out.printf("%-5d %-12s %-12s %-12s %-20s %-15s %-20s\n",
                        rs.getInt("user_id"),
                        safe(rs.getString("username")),
                        safe(rs.getString("name")),
                        safe(rs.getString("surname")),
                        safe(rs.getString("role")),
                        safe(rs.getString("title")),
                        safe(rs.getString("created_at")));
            }
            System.out.println("==============================================================================================");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // ADD USER (Secure Hashing)
    // ==============================
    public static void addUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, name, surname, role, title, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        
        // Hash the plaintext password using BCrypt
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, hashedPassword); // Store the hash
            ps.setString(3, user.getName());
            ps.setString(4, user.getSurname());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getTitle());

            int rows = ps.executeUpdate();
            System.out.println(rows + " user(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // UPDATE USER (Dynamic with Hashing)
    // ==============================
    public static void updateUser(User user) {
        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        List<Object> params = new ArrayList<>();

        if (user.getUsername() != null) {
            sql.append("username = ?, ");
            params.add(user.getUsername());
        }
        if (user.getPassword() != null) {
            sql.append("password_hash = ?, ");
            // Hash new password before update
            params.add(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }
        if (user.getName() != null) {
            sql.append("name = ?, ");
            params.add(user.getName());
        }
        if (user.getSurname() != null) {
            sql.append("surname = ?, ");
            params.add(user.getSurname());
        }
        if (user.getRole() != null) {
            sql.append("role = ?, ");
            params.add(user.getRole());
        }
        if (user.getTitle() != null) {
            sql.append("title = ?, ");
            params.add(user.getTitle());
        }

        if (params.isEmpty()) {
            System.out.println("Nothing to update.");
            return;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE user_id = ?");
        params.add(user.getId());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            int rows = ps.executeUpdate();
            System.out.println(rows + " user(s) updated.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // DELETE USER
    // ==============================
    public static void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows + " user(s) deleted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // LOGIN (Secure Authentication)
    // ==============================
    public static User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?"; 

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    
                    if (BCrypt.checkpw(password, storedHash)) {
                         return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("role"),
                            rs.getString("title")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Authentication failed
    }

    // ==============================
    // CHANGE PASSWORD (Secure Process)
    // ==============================
    public static void changePassword(User user, String currentPassword, String newPassword) {
        String checkSql = "SELECT password_hash FROM users WHERE user_id = ?";
        String storedHash = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setInt(1, user.getId());

            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                storedHash = rs.getString("password_hash");
            } else {
                System.out.println("User not found (Critical Error).");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // 1. Verify current password hash
        if (storedHash == null || !BCrypt.checkpw(currentPassword, storedHash)) {
            System.out.println("Current password is incorrect.");
            return;
        }
        
        // 2. Hash and store the new password
        String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        String updateSql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {

            ps.setString(1, newHashedPassword); 
            ps.setInt(2, user.getId());

            ps.executeUpdate();
            System.out.println("Password updated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ==============================
    // SEARCH USER BY SINGLE FIELD
    // ==============================
    public static void searchUserByField(String field, String value) {
        List<String> allowed = Arrays.asList("username", "name", "surname", "role", "title"); 
        if (!allowed.contains(field)) {
            System.out.println("Invalid field.");
            return;
        }

        String sql = "SELECT user_id, username, name, surname, role, title, created_at FROM users WHERE "
                + field + " LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + value + "%");
            ResultSet rs = ps.executeQuery();

            printSearchHeader();

            while (rs.next()) {
                System.out.printf("%-5d %-12s %-12s %-12s %-20s %-15s %-20s\n",
                        rs.getInt("user_id"),
                        safe(rs.getString("username")),
                        safe(rs.getString("name")),
                        safe(rs.getString("surname")),
                        safe(rs.getString("role")),
                        safe(rs.getString("title")),
                        safe(rs.getString("created_at")));
            }

            System.out.println("==============================================================================================");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // MULTI FIELD SEARCH
    // ==============================
    public static void searchUserByMultipleFields(String username, String name, String role, String title) {
        StringBuilder sql = new StringBuilder(
                "SELECT user_id, username, name, surname, role, title, created_at FROM users WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            sql.append(" AND username LIKE ?");
            params.add("%" + username + "%");
        }
        if (name != null && !name.isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }
        if (role != null && !role.isEmpty()) {
            sql.append(" AND role LIKE ?");
            params.add("%" + role + "%");
        }
        if (title != null && !title.isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + title + "%");
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();

            printSearchHeader();

            while (rs.next()) {
                System.out.printf("%-5d %-12s %-12s %-12s %-20s %-15s %-20s\n",
                        rs.getInt("user_id"),
                        safe(rs.getString("username")),
                        safe(rs.getString("name")),
                        safe(rs.getString("surname")),
                        safe(rs.getString("role")),
                        safe(rs.getString("title")),
                        safe(rs.getString("created_at")));
            }

            System.out.println("==============================================================================================");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}