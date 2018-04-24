package com.vinnorman.encryptiondemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button encryptButton, decryptButton;
    private TextView encryptedTextView, decryptedTextView;
    private EditText plainTextInputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        encryptButton = findViewById(R.id.button_encrypt);
        decryptButton = findViewById(R.id.button_decrypt);
        decryptedTextView = findViewById(R.id.text_decrypted);
        encryptedTextView = findViewById(R.id.text_encrypted);
        plainTextInputField = findViewById(R.id.edit_plain_text);

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plainTextString = plainTextInputField.getText().toString();
                encryptedTextView.setText(plainTextString);
            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String decryptedTextString = encryptedTextView.getText().toString();
                decryptedTextView.setText(decryptedTextString);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }
}
