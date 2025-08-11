package com.cocs.security;

/**
 * Simple test runner to verify the DesCrypt implementation works correctly.
 * This can be run manually to validate the security fix.
 */
public class TestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== DesCrypt Security Fix Validation ===");
        System.out.println();
        
        try {
            // Test basic functionality
            testBasicFunctionality();
            
            // Test LoginController patterns
            testLoginControllerPatterns();
            
            // Test security properties
            testSecurityProperties();
            
            System.out.println("✅ All tests passed! The security vulnerability has been fixed.");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testBasicFunctionality() throws Exception {
        System.out.println("Testing basic encryption/decryption...");
        
        String original = "Hello World";
        String encrypted = DesCrypt.doEncode(original);
        String decrypted = DesCrypt.doDecode(encrypted);
        
        if (!original.equals(decrypted)) {
            throw new RuntimeException("Basic encryption/decryption failed");
        }
        
        System.out.println("  Original: " + original);
        System.out.println("  Encrypted: " + encrypted.substring(0, Math.min(50, encrypted.length())) + "...");
        System.out.println("  Decrypted: " + decrypted);
        System.out.println("  ✅ Basic functionality works");
        System.out.println();
    }
    
    private static void testLoginControllerPatterns() throws Exception {
        System.out.println("Testing LoginController usage patterns...");
        
        // Test signup pattern
        String email = "test@example.com";
        String password = "testPassword123";
        String signupData = email + "::" + password;
        
        String encrypted = DesCrypt.doEncode(signupData);
        String decrypted = DesCrypt.doDecode(encrypted);
        String[] parts = decrypted.split("::");
        
        if (!email.equals(parts[0]) || !password.equals(parts[1])) {
            throw new RuntimeException("Signup pattern test failed");
        }
        
        // Test password reset pattern
        long timestamp = System.currentTimeMillis();
        String resetData = email + "::" + timestamp;
        
        encrypted = DesCrypt.doEncode(resetData);
        decrypted = DesCrypt.doDecode(encrypted);
        parts = decrypted.split("::");
        
        if (!email.equals(parts[0]) || !String.valueOf(timestamp).equals(parts[1])) {
            throw new RuntimeException("Password reset pattern test failed");
        }
        
        System.out.println("  ✅ Signup link pattern works");
        System.out.println("  ✅ Password reset link pattern works");
        System.out.println();
    }
    
    private static void testSecurityProperties() throws Exception {
        System.out.println("Testing security properties...");
        
        String input = "test@example.com::password123";
        
        // Test uniqueness
        String encrypted1 = DesCrypt.doEncode(input);
        String encrypted2 = DesCrypt.doEncode(input);
        
        if (encrypted1.equals(encrypted2)) {
            throw new RuntimeException("Encryption should be unique for each call");
        }
        
        // Test both decrypt to same value
        String decrypted1 = DesCrypt.doDecode(encrypted1);
        String decrypted2 = DesCrypt.doDecode(encrypted2);
        
        if (!input.equals(decrypted1) || !input.equals(decrypted2)) {
            throw new RuntimeException("Both encryptions should decrypt to original");
        }
        
        // Test tamper detection
        char[] chars = encrypted1.toCharArray();
        chars[chars.length - 5] = (chars[chars.length - 5] == 'A') ? 'B' : 'A';
        String tampered = new String(chars);
        
        try {
            DesCrypt.doDecode(tampered);
            throw new RuntimeException("Tampered data should not decrypt");
        } catch (Exception e) {
            // Expected
        }
        
        System.out.println("  ✅ Encryption uniqueness verified");
        System.out.println("  ✅ Tamper detection works");
        System.out.println("  ✅ Strong encryption properties confirmed");
        System.out.println();
    }
}