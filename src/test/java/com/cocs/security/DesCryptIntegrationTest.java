package com.cocs.security;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Integration tests to verify DesCrypt works correctly with LoginController patterns.
 * These tests simulate the actual usage patterns in the application.
 */
public class DesCryptIntegrationTest {
    
    @Test
    public void testSignupLinkGeneration() throws Exception {
        // Simulate signup link generation as done in LoginController line 207
        String email = "test@example.com";
        String password = "testPassword123";
        String target = email + "::" + password;
        
        // Encrypt the target string
        String key = DesCrypt.doEncode(target);
        
        // Verify the key is not null and not empty
        assertNotNull("Encrypted key should not be null", key);
        assertFalse("Encrypted key should not be empty", key.isEmpty());
        
        // Verify it can be used in a URL (no problematic characters)
        assertFalse("Key should not contain spaces", key.contains(" "));
        assertFalse("Key should not contain newlines", key.contains("\n"));
        
        // Simulate signup completion as done in LoginController line 229
        String decryptedKey = DesCrypt.doDecode(key);
        String[] keyInfo = decryptedKey.split("::");
        
        assertEquals("Should have 2 parts", 2, keyInfo.length);
        assertEquals("Email should match", email, keyInfo[0]);
        assertEquals("Password should match", password, keyInfo[1]);
    }
    
    @Test
    public void testPasswordResetLinkGeneration() throws Exception {
        // Simulate password reset link generation as done in LoginController line 280
        String email = "test@example.com";
        long timestamp = System.currentTimeMillis();
        String target = email + "::" + timestamp;
        
        // Encrypt the target string
        String key = DesCrypt.doEncode(target);
        
        // Verify the key is not null and not empty
        assertNotNull("Encrypted key should not be null", key);
        assertFalse("Encrypted key should not be empty", key.isEmpty());
        
        // Simulate password reset processing as done in LoginController line 303
        String decryptedKey = DesCrypt.doDecode(key);
        String[] keyInfo = decryptedKey.split("::");
        
        assertEquals("Should have 2 parts", 2, keyInfo.length);
        assertEquals("Email should match", email, keyInfo[0]);
        assertEquals("Timestamp should match", String.valueOf(timestamp), keyInfo[1]);
    }
    
    @Test
    public void testMultipleUsersSimultaneously() throws Exception {
        // Test that multiple users can generate links simultaneously without conflicts
        String[] emails = {
            "user1@example.com",
            "user2@example.com", 
            "user3@example.com"
        };
        String[] passwords = {
            "password1",
            "password2",
            "password3"
        };
        
        String[] encryptedKeys = new String[emails.length];
        
        // Generate encrypted keys for all users
        for (int i = 0; i < emails.length; i++) {
            String target = emails[i] + "::" + passwords[i];
            encryptedKeys[i] = DesCrypt.doEncode(target);
        }
        
        // Verify all keys are unique (due to random IV and salt)
        for (int i = 0; i < encryptedKeys.length; i++) {
            for (int j = i + 1; j < encryptedKeys.length; j++) {
                assertNotEquals("Keys should be unique", encryptedKeys[i], encryptedKeys[j]);
            }
        }
        
        // Verify all keys decrypt correctly
        for (int i = 0; i < emails.length; i++) {
            String decrypted = DesCrypt.doDecode(encryptedKeys[i]);
            String[] parts = decrypted.split("::");
            assertEquals("Email should match", emails[i], parts[0]);
            assertEquals("Password should match", passwords[i], parts[1]);
        }
    }
    
    @Test
    public void testLongEmailAddresses() throws Exception {
        // Test with very long email addresses
        String longEmail = "very.long.email.address.with.many.dots.and.subdomains@very.long.domain.name.with.multiple.subdomains.example.com";
        String password = "password123";
        String target = longEmail + "::" + password;
        
        String encrypted = DesCrypt.doEncode(target);
        String decrypted = DesCrypt.doDecode(encrypted);
        String[] parts = decrypted.split("::");
        
        assertEquals("Long email should be preserved", longEmail, parts[0]);
        assertEquals("Password should be preserved", password, parts[1]);
    }
    
    @Test
    public void testSpecialCharactersInCredentials() throws Exception {
        // Test with special characters that might appear in emails or passwords
        String email = "user+tag@example-domain.co.uk";
        String password = "P@ssw0rd!#$%^&*()";
        String target = email + "::" + password;
        
        String encrypted = DesCrypt.doEncode(target);
        String decrypted = DesCrypt.doDecode(encrypted);
        String[] parts = decrypted.split("::");
        
        assertEquals("Email with special chars should be preserved", email, parts[0]);
        assertEquals("Password with special chars should be preserved", password, parts[1]);
    }
    
    @Test
    public void testTimestampAccuracy() throws Exception {
        // Test that timestamps are preserved accurately for password reset
        long originalTimestamp = System.currentTimeMillis();
        String email = "test@example.com";
        String target = email + "::" + originalTimestamp;
        
        String encrypted = DesCrypt.doEncode(target);
        
        // Add a small delay to ensure we're not just getting the same timestamp
        Thread.sleep(10);
        
        String decrypted = DesCrypt.doDecode(encrypted);
        String[] parts = decrypted.split("::");
        
        long decryptedTimestamp = Long.parseLong(parts[1]);
        assertEquals("Timestamp should be preserved exactly", originalTimestamp, decryptedTimestamp);
    }
    
    @Test
    public void testErrorHandlingForMalformedData() throws Exception {
        // Test that malformed encrypted data is handled gracefully
        String validTarget = "test@example.com::password";
        String validEncrypted = DesCrypt.doEncode(validTarget);
        
        // Test various forms of corruption
        try {
            DesCrypt.doDecode("invalid_base64_data!");
            fail("Should throw exception for invalid base64");
        } catch (Exception e) {
            // Expected
        }
        
        try {
            DesCrypt.doDecode("dGVzdA=="); // Valid base64 but too short
            fail("Should throw exception for too short data");
        } catch (Exception e) {
            // Expected
        }
        
        // Test that valid data still works
        String decrypted = DesCrypt.doDecode(validEncrypted);
        assertEquals("Valid data should still work", validTarget, decrypted);
    }
}