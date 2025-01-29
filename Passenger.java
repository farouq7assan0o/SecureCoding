package Securefinal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Passenger extends User {
    private String name;
    private String passportNumber;
    private String contactNumber;
    private String flightNumber;
    private String departureTime;
    private String assignedGate;

    public Passenger(String username, String password, String name, String passportNumber, String contactNumber,
                     String flightNumber, String departureTime, String assignedGate) {
        super(username, password); // Call the User constructor
        this.name = name;
        this.passportNumber = passportNumber;
        this.contactNumber = contactNumber;
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.assignedGate = assignedGate;
    }

    // View personal information
    public void viewPersonalInfo() {
        System.out.println("Username: " + username);
        System.out.println("Name: " + name);
        System.out.println("Passport Number: " + passportNumber);
        System.out.println("Contact Number: " + contactNumber);
        System.out.println("Flight Number: " + flightNumber);
        System.out.println("Departure Time: " + departureTime);
        System.out.println("Assigned Gate: " + assignedGate);
    }
    
    public String getName() {
        return this.name; // Ensure 'name' is a field in Passenger class
    }

    public static boolean existsInFile(String username, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length > 0 && parts[0].equals(username)) {
                    return true; // Username already exists
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return false;
    }


    // Update personal information
    public void updatePersonalInfo(String filename, String[] updatedDetails) {
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
            System.out.println("Personal information updated successfully!");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Static method to load a passenger from a file
    public static Passenger loadFromFile(String username, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(username)) {
                    return new Passenger(
                        parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]
                    );
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading passenger file: " + e.getMessage());
        }
        return null; // Return null if no matching passenger is found
    }

	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFlightNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDepartureTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAssignedGate() {
		// TODO Auto-generated method stub
		return null;
	}
}
