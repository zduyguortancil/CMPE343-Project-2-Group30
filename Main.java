package database;

import java.util.Scanner;
import database.UsersOperations;
import database.ContactsOperations;
import models.User;
import models.Contact;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. List Users");
            System.out.println("2. Add User");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. List Contacts");
            System.out.println("6. Add Contact");
            System.out.println("7. Update Contact");
            System.out.println("8. Delete Contact");
            System.out.println("9. Exit");
            System.out.println("10. Search User by Field");
            System.out.println("11. Search User by Multiple Fields");
            System.out.print("Select an option: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    UsersOperations.listUsers();
                    break;

                case "2": // Add User
                    System.out.print("Username: ");
                    String username = scanner.nextLine();

                    System.out.print("Password: ");
                    String password = scanner.nextLine();

                    System.out.print("Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Surname: ");
                    String surname = scanner.nextLine();

                    System.out.print("Role (Tester / Junior Developer / Senior Developer / Manager): ");
                    String role = scanner.nextLine();

                    System.out.print("Title: ");
                    String title = scanner.nextLine();

                    User newUser = new User(username, password, name, surname, role, title);
                    UsersOperations.addUser(newUser);
                    break;

                case "3": // Update User
                    System.out.print("Enter the user ID to update: ");
                    int id = Integer.parseInt(scanner.nextLine());

                    System.out.print("New Username (leave blank to skip): ");
                    String newUsername = scanner.nextLine();

                    System.out.print("New Password (leave blank to skip): ");
                    String newPassword = scanner.nextLine();

                    System.out.print("New Name (leave blank to skip): ");
                    String newName = scanner.nextLine();

                    System.out.print("New Surname (leave blank to skip): ");
                    String newSurname = scanner.nextLine();

                    System.out.print("New Role (leave blank to skip): ");
                    String newRole = scanner.nextLine();

                    System.out.print("New Title (leave blank to skip): ");
                    String newTitle = scanner.nextLine();

                    User updatedUser = new User(
                        id,
                        newUsername.isEmpty() ? null : newUsername,
                        newPassword.isEmpty() ? null : newPassword,
                        newName.isEmpty() ? null : newName,
                        newSurname.isEmpty() ? null : newSurname,
                        newRole.isEmpty() ? null : newRole,
                        newTitle.isEmpty() ? null : newTitle
                    );

                    UsersOperations.updateUser(updatedUser);
                    break;

                case "4": // Delete User
                    System.out.print("Enter the user ID to delete: ");
                    int deleteId = Integer.parseInt(scanner.nextLine());
                    UsersOperations.deleteUser(deleteId);
                    break;

                case "5": // List Contacts
                    ContactsOperations.listContacts();
                    break;

                case "6": // Add Contact
                    System.out.print("First Name: ");
                    String first = scanner.nextLine();
                    System.out.print("Middle Name: ");
                    String middle = scanner.nextLine();
                    System.out.print("Last Name: ");
                    String last = scanner.nextLine();
                    System.out.print("Nickname: ");
                    String nick = scanner.nextLine();
                    System.out.print("Primary Phone: ");
                    String phone1 = scanner.nextLine();
                    System.out.print("Secondary Phone: ");
                    String phone2 = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("LinkedIn URL: ");
                    String linkedin = scanner.nextLine();
                    System.out.print("Title: ");
                    String contactTitle = scanner.nextLine();

                    Contact newContact = new Contact( 
                    "Ali",
                    "M",
                    "Veli",
                    "Nick",
                    "5551234567",
                    null,
                    "ali@example.com",
                    null,
                    null);
                    ContactsOperations.addContact(newContact);
                    break;

                case "7": // Update Contact
                    System.out.print("Enter Contact ID to update: ");
                    int contactId = Integer.parseInt(scanner.nextLine());
                    System.out.print("New Primary Phone: ");
                    String newPhone = scanner.nextLine();
                    System.out.print("New Email: ");
                    String newEmail = scanner.nextLine();
                    ContactsOperations.updateContact(contactId, newPhone, newEmail);
                    break;

                case "8": // Delete Contact
                    System.out.print("Enter Contact ID to delete: ");
                    int deleteContactId = Integer.parseInt(scanner.nextLine());
                    ContactsOperations.deleteContact(deleteContactId);
                    break;

                case "9": // Exit
                    System.out.println("Exiting program. Goodbye!");
                    exit = true;
                    break;

                case "10": // Search User by Single Field
                    System.out.println("Search by field:");
                    System.out.println("1. Username");
                    System.out.println("2. Name");
                    System.out.println("3. Surname");
                    System.out.println("4. Role");
                    System.out.println("5. Title");
                    System.out.print("Enter choice: ");

                    String fieldChoice = scanner.nextLine();
                    String field;

                    switch (fieldChoice) {
                        case "1": field = "username"; break;
                        case "2": field = "name"; break;
                        case "3": field = "surname"; break;
                        case "4": field = "role"; break;
                        case "5": field = "title"; break;
                        default: field = "username"; break;
                    }

                    System.out.print("Enter value: ");
                    String value = scanner.nextLine();

                    UsersOperations.searchUserByField(field, value);
                    break;

                case "11": // Search User by Multiple Fields
                    System.out.println("Enter search values (leave blank to skip):");

                    System.out.print("Username: ");
                    String multiUsername = scanner.nextLine();

                    System.out.print("Name: ");
                    String multiName = scanner.nextLine();

                    System.out.print("Role: ");
                    String multiRole = scanner.nextLine();

                    System.out.print("Title: ");
                    String multiTitle = scanner.nextLine();

                    UsersOperations.searchUserByMultipleFields(multiUsername, multiName, multiRole, multiTitle);
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }

        scanner.close();
    }
}
