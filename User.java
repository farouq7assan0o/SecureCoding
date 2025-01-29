package Securefinal;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class User {
    private static final int MAX_LOGIN_ATTEMPTS = 3; // Limit for failed attempts
    private static final Map<String, Integer> loginAttempts = new HashMap<>();

    protected String username;
    protected String password;

    public User(String username, String password) {
        if (!validateInput(username, 3, 20) || !validateInput(password, 8, 20)) {
            System.out.println("Invalid username or password length for: " + username);
            // Skip invalid user creation (you can return or handle as needed)
            return;
        }
        this.username = username;
        this.password = hashPassword(password);
    }


    public boolean login(String filename) {
        synchronized (User.class) {
            if (isAccountLocked(username)) {
                System.out.println("Account is locked for user: " + username);
                return false; // Deny login immediately if account is locked
            }

            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 2 && parts[0].equals(username)) {
                        if (parts[1].equals(password)) {
                            resetLoginAttempts(username); // Successful login resets attempts
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

            // User not found
            incrementLoginAttempts(username);
            return false;
        }
    }

    private void incrementLoginAttempts(String username) {
        int attempts = loginAttempts.getOrDefault(username, 0) + 1;
        loginAttempts.put(username, attempts);
        System.out.println("Failed login attempt " + attempts + " for user: " + username);
    }

    private void resetLoginAttempts(String username) {
        loginAttempts.put(username, 0); // Reset attempts on successful login
    }

    private boolean isAccountLocked(String username) {
        return loginAttempts.getOrDefault(username, 0) >= MAX_LOGIN_ATTEMPTS;
    }

    public void register(String filename) {
        synchronized (User.class) {
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 2 && parts[0].equals(username)) {
                        throw new IllegalArgumentException("User already exists: " + username);
                    }
                }
            } catch (FileNotFoundException e) {
                // File doesn't exist yet, so it's safe to proceed
            } catch (IOException e) {
                System.out.println("Error reading user file: " + e.getMessage());
            }

            ensureDirectoryExists(filename);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
                bw.write(username + "|" + password);
                bw.newLine();
                System.out.println("User registered successfully.");
            } catch (IOException e) {
                System.out.println("Error writing user file: " + e.getMessage());
            }
        }
    }

    private void ensureDirectoryExists(String filename) {
        File file = new File(filename);
        File directory = file.getParentFile();
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if it doesn't exist
        }
    }

    private String hashPassword(String password) {
        // A simple hashing method for demonstration
        return Integer.toHexString(password.hashCode());
    }

    private boolean validateInput(String input, int minLength, int maxLength) {
        return input != null && input.length() >= minLength && input.length() <= maxLength;
    }
}
