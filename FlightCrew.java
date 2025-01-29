package Securefinal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FlightCrew extends User {
    private String flightNumber;
    private String departureTime;
    private String assignedGate;

    public FlightCrew(String username, String password, String flightNumber, String departureTime, String assignedGate) {
        super(username, password); // Call the User constructor
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.assignedGate = assignedGate;
    }

    // Method to view assigned flight details
    public void viewAssignedFlight() {
        System.out.println("Flight Number: " + flightNumber);
        System.out.println("Departure Time: " + departureTime);
        System.out.println("Assigned Gate: " + assignedGate);
    }

    // Static method to read flight crew details from a file
    public static FlightCrew loadFromFile(String username, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5 && parts[0].equals(username)) { // Ensure correct format and matching username
                    String fileUsername = parts[0];
                    String filePassword = parts[1];
                    String flightNumber = parts[2];
                    String departureTime = parts[3];
                    String assignedGate = parts[4];
                    return new FlightCrew(fileUsername, filePassword, flightNumber, departureTime, assignedGate);
                }
            }
        } catch (IOException e) {
            MyLogger.logInfo("Error reading flight crew file: " + e.getMessage());
        }
        return null; // Return null if no matching crew found
    }
}
