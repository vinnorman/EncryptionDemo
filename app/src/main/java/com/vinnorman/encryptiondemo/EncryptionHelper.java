package com.vinnorman.encryptiondemo;

import android.util.Log;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by vinno on 24/04/2018.
 */

class EncryptionHelper {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static final String DECRYPT_PROBLEM_LOG_TAG = "DecryptProblem";
    private static final String ENCRYPT_PROBLEM_LOG_TAG = "EncryptProblem";
    private static final String RSA_ALOGORITHM = "RSA";
    private static final int KEY_SIZE = 1024;

    private String logExceptionNoSuchAlgorithm = "RSA not supported";
    private String logExceptionNoSuchPadding = "Default Padding not supported";
    private String logExceptionInvalidKey = "Invalid Key";
    private String logExceptionIllegalBlockSize = "Illegal block size MESSAGE";
    private String logExceptionBadPadding = "bad padding MESSAGE HERE";

    private KeyPairGenerator keyPairGenerator;

    EncryptionHelper() {
        keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALOGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
        }  catch (NoSuchAlgorithmException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, logExceptionNoSuchAlgorithm, e);
        }
    }

    KeyPair getKeyPair() {
        return keyPairGenerator.generateKeyPair();
    }

    String getEncryptedString(String plainTextString, PublicKey publicKey) {
        try {
            Cipher encryptionCipher = Cipher.getInstance(RSA_ALOGORITHM);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = encryptionCipher.doFinal(plainTextString.getBytes());

            return bytesToHexString(encryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, logExceptionNoSuchAlgorithm, e);
        } catch (NoSuchPaddingException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, logExceptionNoSuchPadding, e);
        } catch (InvalidKeyException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, logExceptionInvalidKey, e);
        } catch (IllegalBlockSizeException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, logExceptionIllegalBlockSize, e);
        } catch (BadPaddingException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, logExceptionBadPadding, e);
        }
        return "encryption didn't work!";
    }

    String getDecryptedString(String encryptedTextString, PrivateKey privateKey) {

        byte[] encryptedBytes = hexStringToByteArray(encryptedTextString);

        try {
            Cipher decryptionCipher = Cipher.getInstance(RSA_ALOGORITHM);
            decryptionCipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = decryptionCipher.doFinal(encryptedBytes);

            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            Log.w(DECRYPT_PROBLEM_LOG_TAG, logExceptionNoSuchAlgorithm, e);
        } catch (NoSuchPaddingException e) {
            Log.w(DECRYPT_PROBLEM_LOG_TAG, logExceptionNoSuchPadding, e);
        } catch (InvalidKeyException e) {
            Log.w(DECRYPT_PROBLEM_LOG_TAG, logExceptionInvalidKey, e);
        } catch (IllegalBlockSizeException e) {
            Log.w(DECRYPT_PROBLEM_LOG_TAG, logExceptionIllegalBlockSize, e);
        } catch (BadPaddingException e) {
            Log.w(DECRYPT_PROBLEM_LOG_TAG, logExceptionBadPadding, e);
        }
        return "decryption didn't work!";
    }

    private String bytesToHexString(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    private byte[] hexStringToByteArray(String hexString) {

        int length = hexString.length();
        byte[] data = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }

}
