package models;

public class User {
    private int id;
    private String username;
    private String password; // plain for now, DB column is password_hash
    private String name;
    private String surname;
    private String role;
    private String title;

    // for reading from DB
    public User(int id, String username, String name, String surname, String role, String title) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.title = title;
    }

    // for creating new user (insert)
    public User(String username, String password, String name, String surname, String role, String title) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.title = title;
    }

    // for update
    public User(int id, String username, String password, String name, String surname, String role, String title) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getRole() {
        return role;
    }

    public String getTitle() {
        return title;
    }
}

