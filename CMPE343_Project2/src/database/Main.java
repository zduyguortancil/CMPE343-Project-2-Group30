package database;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Arrays; 

import models.User;
import models.Contact;

public class Main {

    // ============================================================
    // PATTERNS (Regex)
    // ============================================================

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-zÇŞĞÜÖİçşğüöı]+$");

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9._-]+$"); 

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{10,15}$");

    private static final Pattern PHONE_FORMAT_PATTERN =
            Pattern.compile("^0[0-9]{3}[\\s]?[0-9]{3}[\\s]?[0-9]{2}[\\s]?[0-9]{2}$");


    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@(gmail|hotmail|outlook|yahoo)\\.com$");

    private static final Pattern LINKEDIN_PATTERN =
            Pattern.compile("^(https?://)?(www\\.)?linkedin\\.com/in/[A-Za-z0-9\\-_]+/?$");


    // ============================================================
    //  MAIN
    // ============================================================

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            clearScreen();
            printTitle();

            User currentUser = null;

            // LOGIN LOOP
            while (currentUser == null) {
                System.out.println("=== Login ===");
                System.out.print("Username: ");
                String username = scanner.nextLine().trim();
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();

                currentUser = UsersOperations.authenticate(username, password);

                if (currentUser == null) {
                    printBox("Invalid username or password.");
                    waitEnter(scanner);
                    clearScreen();
                    printTitle();
                }
            }

            printBox("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
            waitEnter(scanner);

            boolean logout = false;

            while (!logout) {
                // TERMINAL GÖRÜNÜM DÜZELTMESİ
                clearScreen();
                printTitle(); 
                logout = renderMenu(scanner, currentUser);
            }
        }
    }

    // ============================================================
    // CONSOLE HELPERS
    // ============================================================

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void waitEnter(Scanner sc) {
        System.out.println("\nPress ENTER to continue...");
        sc.nextLine();
    }

    public static void printTitle() {
        System.out.println("=== ROLE-BASED CONTACT MANAGEMENT SYSTEM ===\n");
    }

    public static void printBox(String message) {
        System.out.println("\n+----------------------------------------------+");
        System.out.println("|  " + message);
        System.out.println("+----------------------------------------------+\n");
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                printBox("Please enter a valid number.");
            }
        }
    }

    // ============================================================
    // DATA VALIDATION READERS
    // ============================================================
    
    /**
     * Reads a required name/string that must match NAME_PATTERN.
     */
    private static String readName(Scanner sc, String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                printBox("This field is required.");
                continue;
            }
            if (NAME_PATTERN.matcher(input).matches()) break;
            printBox("Invalid input! Must contain letters only (Turkish letters are allowed).");
        }
        return input;
    }
    
    /**
     * Reads an optional field. If user enters '0', it returns null.
     */
    private static String readOptionalNameOrZero(Scanner sc, String prompt, Pattern pattern, String errorMessage) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();

            if (input.equals("0")) {
                return null; // User chose to skip (null assignment)
            }
            if (input.isEmpty()) {
                printBox("Enter a value, or type '0' to skip/assign null.");
                continue;
            }
            if (pattern.matcher(input).matches()) {
                return input;
            }
            printBox(errorMessage);
        }
    }
    
    /**
     * Reads a required nickname that must match NICKNAME_PATTERN and be unique.
     */
    private static String readRequiredNickname(Scanner sc, String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                printBox("Nickname is required.");
                continue;
            }
            if (!NICKNAME_PATTERN.matcher(input).matches()) {
                 printBox("Invalid Nickname! Must contain letters, numbers, dot, or underscore.");
                 continue;
            }
            
            // Benzersizlik Kontrolü
            if (ContactsOperations.nicknameExists(input)) {
                printBox("This nickname already exists! Please enter a unique one.");
                continue;
            }
            
            break;
        }
        return input;
    }

    /**
     * Reads a required phone number in the expected format, cleans it, and checks for uniqueness.
     */
    private static String readUniquePhoneFormat(Scanner sc, String prompt) {
        String input;
        String cleanPhone;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();

            if (input.isEmpty()) {
                printBox("Phone Number (Phone1) is required.");
                continue;
            }

            if (PHONE_FORMAT_PATTERN.matcher(input).matches()) {
                cleanPhone = input.replace(" ", "");
                if (PHONE_PATTERN.matcher(cleanPhone).matches()) {
                    // Check for uniqueness
                    if (ContactsOperations.phoneExists(cleanPhone)) {
                        printBox("This phone number already exists in the system! Please enter a different one.");
                        continue;
                    }
                    return cleanPhone;
                }
            }
            printBox("Invalid Phone format! Must be 05xx xxx xx xx and unique.");
        }
    }
    
    /**
     * Reads an optional phone number in the expected format, cleans it.
     */
    private static String readOptionalPhoneFormatOrZero(Scanner sc, String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();

            if (input.equals("0")) {
                return null; // User chose to skip (null assignment)
            }
            if (input.isEmpty()) {
                printBox("Phone2 is optional. Enter a value, or type '0' to skip/assign null.");
                continue;
            }

            if (PHONE_FORMAT_PATTERN.matcher(input).matches()) {
                return input.replace(" ", ""); // Clean it for DB
            }
            
            printBox("Invalid Phone2 format! Must be 05xx xxx xx xx, or type '0' to skip/assign null.");
        }
    }
    
    /**
     * Reads a required email and checks for uniqueness.
     */
    private static String readUniqueEmail(Scanner sc, String prompt) {
        String email;
        while (true) {
            System.out.print(prompt);
            email = sc.nextLine().trim();

            if (email.isEmpty()) {
                printBox("Email is required.");
                continue;
            }

            if (!EMAIL_PATTERN.matcher(email).matches()) {
                printBox("Invalid Email format! Must end with @gmail.com, @hotmail.com, @outlook.com, or @yahoo.com");
                continue;
            }

            // Check for uniqueness
            if (ContactsOperations.emailExists(email)) {
                printBox("This email already exists in the system! Please enter a different one.");
                continue;
            }

            break;
        }
        return email;
    }
    
    /**
     * Reads a required LinkedIn URL and checks for uniqueness.
     */
    private static String readUniqueLinkedIn(Scanner sc, String prompt) {
        String link;
        while (true) {
            System.out.print(prompt);
            link = sc.nextLine().trim();

            if (link.isEmpty()) {
                printBox("LinkedIn URL is required.");
                continue;
            }

            if (LINKEDIN_PATTERN.matcher(link).matches()) {
                // Check for uniqueness
                if (ContactsOperations.linkedinExists(link)) {
                    printBox("This LinkedIn URL already exists in the system! Please enter a different one.");
                    continue;
                }
                break;
            }
            printBox("Invalid LinkedIn URL format. Must be a valid linkedin.com/in/... URL.");
        }
        return link;
    }
    
    /**
     * Reads an optional birth date, checking for future dates and valid date structure (including leap years).
     */
    private static LocalDate readBirthDate(Scanner sc) {
        LocalDate birthDate = null;
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        while (true) {
            System.out.print("Birth Date (dd-MM-yyyy) or leave empty: ");
            String bd = sc.nextLine().trim();
            if (bd.isEmpty()) {
                return null; // Boş bırakıldıysa null döner.
            }

            try {
                birthDate = LocalDate.parse(bd, fmt);

                // Future date check
                if (birthDate.isAfter(today)) {
                    printBox("Birth Date cannot be a future date! Please enter a valid date (today or earlier).");
                    continue; // Hatalı, tekrar sor
                }
                return birthDate; 

            } catch (DateTimeParseException e) {
                printBox("Invalid date! Please use the format dd-MM-yyyy and ensure it's a real date (e.g., check for 29 Feb in non-leap years).");
            }
        }
    }

    // ============================================================
    // MENU DISPATCHER
    // ============================================================

    private static boolean renderMenu(Scanner scanner, User currentUser) {

        String role = currentUser.getRole();

        if ("Tester".equalsIgnoreCase(role)) return testerMenu(scanner, currentUser);
        if ("Junior Developer".equalsIgnoreCase(role)) return juniorMenu(scanner, currentUser);
        if ("Senior Developer".equalsIgnoreCase(role)) return seniorMenu(scanner, currentUser);
        if ("Manager".equalsIgnoreCase(role)) return managerMenu(scanner, currentUser);

        printBox("Unknown role: " + role);
        return true;
    }

    // ============================================================
    // ROLE MENUS 
    // ============================================================

    private static boolean testerMenu(Scanner scanner, User currentUser) {
        System.out.println("=== Tester Menu ===");
        System.out.println("1. List Contacts");
        System.out.println("2. Search Contacts (Tekli/Çoklu Alan)"); 
        System.out.println("3. Sort Contacts");
        System.out.println("4. Change Password");
        System.out.println("5. Logout");
        System.out.println("6. Terminate System");

        System.out.print("Select: ");

        switch (scanner.nextLine().trim()) {

            case "1":
                UsersOperations.listUsers(); // DB çıktısı hemen temiz bir sayfada başlar
                waitEnter(scanner);
                return false;

            case "2":
                searchFlow(scanner); 
                return false;

            case "3":
                return sortFlow(scanner);

            case "4":
                changePasswordFlow(scanner, currentUser);
                return false;

            case "5":
                return true;

            case "6":
                System.exit(0);

            default:
                printBox("Invalid option.");
                waitEnter(scanner);
                return false;
        }
    }

    private static boolean juniorMenu(Scanner scanner, User currentUser) {
        System.out.println("=== Junior Developer Menu ===");
        System.out.println("1. List Contacts");
        System.out.println("2. Search Contacts (Tekli/Çoklu Alan)"); 
        System.out.println("3. Sort Contacts");
        System.out.println("4. Update Contact");
        System.out.println("5. Change Password");
        System.out.println("6. Logout");
        System.out.println("7. Terminate System");
        System.out.print("Select: ");

        switch (scanner.nextLine().trim()) {

            case "1":
                ContactsOperations.listContacts();
                waitEnter(scanner);
                return false;

            case "2":
                searchFlow(scanner);
                return false;

            case "3":
                return sortFlow(scanner);

            case "4":
                updateContactFlow(scanner);
                waitEnter(scanner);
                return false;

            case "5":
                changePasswordFlow(scanner, currentUser);
                return false;

            case "6":
                return true;

            case "7":
                System.exit(0);

            default:
                printBox("Invalid option.");
                waitEnter(scanner);
                return false;
        }
    }

    private static boolean seniorMenu(Scanner scanner, User currentUser) {
        System.out.println("=== Senior Developer Menu ===");
        System.out.println("1. List Contacts");
        System.out.println("2. Search Contacts (Tekli/Çoklu Alan)"); 
        System.out.println("3. Sort Contacts");
        System.out.println("4. Add Contact");
        System.out.println("5. Update Contact");
        System.out.println("6. Delete Contact");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
        System.out.println("9. Terminate System");
        System.out.print("Select: ");

        switch (scanner.nextLine().trim()) {

            case "1":
                ContactsOperations.listContacts();
                waitEnter(scanner);
                return false;

            case "2":
                searchFlow(scanner);
                return false;

            case "3":
                return sortFlow(scanner);

            case "4":
                addContactFlow(scanner);
                waitEnter(scanner);
                return false;

            case "5":
                updateContactFlow(scanner);
                waitEnter(scanner);
                return false;

            case "6":
                int delId = readInt(scanner, "Contact ID to delete: ");
                ContactsOperations.deleteContact(delId);
                waitEnter(scanner);
                return false;

            case "7":
                changePasswordFlow(scanner, currentUser);
                return false;

            case "8":
                return true;

            case "9":
                System.exit(0);

            default:
                printBox("Invalid option.");
                waitEnter(scanner);
                return false;
        }
    }

    private static boolean managerMenu(Scanner scanner, User currentUser) {
        System.out.println("=== Manager Menu ===");
        System.out.println("1. List Users");
        System.out.println("2. Add User");
        System.out.println("3. Update User");
        System.out.println("4. Delete User");
        System.out.println("5. Contacts Statistics");
        System.out.println("6. List Contacts");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
        System.out.println("9. Terminate System");
        System.out.print("Select: ");

        switch (scanner.nextLine().trim()) {

            case "1":
                UsersOperations.listUsers(); // DB çıktısı hemen temiz bir sayfada başlar
                waitEnter(scanner);
                return false;

            case "2":
                addUserFlow(scanner);
                waitEnter(scanner);
                return false;

            case "3":
                updateUserFlow(scanner);
                waitEnter(scanner);
                return false;

            case "4":
                int idToDelete = readInt(scanner, "User ID to delete: ");
    
                // KRİTİK KONTROL: Yönetici silme koruması
                if (UsersOperations.isUserRoleManager(idToDelete)) {
                    printBox("Error: Cannot delete a user with the 'Manager' role.");
                } else {
                    UsersOperations.deleteUser(idToDelete);
                }
                
                waitEnter(scanner);
                return false;

            case "5":
                ContactsOperations.showStatistics();
                waitEnter(scanner);
                return false;

            case "6":
                ContactsOperations.listContacts();
                waitEnter(scanner);
                return false;

            case "7":
                changePasswordFlow(scanner, currentUser);
                return false;

            case "8":
                return true;

            case "9":
                System.exit(0);

            default:
                printBox("Invalid option.");
                waitEnter(scanner);
                return false;
        }
    }

    // ============================================================
    // SHARED FLOWS
    // ============================================================

    private static boolean sortFlow(Scanner scanner) {
        System.out.println("Sort by:");
        System.out.println("1. First Name");
        System.out.println("2. Last Name");
        System.out.println("3. Birth Date");
        System.out.print("Choose: ");

        String field = switch (scanner.nextLine().trim()) {
            case "1" -> "first_name";
            case "2" -> "last_name";
            case "3" -> "birth_date";
            default -> "first_name";
        };

        System.out.print("Order (ASC/DESC): ");
        String order = scanner.nextLine().trim().toUpperCase();
        if (!order.equals("ASC") && !order.equals("DESC")) order = "ASC";

        ContactsOperations.sortContacts(field, order);
        waitEnter(scanner);
        return false;
    }

    private static void changePasswordFlow(Scanner sc, User user) {
        System.out.print("Current password: ");
        String cur = sc.nextLine().trim();

        System.out.print("New password: ");
        String np = sc.nextLine().trim();

        UsersOperations.changePassword(user, cur, np);
        // EKRAN TEMİZLİĞİ VE YENİDEN BAŞLATMA DÜZELTMESİ
        clearScreen(); 
        waitEnter(sc);
    }
    
    // ----------------------------------------------------------------------------------
    // YENİ AKIŞ: ARAMA (SEARCH) METOTLARI
    // ----------------------------------------------------------------------------------

    private static boolean searchFlow(Scanner scanner) {
        System.out.println("=== Contact Search Options ===");
        System.out.println("1. Single Field Search (Ad, Soyad, Telefon, vb.)");
        System.out.println("2. Multi-Field / Custom Search (Kendi sorgunuzu oluşturun)");
        System.out.println("3. Geri Dön");
        System.out.print("Seçiminiz: ");

        switch (scanner.nextLine().trim()) {
            case "1":
                singleFieldSearch(scanner);
                break;
            case "2":
                multiFieldSearch(scanner);
                break;
            case "3":
                return false;
            default:
                printBox("Geçersiz seçenek.");
        }
        waitEnter(scanner);
        return false;
    }

    private static void singleFieldSearch(Scanner scanner) {
        System.out.println("\n--- Tek Alan Seçimi ---");
        System.out.println("1. First Name");
        System.out.println("2. Last Name");
        System.out.println("3. Email");
        System.out.print("Arama yapmak istediğiniz alanı seçin: ");
        String fieldChoice = scanner.nextLine().trim();

        String fieldName = switch (fieldChoice) {
            case "1" -> "first_name";
            case "2" -> "last_name";
            case "3" -> "email";
            default -> null; // Geçersizse null döndür
        }; 

        if (fieldName == null) {
            printBox("Geçersiz alan seçimi."); 
            return; // Metottan çık
        }

        System.out.print("Aranacak değer (kısmi arama için % işareti kullanın, örn: %Ali%): ");
        String query = scanner.nextLine().trim();

        ContactsOperations.searchBySelectedField(fieldName, query); 
    }

    private static void multiFieldSearch(Scanner scanner) {
        printBox("Özel Sorgu: Birden fazla alana göre arama yapın (WHERE koşulları). '0' ile sonlandırın.");
        printBox("Format: ALAN|OPERATÖR|DEĞER (örn: first_name|LIKE|%Ahmet%)");
        
        StringBuilder whereClause = new StringBuilder();
        int count = 0;
        
        while (true) {
            System.out.println("\n" + (count + 1) + ". Filtre:");
            System.out.print("Sorgu Parçası (veya '0' ile bitir): ");
            String line = scanner.nextLine().trim();

            if (line.equals("0")) {
                break;
            }
            
            if (line.split("\\|").length < 3) {
                 printBox("Hatalı format! Format ALAN|OPERATÖR|DEĞER olmalı.");
                 continue;
            }

            if (count > 0) {
                System.out.print("Mantıksal Bağlaç (AND/OR): ");
                String connector = scanner.nextLine().trim().toUpperCase();
                if (connector.equals("AND") || connector.equals("OR")) {
                    whereClause.append(" ").append(connector).append(" ");
                } else {
                    printBox("Geçersiz bağlaç. AND/OR kullanın. Filtre iptal edildi.");
                    continue;
                }
            }
            
            whereClause.append(line);
            count++;
        }

        if (count > 0) {
            System.out.println("\nÇalıştırılan Sorgu: WHERE " + whereClause.toString());
            ContactsOperations.executeUserDefinedSearch(whereClause.toString()); 
        } else {
            printBox("Hiçbir arama koşulu girilmedi.");
        }
    }
    
    // ============================================================
    // UPDATE CONTACT FLOW (BENZERSİZLİK KONTROLÜ EKLENDİ)
    // ============================================================

    private static void updateContactFlow(Scanner scanner) {
        int id = readInt(scanner, "Contact ID: ");

        // ----------- PHONE UPDATE -----------
        System.out.print("New phone (05xx xxx xx xx format, '0' to assign NULL, leave empty to skip): ");
        String phoneInput = scanner.nextLine().trim();
        String phone = null;
        
        if (phoneInput.equals("0")) {
            phone = null;
        } else if (!phoneInput.isEmpty()) {
            String cleanPhone = phoneInput.replace(" ", "");
            if (PHONE_PATTERN.matcher(cleanPhone).matches()) {
                
                // BENZERSİZLİK KONTROLÜ
                if (ContactsOperations.phoneExistsExcept(cleanPhone, id)) {
                     printBox("Bu telefon numarası başka bir kullanıcıda kayıtlı!");
                     return; 
                }
                phone = cleanPhone;
                
            } else {
                printBox("Invalid phone format! Digits only, 10–15 length (after removing spaces).");
                return;
            }
        }


        // ----------- EMAIL UPDATE -----------
        System.out.print("New email ('0' to assign NULL, leave empty to skip): ");
        String emailInput = scanner.nextLine().trim();
        String email = null;
        
        if (emailInput.equals("0")) {
            email = null;
        } else if (!emailInput.isEmpty()) {
            if (!EMAIL_PATTERN.matcher(emailInput).matches()) {
                printBox("Invalid email format!");
                return;
            }
            
            // BENZERSİZLİK KONTROLÜ
            if (ContactsOperations.emailExistsExcept(emailInput, id)) {
                 printBox("Bu email adresi başka bir kullanıcıda kayıtlı!");
                 return; 
            }
            email = emailInput;
        }

        ContactsOperations.updateContact(id, phone, email);
    }

    // ============================================================
    // ADD CONTACT FLOW — VALIDATION INCLUDED
    // ============================================================

    private static void addContactFlow(Scanner sc) {

        // --------- NAME (Required) ----------
        String first = readName(sc, "First Name (Required): ");
        String last = readName(sc, "Last Name (Required): ");

        // --------- MIDDLE NAME (Optional - '0' to null) ----------
        String mid = readOptionalNameOrZero(sc, 
            "Middle Name (Optional, letters only, type '0' for null): ", 
            NAME_PATTERN, 
            "Invalid Middle Name! Letters only. Type '0' for null."
        );

        // --------- NICKNAME (Required and Unique) ----------
        String nick = readRequiredNickname(sc, "Nickname (Required, letters/numbers/./_, and Unique): "); // Başlık güncellendi

        // --------- PHONE1 (Required and Unique) ----------
        String p1 = readUniquePhoneFormat(sc, "Phone1 (Required, Unique, Format: 05xx xxx xx xx): ");

        // --------- PHONE2 (Optional - '0' to null) ----------
        String p2 = readOptionalPhoneFormatOrZero(sc, "Phone2 (Optional, Format: 05xx xxx xx xx, type '0' for null): ");


        // --------- EMAIL (Required and Unique) ----------
        String email = readUniqueEmail(sc, "Email (Required and Unique): ");

        // --------- LINKEDIN (Required and Unique) ----------
        String link = readUniqueLinkedIn(sc, "LinkedIn URL (Required and Unique, e.g. https://linkedin.com/in/username): ");

        // --------- JOB (Required) ----------
        String job = readName(sc, "Job Title (Required): ");

        // --------- BIRTH DATE (Optional, not future, leap year check is implicit) ----------
        LocalDate birthDate = readBirthDate(sc);
        
        Contact c = new Contact(first, mid, last, nick, p1, p2, email, link, job, birthDate);
        ContactsOperations.addContact(c);
    }

    // ============================================================
    // ADD USER FLOW (Şifre Hashleme UsersOperations'da)
    // ============================================================

    private static String readRequiredRole(Scanner sc) {
        List<String> validRoles = Arrays.asList("Tester", "Junior Developer", "Senior Developer", "Manager");
        String input;
        while (true) {
            System.out.print("Role (Tester, Junior Developer, Senior Developer, Manager): ");
            input = sc.nextLine().trim();

            if (input.isEmpty()) {
                printBox("Role is required.");
                continue;
            }

            // HATA ÇÖZÜMÜ: input'un değerini lambdadan önce yerel, final bir değişkene kopyalayın
            final String currentInput = input; 
            
            // Case-insensitive kontrol
            if (validRoles.stream().anyMatch(r -> r.equalsIgnoreCase(currentInput))) {
                // Eşleşen rolü, veritabanına kaydedilecek formatta döndür
                return validRoles.stream().filter(r -> r.equalsIgnoreCase(currentInput)).findFirst().get();
            }
            
            printBox("Invalid Role! Must be one of: Tester, Junior Developer, Senior Developer, Manager.");
        }
    }

    private static String readRequiredTitle(Scanner sc) {
        String input;
        while (true) {
            System.out.print("Title: ");
            input = sc.nextLine().trim();
            
            if (input.isEmpty()) {
                printBox("Title is required.");
                continue;
            }
            if (NAME_PATTERN.matcher(input).matches()) break;
            printBox("Invalid Title! Must contain letters only.");
        }
        return input;
    }
    
    private static void addUserFlow(Scanner sc) {
        
        // --- USERNAME BENZERSİZLİK KONTROLÜ ---
        String u;
        while (true) {
            System.out.print("Username: ");
            u = sc.nextLine().trim();
            
            if (u.isEmpty()) {
                printBox("Username is required.");
                continue;
            }
            
            // USERNAME BENZERSİZLİK KONTROLÜ
            if (UsersOperations.usernameExists(u)) {
                printBox("This username already exists in the system. Please choose a unique one.");
                continue;
            }
            break; 
        }
        // --- USERNAME BENZERSİZLİK KONTROLÜ BİTTİ ---

        System.out.print("Password: ");
        String p = sc.nextLine();

        String n = readName(sc, "Name: ");
        String s = readName(sc, "Surname: ");

        // YENİ: Rol kısıtlaması
        String r = readRequiredRole(sc); 

        // YENİ: Title kısıtlaması
        String t = readRequiredTitle(sc); 

        User user = new User(u, p, n, s, r, t);
        UsersOperations.addUser(user);
    }

    // ============================================================
    // UPDATE USER FLOW
    // ============================================================

    private static void updateUserFlow(Scanner sc) {
        int id = readInt(sc, "User ID: ");

        // Username ve Password için "leave empty to skip" mesajı eklendi
        System.out.print("New Username (leave empty to skip): ");
        String u = sc.nextLine();
        if (u.isEmpty()) u = null;

        System.out.print("New Password (leave empty to skip): ");
        String p = sc.nextLine();
        if (p.isEmpty()) p = null;

        String n = readOptionalName(sc, "New Name (leave empty to skip): ");
        String s = readOptionalName(sc, "New Surname (leave empty to skip): ");

        System.out.print("New Role (leave empty to skip): ");
        String r = sc.nextLine();
        if (r.isEmpty()) r = null;

        String t = readOptionalName(sc, "New Title (leave empty to skip): ");

        User user = new User(id, u, p, n, s, r, t);
        UsersOperations.updateUser(user);
    }

    /**
     * Reads an optional name/string that must match NAME_PATTERN if entered.
     */
    private static String readOptionalName(Scanner sc, String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                return null; // Optional, so return null if empty
            }
            if (NAME_PATTERN.matcher(input).matches()) {
                return input;
            }
            printBox("Invalid input! Must contain letters only. Leave empty to skip.");
        }
    }
}