package Securefinal;

import java.io.*;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final String USER_FILE = "data/users.txt";
    private static final String PASSENGER_FILE = "data/passengers.txt";
    private static final String FLIGHT_CREW_FILE = "data/flight_crew.txt";
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final Map<String, Integer> loginAttempts = new HashMap<>();

    public static void main(String[] args) {
        MyLogger.logInfo("SkyPort application started.");
        loadLoginAttemptsFromLog(); // Load login attempts from log file
        initializeDefaultAgent();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\nWelcome to SkyPort!");
                MyLogger.logInfo("Main menu displayed to the user.");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Enter your choice: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    MyLogger.logWarning("User entered invalid input in main menu.");
                    scanner.nextLine();
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                MyLogger.logInfo("User selected menu option: " + choice);

                switch (choice) {
                    case 1:
                        MyLogger.logInfo("User selected Login.");
                        handleLogin(scanner);
                        break;
                    case 2:
                        MyLogger.logInfo("User selected Exit. Application shutting down.");
                        System.out.println("Exiting application. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        MyLogger.logWarning("User entered an invalid menu choice.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                MyLogger.logError("InputMismatchException encountered in main menu.", e);
                scanner.nextLine(); // Clear invalid input
            }
        }
    }


    private static void initializeDefaultAgent() {
        File userFile = new File(USER_FILE);
        if (!userFile.exists()) {
            try {
                userFile.getParentFile().mkdirs();
                BufferedWriter writer = new BufferedWriter(new FileWriter(userFile));
                String defaultUsername = "agent_default";
                String defaultPassword = hashPassword("Default@123");
                writer.write(defaultUsername + "|" + defaultPassword);
                writer.newLine();
                writer.close();
                System.out.println("Default agent created. Username: agent_default, Password: Default@123");

                // Reset login attempts for default agent
                resetLoginAttempts(defaultUsername);
            } catch (IOException e) {
                System.out.println("Error initializing default agent: " + e.getMessage());
            }
        }
    }



    private static void handleLogin(Scanner scanner) {
        int attempts = 0; // Track login attempts for this session

        while (attempts < MAX_LOGIN_ATTEMPTS) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            MyLogger.logInfo("User entered username: " + username);

            // Validate username input
            if (username.length() < 3 || username.length() > 20) {
                System.out.println("Invalid username length. Must be between 3 and 20 characters.");
                MyLogger.logWarning("Invalid username length entered: " + username);
                continue;
            }

            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            MyLogger.logInfo("User entered a password (masked for security).");

            // Validate password input
            if (password.length() < 8 || password.length() > 20) {
                System.out.println("Invalid password length. Must be between 8 and 20 characters.");
                MyLogger.logWarning("Invalid password length entered for username: " + username);
                continue;
            }

            // Hash the entered password
            String hashedPassword = hashPassword(password);
            MyLogger.logInfo("Password hashed successfully.");

            // Check if account is locked
            if (loginAttempts.getOrDefault(username, 0) >= MAX_LOGIN_ATTEMPTS) {
                System.out.println("Account locked due to too many failed login attempts.");
                MyLogger.logWarning("Login attempt blocked for locked account: " + username);
                return; // Exit login handling
            }

            // Authenticate user credentials
            if (authenticateUser(username, hashedPassword)) {
                System.out.println("Login successful! Welcome, " + username + "!");
                MyLogger.logInfo("User logged in successfully: " + username);
                resetLoginAttempts(username); // Reset login attempts

                // Redirect to role-specific page
                if (username.equals("agent_default")) {
                    agentPage(scanner);
                } else if (username.startsWith("crew")) {
                    flightCrewPage(username);
                } else {
                    passengerPage(scanner, username);
                }
                return;
            } else {
                // Increment failed login attempts
                incrementLoginAttempts(username);
                attempts++;

                int remainingAttempts = MAX_LOGIN_ATTEMPTS - loginAttempts.get(username);
                if (remainingAttempts <= 0) {
                    System.out.println("Too many failed login attempts. Account is now locked.");
                    MyLogger.logWarning("Account locked due to excessive failed attempts: " + username);
                } else {
                    System.out.println("Invalid credentials. Attempts remaining: " + remainingAttempts);
                    MyLogger.logWarning("Invalid credentials entered. Remaining attempts: " + remainingAttempts);
                }
            }
        }

        System.out.println("Too many failed attempts. Please try again later.");
        MyLogger.logWarning("User exceeded maximum login attempts.");
    }





	private static void passengerPage(Scanner scanner, String username) {
        while (true) {
            System.out.println("\nPassenger Page:");
            System.out.println("1. View Personal Info");
            System.out.println("2. Update Personal Info");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewPassengerInfo(username);
                    break;
                case 2:
                    updatePassengerInfo(scanner, username);
                    break;
                case 3:
                    System.out.println("Logging out...\n");
                    MyLogger.logInfo("User logged out.");

                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

	private static boolean authenticateUser(String username, String hashedPassword) {
	    try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] parts = line.split("\\|");
	            if (parts.length == 2 && parts[0].equals(username)) {
	                if (parts[1].equals(hashedPassword)) {
	                    resetLoginAttempts(username); // Reset on successful login
	                    return true;
	                } else {
	                    incrementLoginAttempts(username);
	                    return false;
	                }
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading user file: " + e.getMessage());
	    }
	    incrementLoginAttempts(username); // Increment for nonexistent users
	    return false;
	}



	private static void agentPage(Scanner scanner) {
	    MyLogger.logInfo("Agent accessed the Agent Page.");
	    while (true) {
	        System.out.println("\nAgent Page:");
	        System.out.println("1. Register Passenger");
	        System.out.println("2. Register Flight Crew");
	        System.out.println("3. View Passenger Info");
	        System.out.println("4. Update Passenger Info");
	        System.out.println("5. Logout");
	        System.out.print("Enter your choice: ");

	        if (!scanner.hasNextInt()) {
	            System.out.println("Invalid input. Please enter a number.");
	            MyLogger.logInfo("Agent entered invalid input in Agent Page menu.");
	            scanner.nextLine();
	            continue;
	        }

	        int choice = scanner.nextInt();
	        scanner.nextLine(); // Consume newline
	        MyLogger.logInfo("Agent selected option: " + choice);

	        switch (choice) {
	            case 1:
	                MyLogger.logInfo("Agent chose to register a passenger.");
	                registerPassenger(scanner);
	                break;
	            case 2:
	                MyLogger.logInfo("Agent chose to register a flight crew.");
	                registerFlightCrew(scanner);
	                break;
	            case 3:
	                System.out.print("Enter passenger username to view: ");
	                String viewUsername = scanner.nextLine();
	                MyLogger.logInfo("Agent chose to view passenger info for: " + viewUsername);
	                viewPassengerInfo(viewUsername);
	                break;
	            case 4:
	                System.out.print("Enter passenger username to update: ");
	                String updateUsername = scanner.nextLine();
	                MyLogger.logInfo("Agent chose to update passenger info for: " + updateUsername);
	                updatePassengerInfo(scanner, updateUsername);
	                break;
	            case 5:
	                System.out.println("Logging out...");
	                MyLogger.logInfo("Agent logged out.");
	                return;
	            default:
	                System.out.println("Invalid choice. Please try again.");
	                MyLogger.logInfo("Agent entered an invalid option in Agent Page menu.");
	        }
	    }
	}


    private static void viewPassengerInfo(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PASSENGER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(username)) {
                    System.out.println("Passenger Info:");
                    System.out.println("Name: " + parts[1]);
                    System.out.println("Passport Number: " + parts[2]);
                    System.out.println("Contact Number: " + parts[3]);
                    System.out.println("Flight Number: " + parts[4]);
                    return;
                }
            }
            System.out.println("Passenger not found.");
        } catch (IOException e) {
            System.out.println("Error reading passenger file: " + e.getMessage());
        }
    }

    private static void updatePassengerInfo(Scanner scanner, String username) {
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        
        if (!newName.matches("[a-zA-Z\\s]+")) {
            System.out.println("Invalid name. Only alphabetic characters are allowed.");
            return;
        }

        System.out.print("Enter new passport number: ");
        String newPassport = scanner.nextLine();
        if (!newPassport.matches("[A-Z0-9]{6,10}")) {
            System.out.println("Invalid passport number. It must be 6-10 alphanumeric characters.");
            return;
        }

        System.out.print("Enter new contact number: ");
        String newContact = scanner.nextLine();
        if (!newContact.matches("\\d{10}")) {
            System.out.println("Invalid contact number. It must be 10 digits.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(PASSENGER_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(PASSENGER_FILE + ".tmp"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(username)) {
                    writer.write(username + "|" + newName + "|" + newPassport + "|" + newContact + "|" + parts[4]);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating passenger file: " + e.getMessage());
        }

        new File(PASSENGER_FILE).delete();
        new File(PASSENGER_FILE + ".tmp").renameTo(new File(PASSENGER_FILE));
        System.out.println("Passenger info updated successfully!");
    }

    private static void flightCrewPage(String username) {
        System.out.println("\nFlight Crew Page:");
        viewFlightCrewInfo(username);
    }

    private static void viewFlightCrewInfo(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FLIGHT_CREW_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                // Ensure the line has all required fields
                if (parts.length >= 6 && parts[0].equals(username)) {
                    System.out.println("Flight Crew Info:");
                    System.out.println("Name: " + parts[1]);
                    System.out.println("Contact Number: " + parts[2]);
                    System.out.println("Assigned Flight Number: " + parts[3]);
                    System.out.println("Departure Time: " + parts[4]);
                    System.out.println("Assigned Gate: " + parts[5]);
                    return;
                }

            }
            System.out.println("Flight crew member not found.");
        } catch (IOException e) {
            System.out.println("Error reading flight crew file: " + e.getMessage());
        }
    }

    
    

    private static void registerPassenger(Scanner scanner) {
        System.out.print("Enter passenger username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (!validateStrongPassword(password)) {
            System.out.println("Password does not meet the strong password requirements.");
            return;
        }
        
        System.out.print("Enter passenger member full name (letters only): ");
        String name = scanner.nextLine().trim();
        if (!name.matches("[a-zA-Z\\s]+")) {
            System.out.println("Invalid name. Only alphabetic characters are allowed.");
            MyLogger.logInfo("Invalid passenger member name provided: " + name);
            return;
        }
        
        System.out.print("Enter passport number: ");
        String passportNumber = scanner.nextLine().trim();
        if (!passportNumber.matches("[A-Z0-9]{6,10}")) {
            System.out.println("Invalid passport number. It must be 6-10 alphanumeric characters.");
            MyLogger.logInfo("Invalid flight crew member number provided: " + name);
            return;
        }
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine().trim();
        if (!contactNumber.matches("\\d{10}")) {
            System.out.println("Invalid contact number. It must be 10 digits.");
            MyLogger.logInfo("Invalid contact number entered");
            return;
        }
        System.out.print("Enter flight number: ");
        String flightNumber = scanner.nextLine().trim();
        if (!flightNumber.matches("FL\\d{3,4}")) {
            System.out.println("Invalid flight number. It must follow the format FL1234.");
            MyLogger.logInfo("Invalid flight number. It must follow the format FL1234");
            return;
        }
        System.out.print("Enter departure time: ");
        String departureTime = scanner.nextLine().trim();
        System.out.print("Enter assigned gate: ");
        String assignedGate = scanner.nextLine().trim();

        try (BufferedWriter userWriter = new BufferedWriter(new FileWriter(USER_FILE, true));
             BufferedWriter passengerWriter = new BufferedWriter(new FileWriter(PASSENGER_FILE, true))) {

            userWriter.write(username + "|" + hashPassword(password));
            userWriter.newLine();

            passengerWriter.write(username + "|" + name + "|" + passportNumber + "|" + contactNumber + "|"
                    + flightNumber + "|" + departureTime + "|" + assignedGate);
            passengerWriter.newLine();

            System.out.println("Passenger registered successfully!");
            MyLogger.logInfo("Passenger registered successfully: " + username);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
            MyLogger.logError("Error during passenger registration.", e);
        }
    }

    private static void registerFlightCrew(Scanner scanner) {
        System.out.print("Enter flight crew username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (!validateStrongPassword(password)) {
            System.out.println("Password does not meet the strong password requirements.");
            return;
        }

        System.out.print("Enter crew member full name (letters only): ");
        String name = scanner.nextLine().trim();
        if (!name.matches("[a-zA-Z\\s]+")) {
            System.out.println("Invalid name. Only alphabetic characters are allowed.");
            return;
        }

        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine().trim();
        if (!contactNumber.matches("\\d{10}")) {
            System.out.println("Invalid contact number. It must be 10 digits.");
            return;
        }

        System.out.print("Enter assigned flight number: ");
        String flightNumber = scanner.nextLine().trim();
        if (!flightNumber.matches("FL\\d{3,4}")) {
            System.out.println("Invalid flight number. It must follow the format FL1234.");
            return;
        }

        System.out.print("Enter departure time: ");
        String departureTime = scanner.nextLine().trim();

        System.out.print("Enter assigned gate: ");
        String assignedGate = scanner.nextLine().trim();

        try (BufferedWriter userWriter = new BufferedWriter(new FileWriter(USER_FILE, true));
             BufferedWriter crewWriter = new BufferedWriter(new FileWriter(FLIGHT_CREW_FILE, true))) {

            // Write to the users file for authentication
            userWriter.write(username + "|" + hashPassword(password));
            userWriter.newLine();

            // Write to the flight crew file for additional details
            crewWriter.write(username + "|" + name + "|" + contactNumber + "|" + flightNumber + "|" + departureTime + "|" + assignedGate);
            crewWriter.newLine();

            System.out.println("Flight crew registered successfully!");
            MyLogger.logInfo("Flight crew registered successfully: " + username);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
            MyLogger.logError("Error during flight crew registration.", e);
        }
    }



    private static void incrementLoginAttempts(String username) {
        int currentAttempts = loginAttempts.getOrDefault(username, 0);
        loginAttempts.put(username, currentAttempts + 1);
        MyLogger.logWarning("Failed login attempt " + (currentAttempts + 1) + " for user: " + username);
    }

    private static void resetLoginAttempts(String username) {
        loginAttempts.put(username, 0); // Reset to 0
        MyLogger.logInfo("Reset login attempts for user: " + username);
    }



    public static boolean validateStrongPassword(String password) {
        boolean isValid = password.length() >= 8
                && password.length() <= 20
                && password.matches(".*[A-Z].*") // At least one uppercase letter
                && password.matches(".*[a-z].*") // At least one lowercase letter
                && password.matches(".*\\d.*") // At least one digit
                && password.matches(".*[!@#$%^&*(),.?\":{}|<>].*"); // At least one special character

        if (!isValid) {
            MyLogger.logWarning("Password validation failed.");
        } else {
            MyLogger.logInfo("Password successfully validated.");
        }

        return isValid;
    }



    static String hashPassword(String password) {
        String salt = "random_salt";
        password += salt;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            MyLogger.logInfo("Password hashed successfully.");
            return hexString.toString();
        } catch (Exception e) {
            MyLogger.logError("Error hashing password.", e);
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private static void loadLoginAttemptsFromLog() {
        File logFile = new File("logfinal.log");
        if (logFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("LOGIN_ATTEMPT|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length == 3) {
                            String username = parts[1];
                            int attempts = Integer.parseInt(parts[2]);
                            loginAttempts.put(username, attempts);
                        }
                    } else if (line.contains("RESET_ATTEMPTS|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length == 2) {
                            String username = parts[1];
                            loginAttempts.put(username, 0);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading login attempts log: " + e.getMessage());
            }
        }

        // Ensure default agent starts with 0 attempts
        resetLoginAttempts("agent_default");
    }


}
