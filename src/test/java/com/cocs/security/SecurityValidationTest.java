package com.cocs.security;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Security validation tests to ensure the vulnerability has been properly fixed.
 * These tests verify that the new implementation addresses the security issues
 * present in the original DES-based implementation.
 */
public class SecurityValidationTest {
    
    @Test
    public void testEncryptionUniqueness() throws Exception {
        // Verify that the same input produces different encrypted outputs
        // This ensures we're not using a static key or IV
        String input = "test@example.com::password123";
        Set<String> encryptedValues = new HashSet<>();
        
        // Generate multiple encryptions of the same input
        for (int i = 0; i < 10; i++) {
            String encrypted = DesCrypt.doEncode(input);
            assertFalse("Each encryption should be unique", encryptedValues.contains(encrypted));
            encryptedValues.add(encrypted);
            
            // Verify each one decrypts correctly
            String decrypted = DesCrypt.doDecode(encrypted);
            assertEquals("Each encryption should decrypt to original", input, decrypted);
        }
        
        assertEquals("Should have 10 unique encrypted values", 10, encryptedValues.size());
    }
    
    @Test
    public void testNoPatternLeakage() throws Exception {
        // Verify that similar inputs don't produce similar encrypted outputs
        // This ensures we're not using ECB mode which would leak patterns
        String base = "user@example.com::";
        String input1 = base + "password1";
        String input2 = base + "password2";
        
        String encrypted1 = DesCrypt.doEncode(input1);
        String encrypted2 = DesCrypt.doEncode(input2);
        
        // The encrypted values should be completely different despite similar inputs
        assertNotEquals("Similar inputs should not produce similar outputs", encrypted1, encrypted2);
        
        // Check that there's no obvious pattern in the beginning of the encrypted strings
        // (which would indicate ECB mode pattern leakage)
        String prefix1 = encrypted1.substring(0, Math.min(20, encrypted1.length()));
        String prefix2 = encrypted2.substring(0, Math.min(20, encrypted2.length()));
        assertNotEquals("Encrypted prefixes should be different", prefix1, prefix2);
    }
    
    @Test
    public void testKeyStrength() throws Exception {
        // Verify that the encrypted output is sufficiently long to indicate strong encryption
        String input = "test";
        String encrypted = DesCrypt.doEncode(input);
        
        // AES-256-GCM with salt and IV should produce much longer output than DES
        // Salt (16) + IV (12) + ciphertext (at least 4) + tag (16) = at least 48 bytes
        // Base64 encoding increases this by ~33%, so at least 64 characters
        assertTrue("Encrypted output should be sufficiently long", encrypted.length() >= 64);
    }
    
    @Test
    public void testTamperDetection() throws Exception {
        // Verify that tampering with encrypted data is detected
        String input = "test@example.com::password123";
        String encrypted = DesCrypt.doEncode(input);
        
        // Tamper with the encrypted data by changing one character
        char[] chars = encrypted.toCharArray();
        chars[chars.length - 5] = (chars[chars.length - 5] == 'A') ? 'B' : 'A';
        String tampered = new String(chars);
        
        try {
            DesCrypt.doDecode(tampered);
            fail("Tampered data should not decrypt successfully");
        } catch (Exception e) {
            // Expected - tampered data should be rejected
            assertTrue("Should detect tampering", e.getMessage().contains("Decryption failed"));
        }
    }
    
    @Test
    public void testNoInformationLeakage() throws Exception {
        // Verify that decryption failures don't leak information about the data
        String[] invalidInputs = {
            "invalid_base64!",
            "dGVzdA==", // Valid base64 but too short
            "VGhpcyBpcyBhIHZhbGlkIGJhc2U2NCBzdHJpbmcgYnV0IG5vdCB2YWxpZCBlbmNyeXB0ZWQgZGF0YQ==",
            ""
        };
        
        for (String invalid : invalidInputs) {
            try {
                DesCrypt.doDecode(invalid);
                fail("Invalid input should not decrypt: " + invalid);
            } catch (Exception e) {
                // Verify that error messages don't leak sensitive information
                String message = e.getMessage().toLowerCase();
                assertFalse("Error should not leak key info", message.contains("key"));
                assertFalse("Error should not leak salt info", message.contains("salt"));
                assertFalse("Error should not leak iv info", message.contains("iv"));
                
                // Should have a generic error message
                assertTrue("Should have generic error message", 
                    message.contains("decryption failed") || 
                    message.contains("invalid") ||
                    message.contains("corrupted"));
            }
        }
    }
    
    @Test
    public void testCryptographicRandomness() throws Exception {
        // Test that the encryption uses cryptographically secure randomness
        // by checking that encrypted outputs have good entropy
        String input = "test";
        Set<Character> uniqueChars = new HashSet<>();
        
        // Collect characters from multiple encryptions
        for (int i = 0; i < 20; i++) {
            String encrypted = DesCrypt.doEncode(input);
            for (char c : encrypted.toCharArray()) {
                uniqueChars.add(c);
            }
        }
        
        // Base64 uses 64 different characters, we should see good variety
        assertTrue("Should use diverse character set", uniqueChars.size() >= 20);
    }
    
    @Test
    public void testBackwardCompatibilityFailure() throws Exception {
        // Verify that old DES-encrypted data cannot be decrypted by new implementation
        // This ensures we've completely replaced the vulnerable encryption
        
        // These are examples of what DES-encrypted data might look like
        String[] oldFormatExamples = {
            "dGVzdA==", // Simple base64
            "SGVsbG8gV29ybGQ=", // "Hello World" in base64
            "VGVzdCBEYXRh" // "Test Data" in base64
        };
        
        for (String oldFormat : oldFormatExamples) {
            try {
                DesCrypt.doDecode(oldFormat);
                fail("Old format data should not decrypt with new implementation: " + oldFormat);
            } catch (Exception e) {
                // Expected - old format should be rejected
            }
        }
    }
    
    @Test
    public void testPerformanceIsReasonable() throws Exception {
        // Verify that the new secure implementation doesn't have unreasonable performance impact
        String input = "test@example.com::password123";
        
        long startTime = System.currentTimeMillis();
        
        // Perform 50 encryption/decryption cycles
        for (int i = 0; i < 50; i++) {
            String encrypted = DesCrypt.doEncode(input);
            String decrypted = DesCrypt.doDecode(encrypted);
            assertEquals("Data should be preserved", input, decrypted);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete in reasonable time (less than 5 seconds for 50 cycles)
        assertTrue("Performance should be reasonable", duration < 5000);
        System.out.println("50 encryption/decryption cycles completed in " + duration + "ms");
    }
}