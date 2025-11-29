package database;

import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UsersOperations {

    // Kullanıcı listeleme
    public static void listUsers() {
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("ID | Username | Password | Name | Surname | Role");
            while (rs.next()) {
                System.out.println(
                    rs.getInt("user_id") + " | " +
                    rs.getString("username") + " | " +
                    rs.getString("password_hash") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("surname") + " | " +
                    rs.getString("role")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(User user) {
    String sql = "INSERT INTO users (username, password_hash, name, surname, role, title) "
               + "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPasswordHash());
        ps.setString(3, user.getName());
        ps.setString(4, user.getSurname());
        ps.setString(5, user.getRole());
        ps.setString(6, user.getTitle());   // ✔ title NULL olabilir

        ps.executeUpdate();
        System.out.println("User added successfully!");

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


     // Delete User
    public static void deleteUser(int userId) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userId);

            int rows = ps.executeUpdate();
            System.out.println(rows + " user deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tek alan arama
public static void searchUserByField(String field, String value) {
    String sql = "SELECT * FROM users WHERE " + field + " LIKE ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, "%" + value + "%");
        ResultSet rs = ps.executeQuery();

        System.out.println("ID | Username | Password | Name | Surname | Role");
        while (rs.next()) {
            System.out.println(
                rs.getInt("user_id") + " | " +
                rs.getString("username") + " | " +
                rs.getString("password") + " | " +
                rs.getString("name") + " | " +
                rs.getString("surname") + " | " +
                rs.getString("role")
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static void updateUser(User user) {
    String sql = "UPDATE users SET username=?, password_hash=?, name=?, surname=?, role=?, title=? "
               + "WHERE user_id=?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPasswordHash());
        ps.setString(3, user.getName());
        ps.setString(4, user.getSurname());
        ps.setString(5, user.getRole());
        ps.setString(6, user.getTitle());
        ps.setInt(7, user.getUserId());

        ps.executeUpdate();
        System.out.println("User updated successfully!");

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }


    // coklu arama
    public static void searchUserByMultipleFields(String username, String name, String role, String title) {
    try (Connection connection = DBConnection.getConnection()) {

        StringBuilder query = new StringBuilder("SELECT * FROM users WHERE 1=1");

        if (!username.isEmpty()) query.append(" AND username LIKE ?");
        if (!name.isEmpty()) query.append(" AND name LIKE ?");
        if (!role.isEmpty()) query.append(" AND role LIKE ?");
        if (!title.isEmpty()) query.append(" AND title LIKE ?");

        PreparedStatement ps = connection.prepareStatement(query.toString());

        int index = 1;
        if (!username.isEmpty()) ps.setString(index++, "%" + username + "%");
        if (!name.isEmpty()) ps.setString(index++, "%" + name + "%");
        if (!role.isEmpty()) ps.setString(index++, "%" + role + "%");
        if (!title.isEmpty()) ps.setString(index++, "%" + title + "%");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            System.out.println(
                rs.getInt("user_id") + " | " +
                rs.getString("username") + " | " +
                rs.getString("name") + " | " +
                rs.getString("surname") + " | " +
                rs.getString("role") + " | " +
                rs.getString("title")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}

