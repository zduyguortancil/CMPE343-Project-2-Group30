package models;

import java.time.LocalDate;

public class Contact {
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private String phonePrimary;
    private String phoneSecondary;
    private String email;
    private String linkedinUrl;
    private LocalDate birthDate;
    private String jobTitle;

    // for reading from DB
    public Contact(int id, String firstName, String middleName, String lastName,
                   String nickname, String phonePrimary, String phoneSecondary,
                   String email, String linkedinUrl, LocalDate birthDate, String jobTitle) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.phonePrimary = phonePrimary;
        this.phoneSecondary = phoneSecondary;
        this.email = email;
        this.linkedinUrl = linkedinUrl;
        this.birthDate = birthDate;
        this.jobTitle = jobTitle;
    }

    // for creating new contact
    public Contact(String firstName, String middleName, String lastName,
                   String nickname, String phonePrimary, String phoneSecondary,
                   String email, String linkedinUrl, String jobTitle) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.phonePrimary = phonePrimary;
        this.phoneSecondary = phoneSecondary;
        this.email = email;
        this.linkedinUrl = linkedinUrl;
        this.jobTitle = jobTitle;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhonePrimary() {
        return phonePrimary;
    }

    public String getPhoneSecondary() {
        return phoneSecondary;
    }

    public String getEmail() {
        return email;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }
}

