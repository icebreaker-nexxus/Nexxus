package com.icebreakers.nexxus.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.persistence.NexxusSharePreferences;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by amodi on 4/3/17.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.btnLogin) Button loginButton;

    private final Activity thisActivity = this;
    private AccessToken accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LISessionManager.getInstance(getApplicationContext()).init(thisActivity, buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        Log.i(TAG, "Login successful");
                        // save the accessToken for future use
                        saveAccessToken();
                        //NexxusSharePreferences.putLIAccessToken(thisActivity, );
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        Log.e(TAG, "Login was not successful " + error );
                        Toast.makeText(thisActivity, R.string.login_failure, Toast.LENGTH_LONG).show();
                    }
                }, true);
            }
        });
    }

    private void saveAccessToken() {
        LISessionManager sessionManager = LISessionManager.getInstance(getApplicationContext());
        LISession session = sessionManager.getSession();
        accessToken = session.getAccessToken();
        NexxusSharePreferences.putLIAccessToken(thisActivity, accessToken);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE);
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
