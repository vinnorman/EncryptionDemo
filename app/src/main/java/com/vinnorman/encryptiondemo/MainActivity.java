package com.vinnorman.encryptiondemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
    private static final String DECRYPT_PROBLEM_LOG_TAG = "DecryptProblem";
    public static final String ENCRYPT_PROBLEM_LOG_TAG = "EncryptProblem";
    public static final String RSA_ALOGORITHM = "RSA";
    public static final int KEY_SIZE = 1024;
    public static final String STATE_TAG_ENCRYPTED_TEXT = "EncryptedText";
    public static final String STATE_TAG_PRIVATE_KEY = "private key";

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

        if (savedInstanceState != null) {
            String existingEncryptedText = savedInstanceState.getString(STATE_TAG_ENCRYPTED_TEXT);
            if (!TextUtils.isEmpty(existingEncryptedText)) {
                encryptedTextView.setText(existingEncryptedText);
                privateKey = (PrivateKey) savedInstanceState.getSerializable(STATE_TAG_PRIVATE_KEY);
                decryptButton.setEnabled(true);
            }
        }

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plainTextString = plainTextInputField.getText().toString();
                String encryptionResult = getEncryptedString(plainTextString);
                encryptedTextView.setText(encryptionResult);
                plainTextInputField.setText(null);
                decryptButton.setEnabled(true);
            }
        });
        plainTextInputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    encryptButton.performClick();
                }
                return false;
            }
        });
        plainTextInputField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    encryptButton.setEnabled(false);
                } else {
                    encryptButton.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_TAG_ENCRYPTED_TEXT, encryptedTextView.getText().toString());
        outState.putSerializable(STATE_TAG_PRIVATE_KEY, privateKey);
        super.onSaveInstanceState(outState);
    }

    private String getEncryptedString(String plainTextString) {

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALOGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            Cipher encryptionCipher = Cipher.getInstance(RSA_ALOGORITHM);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = encryptionCipher.doFinal(plainTextString.getBytes());

            return bytesToHexString(encryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, getString(R.string.log_message_no_such_algorith_exception), e);
        } catch (NoSuchPaddingException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, getString(R.string.log_message_no_such_padding_exception), e);
        } catch (InvalidKeyException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, getString(R.string.log_message_invalid_key_exception), e);
        } catch (IllegalBlockSizeException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, getString(R.string.log_message_illegal_block_size_exception), e);
        } catch (BadPaddingException e) {
            Log.w(ENCRYPT_PROBLEM_LOG_TAG, getString(R.string.log_message_bad_padding_exception), e);
        }
        return getString(R.string.encryption_failure_message);
    }

    private String getDecryptedString(String encryptedTextString) {

        byte[] encryptedBytes = hexStringToByteArray(encryptedTextString);

        try {
            Cipher decryptionCipher = Cipher.getInstance(RSA_ALOGORITHM);
            decryptionCipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = decryptionCipher.doFinal(encryptedBytes);

            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            Log.w(DECRYPT_PROBLEM_LOG_TAG, getString(R.string.log_message_no_such_algorith_exception), e);
        } catch (NoSuchPaddingException e) {
            Log.w(DECRYPT_PROBLEM_LOG_TAG, getString(R.string.log_message_no_such_padding_exception), e);
        } catch (InvalidKeyException e) {
            Log.w(DECRYPT_PROBLEM_LOG_TAG, getString(R.string.log_message_invalid_key_exception), e);
        } catch (IllegalBlockSizeException e) {
            Log.w(DECRYPT_PROBLEM_LOG_TAG, getString(R.string.log_message_illegal_block_size_exception), e);
        } catch (BadPaddingException e) {
            Log.w(DECRYPT_PROBLEM_LOG_TAG, getString(R.string.log_message_bad_padding_exception), e);
        }
        return getString(R.string.decryption_failure_message);
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
