package Securefinal;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class PassPolicy {

    @Before
    public void setup() {
        System.out.println("Setting up for password policy tests...");
    }

    @After
    public void tearDown() {
        System.out.println("Tearing down after password policy tests...");
    }

    @Test
    public void testValidPassword() {
        System.out.println("Testing: Valid password");
        boolean actual = Main.validateStrongPassword("Valid@123");
        assertTrue("Password meeting all criteria should be valid", actual);
    }

    @Test
    public void testPasswordTooShort() {
        System.out.println("Testing: Password too short");
        boolean actual = Main.validateStrongPassword("Short1!");
        assertFalse("Password less than 8 characters should be invalid", actual);
    }

    @Test
    public void testPasswordTooLong() {
        System.out.println("Testing: Password too long");
        boolean actual = Main.validateStrongPassword("ThisPasswordIsWayTooLong123!");
        assertFalse("Password more than 20 characters should be invalid", actual);
    }

    @Test
    public void testPasswordMissingUppercase() {
        System.out.println("Testing: Password missing uppercase letter");
        boolean actual = Main.validateStrongPassword("valid@123");
        assertFalse("Password without an uppercase letter should be invalid", actual);
    }

    @Test
    public void testPasswordMissingLowercase() {
        System.out.println("Testing: Password missing lowercase letter");
        boolean actual = Main.validateStrongPassword("VALID@123");
        assertFalse("Password without a lowercase letter should be invalid", actual);
    }

    @Test
    public void testPasswordMissingDigit() {
        System.out.println("Testing: Password missing digit");
        boolean actual = Main.validateStrongPassword("Valid@Pass");
        assertFalse("Password without a digit should be invalid", actual);
    }

    @Test
    public void testPasswordMissingSpecialCharacter() {
        System.out.println("Testing: Password missing special character");
        boolean actual = Main.validateStrongPassword("Valid1234");
        assertFalse("Password without a special character should be invalid", actual);
    }

    @Test
    public void testEmptyPassword() {
        System.out.println("Testing: Empty password");
        boolean actual = Main.validateStrongPassword("");
        assertFalse("Empty password should be invalid", actual);
    }

    @Test
    public void testPasswordWithSpacesOnly() {
        System.out.println("Testing: Password with spaces only");
        boolean actual = Main.validateStrongPassword("        ");
        assertFalse("Password with only spaces should be invalid", actual);
    }

    @Test
    public void testPasswordHashingConsistency() {
        System.out.println("Testing: Password hashing consistency");
        String password = "Consistent@123";
        String hash1 = Main.hashPassword(password);
        String hash2 = Main.hashPassword(password);
        assertEquals("Hashing the same password should produce the same result", hash1, hash2);
    }

    @Test
    public void testPasswordHashingIrreversibility() {
        System.out.println("Testing: Password hashing irreversibility");
        String password = "Irreversible@123";
        String hash = Main.hashPassword(password);
        assertNotEquals("Hashed password should not match the original password", password, hash);
    }

    @Test
    public void testPasswordHashingUniqueness() {
        System.out.println("Testing: Password hashing uniqueness");
        String password1 = "Unique@123";
        String password2 = "Different@123";
        String hash1 = Main.hashPassword(password1);
        String hash2 = Main.hashPassword(password2);
        assertNotEquals("Hashing different passwords should produce different results", hash1, hash2);
    }
}
