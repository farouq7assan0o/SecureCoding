package Securefinal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CheckInAgent extends User {

    public CheckInAgent(String username, String password) {
        super(username, password); // Call the User constructor
    }

    // Register a new passenger
    public void registerPassenger(String filename, String[] passengerDetails) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[0].equals(passengerDetails[0])) {
                    System.out.println("Passenger already exists: " + passengerDetails[0]);
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            StringBuilder sb = new StringBuilder();
            for (String detail : passengerDetails) {
                sb.append(detail).append("|");
            }
            bw.write(sb.toString());
            bw.newLine();
            System.out.println("Passenger registered successfully: " + passengerDetails[0]);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // View a passenger's details
    public void viewPassenger(String filename, String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(username)) {
                    System.out.println("Passenger Details:");
                    for (int i = 0; i < parts.length; i++) {
                        System.out.println("Field " + i + ": " + parts[i]);
                    }
                    return;
                }
            }
            System.out.println("Passenger not found: " + username);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Update a passenger's details
    public void updatePassenger(String filename, String username, String[] updatedDetails) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(username)) {
                    StringBuilder sb = new StringBuilder();
                    for (String detail : updatedDetails) {
                        sb.append(detail).append("|");
                    }
                    lines.add(sb.toString());
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        if (!found) {
            System.out.println("Passenger not found: " + username);
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Passenger updated successfully: " + username);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}
