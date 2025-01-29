package Securefinal;

import java.io.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class LoginTest {

    private static final String USER_FILE = System.getProperty("user.dir") + "/data/users.txt";

    @Before
    public void setup() throws Exception {
        // Ensure the directory exists
        ensureDirectoryExists(USER_FILE);

        // Delete the user file to ensure a clean slate
        File userFile = new File(USER_FILE);
        if (userFile.exists()) {
            userFile.delete();
        }

        // Set up a valid user for testing
        User testUser = new User("testUser1", "Test@123");
        testUser.register(USER_FILE);
    }

    @After
    public void tearDown() throws Exception {
        // Clean up user file after tests
        File userFile = new File(USER_FILE);
        if (userFile.exists()) {
            userFile.delete();
        }
    }

    @Test
    public void testCorrectUsernameAndPassword() {
        System.out.println("Testing: Login with correct username and password");
        User user = new User("testUser1", "Test@123");
        boolean expected = true;
        boolean actual = user.login(USER_FILE);
        assertEquals("Login with correct username and password should succeed", expected, actual);
    }

    @Test
    public void testIncorrectUsernameAndCorrectPassword() {
        System.out.println("Testing: Login with incorrect username and correct password");
        User user = new User("wrongUser", "Test@123");
        boolean expected = false;
        boolean actual = user.login(USER_FILE);
        assertEquals("Login with incorrect username should fail", expected, actual);
    }

    @Test
    public void testCorrectUsernameAndIncorrectPassword() {
        System.out.println("Testing: Login with correct username and incorrect password");
        User user = new User("testUser1", "WrongPass123");
        boolean expected = false;
        boolean actual = user.login(USER_FILE);
        assertEquals("Login with correct username but incorrect password should fail", expected, actual);
    }

    @Test
    public void testIncorrectUsernameAndPassword() {
        System.out.println("Testing: Login with incorrect username and password");
        User user = new User("wrongUser", "WrongPass123");
        boolean expected = false;
        boolean actual = user.login(USER_FILE);
        assertEquals("Login with incorrect username and password should fail", expected, actual);
    }

    @Test
    public void testUsernameTooShort() {
        System.out.println("Testing: Username too short");
        try {
            User user = new User("us", "ValidPass123"); // Username too short
            fail("Expected IllegalArgumentException for username too short");
        } catch (IllegalArgumentException e) {
            String expectedMessage = "Invalid username or password length.";
            assertEquals("Username too short should throw exception", expectedMessage, e.getMessage());
        }
    }

    @Test
    public void testUsernameTooLong() {
        System.out.println("Testing: Username too long");
        try {
            User user = new User("thisUsernameIsWayTooLongToBeAccepted", "ValidPass123"); // Username too long
            fail("Expected IllegalArgumentException for username too long");
        } catch (IllegalArgumentException e) {
            String expectedMessage = "Invalid username or password length.";
            assertEquals("Username too long should throw exception", expectedMessage, e.getMessage());
        }
    }

    @Test
    public void testPasswordTooShort() {
        System.out.println("Testing: Password too short");
        try {
            User user = new User("validUser", "short"); // Password too short
            fail("Expected IllegalArgumentException for password too short");
        } catch (IllegalArgumentException e) {
            String expectedMessage = "Invalid username or password length.";
            assertEquals("Password too short should throw exception", expectedMessage, e.getMessage());
        }
    }

    @Test
    public void testPasswordTooLong() {
        System.out.println("Testing: Password too long");
        try {
            User user = new User("validUser", "thisPasswordIsWayTooLongToBeAcceptedAndShouldFail"); // Password too long
            fail("Expected IllegalArgumentException for password too long");
        } catch (IllegalArgumentException e) {
            String expectedMessage = "Invalid username or password length.";
            assertEquals("Password too long should throw exception", expectedMessage, e.getMessage());
        }
    }

    @Test
    public void testLoginAttemptLimit() {
        System.out.println("Testing: Login attempt limit");
        User user = new User("testUser1", "WrongPass123");

        boolean expected;
        boolean actual;

        for (int i = 1; i <= 3; i++) {
            expected = false; // All attempts should fail
            actual = user.login(USER_FILE);
            assertEquals("Login attempt " + i + " should fail due to incorrect password", expected, actual);
        }

        // Account should be locked after 3 failed attempts
        expected = false;
        actual = user.login(USER_FILE);
        assertEquals("Login after 3 failed attempts should be locked", expected, actual);
    }

    private void ensureDirectoryExists(String filePath) {
        File file = new File(filePath);
        File directory = file.getParentFile();
        if (!directory.exists()) {
            directory.mkdirs(); // Create directories if they don't exist
        }
    }
}
