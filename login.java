package Securefinal;

import java.io.*;
import java.util.*;

public class login {
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static Map<String, Integer> loginAttempts = new HashMap<>();
    private static final String FLIGHT_CREW_FILE = "flight_crew.txt";
    private static final String USER_FILE = "users.txt";
    private static final String PASSENGER_FILE = "passengers.txt";

    private static void processLogin(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (loginAttempts.getOrDefault(username, 0) >= MAX_LOGIN_ATTEMPTS) {
            System.out.println("Account locked due to too many failed login attempts.");
            MyLogger.writeToLog("Account locked for user: " + username);
            return;
        }

        if (username.startsWith("crew")) {
            handleCrewLogin(username, password);
        } else if (username.startsWith("agent")) {
            handleCrewLogin(username, password);
        } else if (username.startsWith("passenger")) {
            handleCrewLogin(username, password);
        } else {
            System.out.println("Invalid user type. Username must start with 'crew', 'agent', or 'passenger'.");
        }
    }

    private static void handleCrewLogin(String username, String password) {
        if (!existsInFile(username, FLIGHT_CREW_FILE)) {
            System.out.println("Flight crew member not found.");
            return;
        }
        FlightCrew crew = FlightCrew.loadFromFile(username, FLIGHT_CREW_FILE);
        if (crew != null && crew.login(FLIGHT_CREW_FILE)) {
            System.out.println("Login successful! Welcome, Crew Member.");
            crew.viewAssignedFlight();
            loginAttempts.put(username, 0);
            MyLogger.writeToLog("Login successful for user: " + username);
        } else {
            System.out.println("Invalid credentials.");
            incrementLoginAttempts(username);
        }
    }

    // Similar methods for `handleAgentLogin` and `handlePassengerLogin`

    private static void incrementLoginAttempts(String username) {
        loginAttempts.put(username, loginAttempts.getOrDefault(username, 0) + 1);
    }

    private static boolean existsInFile(String username, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        processLogin(scanner);
    }
}
