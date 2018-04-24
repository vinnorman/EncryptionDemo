package com.vinnorman.encryptiondemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {


    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private KeyPairGenerator keyPairGenerator;
    private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Button encryptButton, decryptButton;
    private TextView encryptedTextView, decryptedTextView;
    private EditText plainTextInputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        encryptButton = findViewById(R.id.button_encrypt);
        decryptButton = findViewById(R.id.button_decrypt);
        decryptedTextView = findViewById(R.id.textview_decrypted);
        encryptedTextView = findViewById(R.id.textview_encrypted);
        plainTextInputField = findViewById(R.id.edittext_plain_text_input);

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plainTextString = plainTextInputField.getText().toString();
                String encryptionResult = getEncryptedString(plainTextString);
                encryptedTextView.setText(encryptionResult);
            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String encryptedTextString = encryptedTextView.getText().toString();
                String decryptionResult = getDecryptedString(encryptedTextString);
                decryptedTextView.setText(decryptionResult);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    private String getEncryptedString(String plainTextString) {

        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);

            keyPair = keyPairGenerator.generateKeyPair();

            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            Cipher encryptionCipher = Cipher.getInstance("RSA");
            encryptionCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = encryptionCipher.doFinal(plainTextString.getBytes());

            return bytesToHexString(encryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            Log.w("EncryptProblem", "RSA not supported", e);
        } catch (NoSuchPaddingException e) {
            Log.w("EncryptProblem", "Default Padding not supported", e);
        } catch (InvalidKeyException e) {
            Log.w("EncryptProblem", "Invalid Key", e);
        } catch (IllegalBlockSizeException e) {
            Log.w("EncryptProblem", "Illegal block size MESSAGE HERE", e);
        } catch (BadPaddingException e) {
            Log.w("EncryptProblem", " bad padding MESSAGE HERE", e);
        }
        return "the encryption didn't work!";
    }

    private String getDecryptedString(String encryptedTextString) {
        byte[] encryptedBytes = hexStringToByteArray(encryptedTextString);

        try {
            Cipher decryptionCipher = Cipher.getInstance("RSA");
            decryptionCipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = decryptionCipher.doFinal(encryptedBytes);

            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            Log.w("EncryptProblem", "RSA not supported", e);
        } catch (NoSuchPaddingException e) {
            Log.w("EncryptProblem", "Default Padding not supported", e);
        } catch (InvalidKeyException e) {
            Log.w("EncryptProblem", "Invalid Key", e);
        } catch (BadPaddingException e) {
            Log.w("EncryptProblem", " bad padding MESSAGE HERE", e);
        } catch (IllegalBlockSizeException e) {
            Log.w("EncryptProblem", "Illegal block size MESSAGE HERE", e);
        }

        return "the decryption didn't work!";
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
