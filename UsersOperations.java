package database;

import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersOperations {

    // List all users
    public static void listUsers() {
        String sql = "SELECT user_id, username, name, surname, role, title, created_at FROM users ORDER BY user_id";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\nID | Username | Name | Surname | Role | Title | Created At");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %s | %s | %s | %s | %s%n",
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("role"),
                        rs.getString("title"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add new user
    public static void addUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, name, surname, role, title, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); // ileride hashPassword(user.getPassword())
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

    // Update user (only non-null fields)
    public static void updateUser(User user) {
        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        List<Object> params = new ArrayList<>();

        if (user.getUsername() != null) {
            sql.append("username = ?, ");
            params.add(user.getUsername());
        }
        if (user.getPassword() != null) {
            sql.append("password_hash = ?, ");
            params.add(user.getPassword());
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

        sql.setLength(sql.length() - 2); // sondaki virgülü sil
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

    // Delete user
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

    // Simple search by single field
    public static void searchUserByField(String field, String value) {
        // Güvenli alan isimleri
        List<String> allowed = List.of("username", "name", "surname", "role", "title");
        if (!allowed.contains(field)) {
            System.out.println("Invalid field.");
            return;
        }

        String sql = "SELECT user_id, username, name, surname, role, title, created_at " +
                "FROM users WHERE " + field + " LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + value + "%");
            ResultSet rs = ps.executeQuery();

            System.out.println("\nID | Username | Name | Surname | Role | Title | Created At");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %s | %s | %s | %s | %s%n",
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("role"),
                        rs.getString("title"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Multi-field search
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
            System.out.println("\nID | Username | Name | Surname | Role | Title | Created At");
            while (rs.next()) {
                System.out.printf(
                        "%d | %s | %s | %s | %s | %s | %s%n",
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("role"),
                        rs.getString("title"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Login
    public static User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // İleride: hashPassword(password)
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Change password
    public static void changePassword(User user, String currentPassword, String newPassword) {
        // önce current doğru mu
        String checkSql = "SELECT * FROM users WHERE user_id = ? AND password_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setInt(1, user.getId());
            checkPs.setString(2, currentPassword);

            ResultSet rs = checkPs.executeQuery();
            if (!rs.next()) {
                System.out.println("Current password is incorrect.");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // doğruysa update
        String updateSql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, user.getId());

            ps.executeUpdate();
            System.out.println("Password updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}




