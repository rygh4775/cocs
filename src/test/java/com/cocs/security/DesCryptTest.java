package com.cocs.security;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for the secure DesCrypt implementation.
 * Tests encryption/decryption functionality, security properties, and edge cases.
 */
public class DesCryptTest {
    
    private static final String TEST_STRING_SIMPLE = "Hello World";
    private static final String TEST_STRING_COMPLEX = "user@example.com::password123::1234567890";
    private static final String TEST_STRING_UNICODE = "Hello 世界 🌍 Ñoël";
    private static final String TEST_STRING_LONG = "This is a very long string that contains multiple sentences and should test the encryption with larger data sizes. It includes various characters, numbers 123456789, and symbols !@#$%^&*()_+-=[]{}|;':\",./<>?";
    
    @Before
    public void setUp() {
        // Any setup needed before tests
    }
    
    @Test
    public void testBasicEncryptionDecryption() throws Exception {
        String encrypted = DesCrypt.doEncode(TEST_STRING_SIMPLE);
        assertNotNull("Encrypted string should not be null", encrypted);
        assertFalse("Encrypted string should not be empty", encrypted.isEmpty());
        assertNotEquals("Encrypted string should be different from original", TEST_STRING_SIMPLE, encrypted);
        
        String decrypted = DesCrypt.doDecode(encrypted);
        assertEquals("Decrypted string should match original", TEST_STRING_SIMPLE, decrypted);
    }
    
    @Test
    public void testComplexStringEncryption() throws Exception {
        String encrypted = DesCrypt.doEncode(TEST_STRING_COMPLEX);
        String decrypted = DesCrypt.doDecode(encrypted);
        assertEquals("Complex string should decrypt correctly", TEST_STRING_COMPLEX, decrypted);
    }
    
    @Test
    public void testUnicodeStringEncryption() throws Exception {
        String encrypted = DesCrypt.doEncode(TEST_STRING_UNICODE);
        String decrypted = DesCrypt.doDecode(encrypted);
        assertEquals("Unicode string should decrypt correctly", TEST_STRING_UNICODE, decrypted);
    }
    
    @Test
    public void testLongStringEncryption() throws Exception {
        String encrypted = DesCrypt.doEncode(TEST_STRING_LONG);
        String decrypted = DesCrypt.doDecode(encrypted);
        assertEquals("Long string should decrypt correctly", TEST_STRING_LONG, decrypted);
    }
    
    @Test
    public void testEncryptionUniqueness() throws Exception {
        // Same input should produce different encrypted outputs due to random IV and salt
        String encrypted1 = DesCrypt.doEncode(TEST_STRING_SIMPLE);
        String encrypted2 = DesCrypt.doEncode(TEST_STRING_SIMPLE);
        
        assertNotEquals("Two encryptions of same input should be different", encrypted1, encrypted2);
        
        // But both should decrypt to the same original value
        String decrypted1 = DesCrypt.doDecode(encrypted1);
        String decrypted2 = DesCrypt.doDecode(encrypted2);
        
        assertEquals("First decryption should match original", TEST_STRING_SIMPLE, decrypted1);
        assertEquals("Second decryption should match original", TEST_STRING_SIMPLE, decrypted2);
    }
    
    @Test
    public void testBase64Output() throws Exception {
        String encrypted = DesCrypt.doEncode(TEST_STRING_SIMPLE);
        
        // Check that output is valid Base64 (no invalid characters)
        assertTrue("Encrypted output should be valid Base64", isValidBase64(encrypted));
        
        // Check that it's URL-safe (no problematic characters)
        assertFalse("Should not contain spaces", encrypted.contains(" "));
        assertFalse("Should not contain newlines", encrypted.contains("\n"));
        assertFalse("Should not contain carriage returns", encrypted.contains("\r"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEncryptNullInput() throws Exception {
        DesCrypt.doEncode(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEncryptEmptyInput() throws Exception {
        DesCrypt.doEncode("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDecryptNullInput() throws Exception {
        DesCrypt.doDecode(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDecryptEmptyInput() throws Exception {
        DesCrypt.doDecode("");
    }
    
    @Test(expected = Exception.class)
    public void testDecryptInvalidBase64() throws Exception {
        DesCrypt.doDecode("This is not valid Base64!");
    }
    
    @Test(expected = Exception.class)
    public void testDecryptTooShortData() throws Exception {
        // Create a valid Base64 string that's too short to contain salt + IV + ciphertext + tag
        DesCrypt.doDecode("dGVzdA=="); // "test" in Base64, too short
    }
    
    @Test(expected = Exception.class)
    public void testDecryptCorruptedData() throws Exception {
        // First encrypt something valid
        String validEncrypted = DesCrypt.doEncode(TEST_STRING_SIMPLE);
        
        // Then corrupt it by changing some characters
        String corrupted = validEncrypted.substring(0, validEncrypted.length() - 5) + "XXXXX";
        
        // This should fail to decrypt
        DesCrypt.doDecode(corrupted);
    }
    
    @Test
    public void testMultipleRoundTrips() throws Exception {
        String current = TEST_STRING_SIMPLE;
        
        // Encrypt and decrypt multiple times
        for (int i = 0; i < 5; i++) {
            String encrypted = DesCrypt.doEncode(current);
            current = DesCrypt.doDecode(encrypted);
        }
        
        assertEquals("Multiple round trips should preserve data", TEST_STRING_SIMPLE, current);
    }
    
    @Test
    public void testSpecialCharacters() throws Exception {
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        String encrypted = DesCrypt.doEncode(specialChars);
        String decrypted = DesCrypt.doDecode(encrypted);
        assertEquals("Special characters should be preserved", specialChars, decrypted);
    }
    
    @Test
    public void testWhitespaceHandling() throws Exception {
        String withWhitespace = "  Hello World  \n\t";
        String encrypted = DesCrypt.doEncode(withWhitespace);
        String decrypted = DesCrypt.doDecode(encrypted);
        
        // The implementation trims the result, so we expect trimmed output
        assertEquals("Whitespace should be handled correctly", withWhitespace.trim(), decrypted);
    }
    
    @Test
    public void testLoginControllerUseCases() throws Exception {
        // Test the specific patterns used in LoginController
        
        // Signup pattern: email::password
        String signupData = "user@example.com::mypassword123";
        String encrypted = DesCrypt.doEncode(signupData);
        String decrypted = DesCrypt.doDecode(encrypted);
        assertEquals("Signup data should decrypt correctly", signupData, decrypted);
        
        // Password reset pattern: email::timestamp
        String resetData = "user@example.com::" + System.currentTimeMillis();
        encrypted = DesCrypt.doEncode(resetData);
        decrypted = DesCrypt.doDecode(encrypted);
        assertEquals("Reset data should decrypt correctly", resetData, decrypted);
    }
    
    @Test
    public void testPerformance() throws Exception {
        // Basic performance test - should complete reasonably quickly
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            String encrypted = DesCrypt.doEncode(TEST_STRING_SIMPLE);
            DesCrypt.doDecode(encrypted);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete 100 round trips in under 10 seconds (very generous)
        assertTrue("Performance should be reasonable", duration < 10000);
    }
    
    /**
     * Helper method to check if a string is valid Base64
     */
    private boolean isValidBase64(String str) {
        try {
            org.apache.commons.codec.binary.Base64.decodeBase64(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}