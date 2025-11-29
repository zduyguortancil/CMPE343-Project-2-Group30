package database;

import java.util.Scanner;
import database.UsersOperations;
import database.ContactsOperations;
import models.User;
import models.Contact;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // LOGIN
        User currentUser = null;
        while (currentUser == null) {
            System.out.println("=== Login ===");
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            currentUser = UsersOperations.authenticate(username, password);
            if (currentUser == null) {
                System.out.println("Invalid username or password. Please try again.\n");
            }
        }

        System.out.println("\nWelcome, " + currentUser.getName() +
                " (" + currentUser.getRole() + ")");

        String role = currentUser.getRole();
        boolean exit = false;

        while (!exit) {
            if ("Tester".equalsIgnoreCase(role)) {
                exit = testerMenu(scanner, currentUser);
            } else if ("Junior Developer".equalsIgnoreCase(role)) {
                exit = juniorMenu(scanner, currentUser);
            } else if ("Senior Developer".equalsIgnoreCase(role)) {
                exit = seniorMenu(scanner, currentUser);
            } else if ("Manager".equalsIgnoreCase(role)) {
                exit = managerMenu(scanner, currentUser);
            } else {
                System.out.println("Unknown role: " + role + ". Exiting.");
                exit = true;
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // ==== Tester Menu ====
    private static boolean testerMenu(Scanner scanner, User currentUser) {
        System.out.println("\n=== Tester Menu ===");
        System.out.println("1. List Contacts");
        System.out.println("2. Search Contacts by First Name");
        System.out.println("3. Sort Contacts");
        System.out.println("4. Change My Password");
        System.out.println("5. Logout / Exit");
        System.out.print("Select an option: ");

        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                ContactsOperations.listContacts();
                break;
            case "2":
                System.out.print("First name: ");
                String first = scanner.nextLine().trim();
                ContactsOperations.searchContactsByFirstName(first);
                break;
            case "3":
                System.out.println("Sort by:");
                System.out.println("1. First Name");
                System.out.println("2. Last Name");
                System.out.println("3. Birth Date");
                System.out.print("Choose field: ");
                String fieldChoice = scanner.nextLine().trim();
                String field;
                switch (fieldChoice) {
                    case "1": field = "first_name"; break;
                    case "2": field = "last_name"; break;
                    case "3": field = "birth_date"; break;
                    default:
                        System.out.println("Invalid field, defaulting to first_name.");
                        field = "first_name";
                }
                System.out.print("Order (ASC / DESC): ");
                String order = scanner.nextLine().trim().toUpperCase();
                if (!order.equals("ASC") && !order.equals("DESC")) {
                    System.out.println("Invalid order, defaulting to ASC.");
                    order = "ASC";
                }
                ContactsOperations.sortContacts(field, order);
                break;
            case "4":
                System.out.print("Current password: ");
                String cur = scanner.nextLine().trim();
                System.out.print("New password: ");
                String np = scanner.nextLine().trim();
                UsersOperations.changePassword(currentUser, cur, np);
                break;
            case "5":
                return true;
            default:
                System.out.println("Invalid option.");
        }
        return false;
    }

    // ==== Junior Menu ====
    private static boolean juniorMenu(Scanner scanner, User currentUser) {
        System.out.println("\n=== Junior Developer Menu ===");
        System.out.println("1. List Contacts");
        System.out.println("2. Search Contacts by First Name");
        System.out.println("3. Sort Contacts");
        System.out.println("4. Update Contact (phone/email)");
        System.out.println("5. Change My Password");
        System.out.println("6. Logout / Exit");
        System.out.print("Select an option: ");

        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                ContactsOperations.listContacts();
                break;
            case "2":
                System.out.print("First name: ");
                String first = scanner.nextLine().trim();
                ContactsOperations.searchContactsByFirstName(first);
                break;
            case "3":
                System.out.println("Sort by:");
                System.out.println("1. First Name");
                System.out.println("2. Last Name");
                System.out.println("3. Birth Date");
                System.out.print("Choose field: ");
                String fieldChoice = scanner.nextLine().trim();
                String field;
                switch (fieldChoice) {
                    case "1": field = "first_name"; break;
                    case "2": field = "last_name"; break;
                    case "3": field = "birth_date"; break;
                    default:
                        System.out.println("Invalid field, defaulting to first_name.");
                        field = "first_name";
                }
                System.out.print("Order (ASC / DESC): ");
                String order = scanner.nextLine().trim().toUpperCase();
                if (!order.equals("ASC") && !order.equals("DESC")) {
                    System.out.println("Invalid order, defaulting to ASC.");
                    order = "ASC";
                }
                ContactsOperations.sortContacts(field, order);
                break;
            case "4":
                int cid = readInt(scanner, "Contact ID: ");
                System.out.print("New primary phone (leave blank to skip): ");
                String phone = scanner.nextLine().trim();
                if (phone.isEmpty()) phone = null;
                System.out.print("New email (leave blank to skip): ");
                String email = scanner.nextLine().trim();
                if (email.isEmpty()) email = null;
                ContactsOperations.updateContact(cid, phone, email);
                break;
            case "5":
                System.out.print("Current password: ");
                String cur = scanner.nextLine().trim();
                System.out.print("New password: ");
                String np = scanner.nextLine().trim();
                UsersOperations.changePassword(currentUser, cur, np);
                break;
            case "6":
                return true;
            default:
                System.out.println("Invalid option.");
        }
        return false;
    }

    // ==== Senior Menu ====
    private static boolean seniorMenu(Scanner scanner, User currentUser) {
        System.out.println("\n=== Senior Developer Menu ===");
        System.out.println("1. List Contacts");
        System.out.println("2. Search Contacts by First Name");
        System.out.println("3. Sort Contacts");
        System.out.println("4. Add Contact");
        System.out.println("5. Update Contact (phone/email)");
        System.out.println("6. Delete Contact");
        System.out.println("7. Change My Password");
        System.out.println("8. Logout / Exit");
        System.out.print("Select an option: ");

        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                ContactsOperations.listContacts();
                break;
            case "2":
                System.out.print("First name: ");
                String first = scanner.nextLine().trim();
                ContactsOperations.searchContactsByFirstName(first);
                break;
            case "3":
                System.out.println("Sort by:");
                System.out.println("1. First Name");
                System.out.println("2. Last Name");
                System.out.println("3. Birth Date");
                System.out.print("Choose field: ");
                String fieldChoice = scanner.nextLine().trim();
                String field;
                switch (fieldChoice) {
                    case "1": field = "first_name"; break;
                    case "2": field = "last_name"; break;
                    case "3": field = "birth_date"; break;
                    default:
                        System.out.println("Invalid field, defaulting to first_name.");
                        field = "first_name";
                }
                System.out.print("Order (ASC / DESC): ");
                String order = scanner.nextLine().trim().toUpperCase();
                if (!order.equals("ASC") && !order.equals("DESC")) {
                    System.out.println("Invalid order, defaulting to ASC.");
                    order = "ASC";
                }
                ContactsOperations.sortContacts(field, order);
                break;
            case "4":
                addContactFlow(scanner);
                break;
            case "5":
                int cid = readInt(scanner, "Contact ID: ");
                System.out.print("New primary phone (leave blank to skip): ");
                String phone = scanner.nextLine().trim();
                if (phone.isEmpty()) phone = null;
                System.out.print("New email (leave blank to skip): ");
                String email = scanner.nextLine().trim();
                if (email.isEmpty()) email = null;
                ContactsOperations.updateContact(cid, phone, email);
                break;
            case "6":
                int delId = readInt(scanner, "Contact ID to delete: ");
                ContactsOperations.deleteContact(delId);
                break;
            case "7":
                System.out.print("Current password: ");
                String cur = scanner.nextLine().trim();
                System.out.print("New password: ");
                String np = scanner.nextLine().trim();
                UsersOperations.changePassword(currentUser, cur, np);
                break;
            case "8":
                return true;
            default:
                System.out.println("Invalid option.");
        }
        return false;
    }

    // ==== Manager Menu ====
    private static boolean managerMenu(Scanner scanner, User currentUser) {
        System.out.println("\n=== Manager Menu ===");
        System.out.println("1. List Users");
        System.out.println("2. Add User");
        System.out.println("3. Update User");
        System.out.println("4. Delete User");
        System.out.println("5. Contacts Statistics");
        System.out.println("6. List Contacts");
        System.out.println("7. Change My Password");
        System.out.println("8. Logout / Exit");
        System.out.print("Select an option: ");

        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                UsersOperations.listUsers();
                break;
            case "2":
                addUserFlow(scanner);
                break;
            case "3":
                updateUserFlow(scanner);
                break;
            case "4":
                int uid = readInt(scanner, "User ID to delete: ");
                UsersOperations.deleteUser(uid);
                break;
            case "5":
                ContactsOperations.showStatistics();
                break;
            case "6":
                ContactsOperations.listContacts();
                break;
            case "7":
                System.out.print("Current password: ");
                String cur = scanner.nextLine().trim();
                System.out.print("New password: ");
                String np = scanner.nextLine().trim();
                UsersOperations.changePassword(currentUser, cur, np);
                break;
            case "8":
                return true;
            default:
                System.out.println("Invalid option.");
        }
        return false;
    }

    // ==== Shared flows ====

    private static void addUserFlow(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Surname: ");
        String surname = scanner.nextLine().trim();
        System.out.print("Role (Tester / Junior Developer / Senior Developer / Manager): ");
        String role = scanner.nextLine().trim();
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        User newUser = new User(username, password, name, surname, role, title);
        UsersOperations.addUser(newUser);
    }

    private static void updateUserFlow(Scanner scanner) {
        int id = readInt(scanner, "User ID to update: ");

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
    }

    private static void addContactFlow(Scanner scanner) {
        System.out.print("First Name: ");
        String first = scanner.nextLine().trim();
        System.out.print("Middle Name (can be empty): ");
        String middle = scanner.nextLine().trim();
        if (middle.isEmpty()) middle = null;
        System.out.print("Last Name: ");
        String last = scanner.nextLine().trim();
        System.out.print("Nickname: ");
        String nick = scanner.nextLine().trim();
        System.out.print("Primary Phone: ");
        String phone1 = scanner.nextLine().trim();
        System.out.print("Secondary Phone (can be empty): ");
        String phone2 = scanner.nextLine().trim();
        if (phone2.isEmpty()) phone2 = null;
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("LinkedIn URL (can be empty): ");
        String linkedin = scanner.nextLine().trim();
        if (linkedin.isEmpty()) linkedin = null;
        System.out.print("Job Title (can be empty): ");
        String jobTitle = scanner.nextLine().trim();
        if (jobTitle.isEmpty()) jobTitle = null;

        Contact c = new Contact(first, middle, last, nick, phone1, phone2, email, linkedin, jobTitle);
        ContactsOperations.addContact(c);
    }
}
