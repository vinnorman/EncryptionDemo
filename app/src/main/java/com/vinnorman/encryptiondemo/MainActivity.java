package com.vinnorman.encryptiondemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class MainActivity extends AppCompatActivity {


    private static final String STATE_TAG_ENCRYPTED_TEXT = "encrypted_text";
    private static final String STATE_TAG_PRIVATE_KEY = "private_key";

    private PrivateKey privateKey;
    private Button encryptButton, decryptButton;
    private TextView encryptedTextView, decryptedTextView;
    private EditText plainTextInputField;
    private EncryptionHelper encryptionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        encryptionHelper = new EncryptionHelper();
        setContentView(R.layout.activity_main);
        setViews();
        setButtonClickListeners();
        configureEditTextBehavior();
        if (savedInstanceState != null) {
            String existingEncryptedText = savedInstanceState.getString(STATE_TAG_ENCRYPTED_TEXT);
            if (!TextUtils.isEmpty(existingEncryptedText)) {
                encryptedTextView.setText(existingEncryptedText);
                privateKey = (PrivateKey) savedInstanceState.getSerializable(STATE_TAG_PRIVATE_KEY);
                decryptButton.setEnabled(true);
            }
        }
    }

    private void setViews() {
        encryptButton = findViewById(R.id.button_encrypt);
        decryptButton = findViewById(R.id.button_decrypt);
        decryptedTextView = findViewById(R.id.textview_decrypted);
        encryptedTextView = findViewById(R.id.textview_encrypted);
        plainTextInputField = findViewById(R.id.edittext_plain_text_input);
    }

    private void setButtonClickListeners() {
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performEncryption();
                plainTextInputField.setText(null);
                decryptButton.setEnabled(true);
                plainTextInputField.setCursorVisible(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(plainTextInputField.getWindowToken(), 0);
                }
            }
        });
        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performDecryption();
            }
        });
    }

    private void configureEditTextBehavior() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.all_about_this_app)
                        .setMessage(R.string.about_dialog_content)
                        .setPositiveButton("Ok", null)
                        .show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_TAG_ENCRYPTED_TEXT, encryptedTextView.getText().toString());
        outState.putSerializable(STATE_TAG_PRIVATE_KEY, privateKey);
        super.onSaveInstanceState(outState);
    }

    private void performEncryption() {
        String plainTextString = plainTextInputField.getText().toString();
        KeyPair keyPair = encryptionHelper.getKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
        String encryptionResult = encryptionHelper.getEncryptedString(plainTextString, publicKey);
        encryptedTextView.setText(encryptionResult);
    }

    private void performDecryption() {
        String encryptedTextString = encryptedTextView.getText().toString();
        String decryptionResult = encryptionHelper.getDecryptedString(encryptedTextString, privateKey);
        decryptedTextView.setText(decryptionResult);
    }

}
