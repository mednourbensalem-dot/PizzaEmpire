package com.pizzaempire.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends Activity {
    private EditText etUser, etPass, etConfirm;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_signup);

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        etConfirm = findViewById(R.id.etConfirm);
        tvError = findViewById(R.id.tvError);

        CheckBox cbShowPass = findViewById(R.id.cbShowPass);
        cbShowPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            android.text.method.TransformationMethod method = isChecked
                ? HideReturnsTransformationMethod.getInstance()
                : PasswordTransformationMethod.getInstance();
            etPass.setTransformationMethod(method);
            etConfirm.setTransformationMethod(method);
            etPass.setSelection(etPass.getText().length());
            etConfirm.setSelection(etConfirm.getText().length());
        });

        findViewById(R.id.tvSuggestPass).setOnClickListener(v -> {
            etPass.setText("pizza123");
            etConfirm.setText("pizza123");
            tvError.setText("");
        });

        findViewById(R.id.btnCreate).setOnClickListener(v -> doSignUp());
        findViewById(R.id.btnBackLogin).setOnClickListener(v -> finish());
    }

    private void doSignUp() {
        String user = etUser.getText().toString().trim().replace(" ", "_");
        etUser.setText(user);
        String pass = etPass.getText().toString();
        String confirm = etConfirm.getText().toString();

        String err = Auth.checkUsername(user);
        if (err == null) err = Auth.checkPassword(pass);
        if (err == null && !pass.equals(confirm)) err = "Passwords do not match";
        if (err == null && PlayerDb.get(this).usernameTaken(user)) err = "That username is already taken";
        if (err != null) { tvError.setText(err); return; }

        long id = PlayerDb.get(this).createPlayer(user, pass);
        if (id < 0) { tvError.setText("Could not create the account"); return; }

        GameHolder.clear();
        Session.set(this, id);           // log the new player in straight away
        startActivity(new Intent(this, MainActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
