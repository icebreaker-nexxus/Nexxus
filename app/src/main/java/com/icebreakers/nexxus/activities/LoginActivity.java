package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amodi on 4/3/17.
 */

public class LoginActivity extends BaseActivity {
    private static final String TAG = NexxusApplication.BASE_TAG + LoginActivity.class.getSimpleName();

    @BindView(R.id.btnLogin) Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Translucent);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LISessionManager.getInstance(getApplicationContext())
                        .init(LoginActivity.this, Scope.build(Scope.R_FULLPROFILE, Scope.R_EMAILADDRESS), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        Log.i(TAG, "Login successful");
                        onLoginSuccessful();
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        Log.e(TAG, "Login was not successful " + error );
                        Toast.makeText(LoginActivity.this, R.string.login_failure, Toast.LENGTH_LONG).show();
                    }
                }, true);
            }
        });
    }

    private void onLoginSuccessful() {
        // save the accessToken for future use
        ProfileHolder profileHolder = ProfileHolder.getInstance(this);
        profileHolder.saveAceessToken(LoginActivity.this);

        profileHolder.fetchProfle(new ProfileHolder.OnProfileReadyCallback() {
            @Override
            public void onSuccess(com.icebreakers.nexxus.models.Profile profile) {
                Log.d(TAG, "Calling EventListActivity");
                startActivity(new Intent(LoginActivity.this, EventListActivity.class));
                finish();
            }

            @Override
            public void onError(LIApiError error) {

            }
        });
    }

    private void debugInformation() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(
                "com.icebreakers.nexxus",
                PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        for (Signature signature : info.signatures) {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(signature.toByteArray());

            Log.d(TAG, "package name: " + info.packageName);
            Log.d(TAG, "key: " + Base64.encodeToString(md.digest(), Base64.NO_WRAP));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

}
