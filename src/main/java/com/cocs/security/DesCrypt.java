package com.cocs.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;

/**
 * Secure encryption utility class using AES-256-GCM encryption.
 * Replaces the insecure DES encryption with modern cryptographic standards.
 * 
 * Security features:
 * - AES-256 encryption (vs insecure DES)
 * - GCM mode for authenticated encryption (vs insecure ECB)
 * - Unique IV for each encryption operation (vs static key)
 * - PBKDF2 key derivation with salt (vs static key generation)
 * - Cryptographically secure random number generation
 */
public class DesCrypt {
    
    // AES-256-GCM configuration
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 16; // 128 bits
    private static final int AES_KEY_LENGTH = 256; // 256 bits
    
    // PBKDF2 configuration
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1"; // Java 1.7 compatible
    private static final int PBKDF2_ITERATIONS = 100000; // OWASP recommended minimum
    private static final int SALT_LENGTH = 16; // 128 bits
    
    // Master password for key derivation (in production, this should be externally configured)
    private static final String MASTER_PASSWORD = "COCS_SECURE_MASTER_KEY_2024";
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Encrypts the target string using AES-256-GCM with a unique salt and IV.
     * 
     * @param target The plaintext string to encrypt
     * @return Base64-encoded encrypted data containing salt, IV, and ciphertext
     * @throws Exception if encryption fails
     */
    public static String doEncode(String target) throws Exception {
        if (target == null || target.isEmpty()) {
            throw new IllegalArgumentException("Target string cannot be null or empty");
        }
        
        // Generate random salt for key derivation
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        
        // Generate random IV for GCM
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        
        // Derive key from master password and salt
        SecretKey secretKey = deriveKey(MASTER_PASSWORD, salt);
        
        // Initialize cipher for encryption
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        
        // Encrypt the data
        byte[] plaintext = target.getBytes(StandardCharsets.UTF_8);
        byte[] ciphertext = cipher.doFinal(plaintext);
        
        // Combine salt + IV + ciphertext for storage
        ByteBuffer buffer = ByteBuffer.allocate(SALT_LENGTH + GCM_IV_LENGTH + ciphertext.length);
        buffer.put(salt);
        buffer.put(iv);
        buffer.put(ciphertext);
        
        // Return Base64-encoded result
        return Base64.encodeBase64String(buffer.array()).trim();
    }
    
    /**
     * Decrypts the target string using AES-256-GCM.
     * 
     * @param target Base64-encoded encrypted data containing salt, IV, and ciphertext
     * @return The decrypted plaintext string
     * @throws Exception if decryption fails or data is invalid
     */
    public static String doDecode(String target) throws Exception {
        if (target == null || target.isEmpty()) {
            throw new IllegalArgumentException("Target string cannot be null or empty");
        }
        
        try {
            // Decode Base64 data
            byte[] encryptedData = Base64.decodeBase64(target.getBytes(StandardCharsets.UTF_8));
            
            // Check minimum length (salt + IV + at least some ciphertext + tag)
            if (encryptedData.length < SALT_LENGTH + GCM_IV_LENGTH + GCM_TAG_LENGTH + 1) {
                throw new IllegalArgumentException("Invalid encrypted data format");
            }
            
            // Extract components
            ByteBuffer buffer = ByteBuffer.wrap(encryptedData);
            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[GCM_IV_LENGTH];
            buffer.get(salt);
            buffer.get(iv);
            
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);
            
            // Derive key from master password and extracted salt
            SecretKey secretKey = deriveKey(MASTER_PASSWORD, salt);
            
            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            
            // Decrypt and return
            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8).trim();
            
        } catch (Exception e) {
            // For security, don't expose detailed error information
            throw new Exception("Decryption failed: Invalid or corrupted data", e);
        }
    }
    
    /**
     * Derives an AES key from a password and salt using PBKDF2.
     * 
     * @param password The password to derive from
     * @param salt The salt for key derivation
     * @return The derived SecretKey
     * @throws NoSuchAlgorithmException if PBKDF2 is not available
     * @throws InvalidKeySpecException if key derivation fails
     */
    private static SecretKey deriveKey(String password, byte[] salt) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(), 
            salt, 
            PBKDF2_ITERATIONS, 
            AES_KEY_LENGTH
        );
        
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        
        // Clear the password from memory
        spec.clearPassword();
        
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}