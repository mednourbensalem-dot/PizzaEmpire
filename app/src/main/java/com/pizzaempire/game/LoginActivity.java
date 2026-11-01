package com.pizzaempire.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
    private EditText etUser, etPass;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (Session.isLoggedIn(this)) { goMain(); return; }      // already logged in
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        tvError = findViewById(R.id.tvError);

        CheckBox cbShowPass = findViewById(R.id.cbShowPass);
        cbShowPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            etPass.setSelection(etPass.getText().length());
        });

        findViewById(R.id.btnLogin).setOnClickListener(v -> doLogin());
        findViewById(R.id.btnSignUp).setOnClickListener(
            v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void doLogin() {
        String user = etUser.getText().toString().trim();
        String pass = etPass.getText().toString();
        if (user.isEmpty() || pass.isEmpty()) { tvError.setText("Enter your username and password"); return; }

        long id = PlayerDb.get(this).login(user, pass);
        if (id < 0) { tvError.setText("Wrong username or password"); return; }

        GameHolder.clear();              // load this player's save, not the previous one
        Session.set(this, id);
        goMain();
    }

    private void goMain() {
        startActivity(new Intent(this, MainActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
