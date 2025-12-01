package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Contact {

    private int contactId;

    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;

    private String phonePrimary;
    private String phoneSecondary;

    private String email;
    private String linkedinUrl;
    private String jobTitle;

    private LocalDate birthDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // === CONSTRUCTOR for ADDING NEW CONTACT ===
    public Contact(String firstName, String middleName, String lastName, String nickname,
                   String phonePrimary, String phoneSecondary, String email,
                   String linkedinUrl, String jobTitle, LocalDate birthDate) {

        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.phonePrimary = phonePrimary;
        this.phoneSecondary = phoneSecondary;
        this.email = email;
        this.linkedinUrl = linkedinUrl;
        this.jobTitle = jobTitle;
        this.birthDate = birthDate;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // === CONSTRUCTOR for READING FROM DATABASE ===
    public Contact(int contactId, String firstName, String middleName, String lastName, String nickname,
                   String phonePrimary, String phoneSecondary, String email,
                   String linkedinUrl, String jobTitle, LocalDate birthDate,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.contactId = contactId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.phonePrimary = phonePrimary;
        this.phoneSecondary = phoneSecondary;
        this.email = email;
        this.linkedinUrl = linkedinUrl;
        this.jobTitle = jobTitle;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // === GETTERS ===
    public int getContactId() { return contactId; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getNickname() { return nickname; }

    public String getPhonePrimary() { return phonePrimary; }
    public String getPhoneSecondary() { return phoneSecondary; }

    public String getEmail() { return email; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public String getJobTitle() { return jobTitle; }

    public LocalDate getBirthDate() { return birthDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // === SETTERS (only for update) ===
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
