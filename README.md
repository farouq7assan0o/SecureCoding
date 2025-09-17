# ✈️ Secure Airline Check-in & Crew Management System

## 📌 Overview
This project is a **Java-based secure airline management system** that demonstrates **secure coding practices** through a file-based check-in and flight crew management platform.

It simulates an airline environment where:
- **Passengers** can register, log in, and update/view their information.  
- **Flight Crew** can log in and view their assigned flight details.  
- **Check-in Agents** can register and update passenger details.  
- **Admins/Agents** can register new users (passengers & crew).  

The system emphasizes **security-first design**, including password hashing, login attempt limits, logging, and input validation.  

### --

## 🛠️ Features

### 🔐 Authentication & Security
- Username/password login with **SHA-256 hashing & salt**  
- Lockout after **3 failed attempts** (brute force protection)  
- Password policy enforcement (**uppercase, lowercase, digit, special character, 8–20 chars**)  
- Audit logging (`logfinal.log`) for login attempts and errors  

### 👤 Passenger Management
- Register new passengers  
- View and update passenger information  
- Load passengers from data files  

### 👨‍✈️ Flight Crew Management
- Assign flight, departure time, and gate  
- View assigned flight details  
- File-based persistence  

### 🛎️ Check-in Agent
- Register and update passenger/crew details  
- Validate inputs (names, contact numbers, flight numbers)  

### 📑 Logging & Error Handling
- Centralized logger (`MyLogger`) with severity levels  
- Graceful exception handling for file operations  

### 🧪 Testing
- ✅ Unit testing with JUnit (`LoginTest`, `PassPolicy`)  
- ✅ Integration testing (login + file operations)  
- ✅ User Acceptance Testing (UAT) with role-based flows  
- ✅ Smoke testing for build verification  
- ✅ Security testing (fuzzing login attempts, regression testing)  

### --

## 🔐 Security Principles Demonstrated
- **Least Privilege** – users only access role-specific features  
- **Input Validation** – prevents invalid or malicious data  
- **Password Hashing** – SHA-256 with salt  
- **Error Handling** – avoids crashes and logs failures  
- **Threat Modeling** – identifies potential misuse cases (brute force, injection, etc.)  

### --

## 📂 Project Structure
SecureCoding/
├── data/
│ ├── flight_crew.txt # Crew details
│ ├── passengers.txt # Passenger details
│ ├── users.txt # Registered users
│ ├── login_attempts.log # Failed logins
│ └── output.csv # Static code analysis results
│
├── CheckInAgent.java # Passenger registration/update
├── FlightCrew.java # Crew details & flight assignment
├── Passenger.java # Passenger details & update methods
├── User.java # Base user authentication
├── login.java # Login flow handler
├── Main.java # Application entry point
├── MyLogger.java # Centralized logger
├── PassPolicy.java # Password policy tests
├── LoginTest.java # JUnit login tests
└── Sky_System_Secure_Coding_Final.docx # Report & documentation

bash
Copy code

### --

## ⚙️ Installation & Setup
```bash
# Clone this repository
git clone https://github.com/farouq7assan0o/SecureCoding.git
cd SecureCoding

# Compile the Java files
javac SecureCoding/*.java

# Run the application
java SecureCoding.Main
--
🧪 Testing
Unit Testing (JUnit)
Valid login returns true

Wrong password increments login attempts

Account locks after 3 failures

Password policy checks (length, uppercase, lowercase, digit, special char)

Integration Testing
Login + data persistence (users, passengers, crew)

Register and retrieve a passenger/crew

User Acceptance Testing
Passenger logs in, views/updates info

Agent registers a new passenger

Crew views assigned flight

Security Testing
Fuzz Testing – login tested with 1k → 10k → 100k invalid attempts

SAST – PMD issues listed in output.csv

DAST – brute force lockout verified

Regression Testing – reran old tests after updates

--
📊 Documentation & Analysis
Use Case & Misuse Case Diagrams (login, register, update, view)

Data Flow Diagrams (DFD) with threat modeling analysis

Testing Reports – JUnit test cases (login + password policy)

Static Code Analysis – rule violations & fixes in output.csv

📷 Add diagrams in an assets/ folder and update links accordingly.

--
🚀 Technologies Used
Java SE

JUnit (unit testing)

PMD / static analysis tools

OWASP practices (validation, logging, password policies)

--
📄 License
This project is provided for educational and academic purposes under the MIT License.
