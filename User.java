package models;

public class User {
    private int userId;
    private String username;
    private String passwordHash; 
    private String name;
    private String surname;
    private String role;
    private String title;

     // Add User constructor
    public User(String username, String passwordHash, String name, String surname, String role, String title) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.title = title;
    }

    // Constructor (ID ile)
    public User(int userId, String username, String passwordHash, String name, String surname, String role, String title) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.title = title;
    }

    // Getter ve Setterlar
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public String toString() {
        return userId + " | " + username + " | " + passwordHash + " | " + name + " | " + surname + " | " + role;
    }
}
