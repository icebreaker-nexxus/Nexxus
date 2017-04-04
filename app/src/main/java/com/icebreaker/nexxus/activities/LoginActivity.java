package com.icebreaker.nexxus.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.icebreaker.nexxus.R;

/**
 * Created by amodi on 4/3/17.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.btnLogin) Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupLoginButton();
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> Toast.makeText(this, "Clicked Login button", Toast.LENGTH_SHORT).show());
    }


}
