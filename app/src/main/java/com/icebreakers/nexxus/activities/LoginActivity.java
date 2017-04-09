package com.icebreakers.nexxus.activities;

import android.app.Activity;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.clients.LinkedInClient;
import com.icebreakers.nexxus.helpers.Router;
import com.icebreakers.nexxus.models.internal.Profile;
import com.icebreakers.nexxus.persistence.Database;
import com.icebreakers.nexxus.persistence.NexxusSharePreferences;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by amodi on 4/3/17.
 */

public class LoginActivity extends BaseActivity {
    private static final String TAG = NexxusApplication.BASE_TAG + LoginActivity.class.getSimpleName();

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
                        fetchProfileAndSaveProfileId();
                        Router.startEventListActivity(LoginActivity.this);
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

    private void fetchProfileAndSaveProfileId() {
        LinkedInClient linkedInClient = new LinkedInClient(getApplicationContext());
        linkedInClient.fetchFullProfileInformation(new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                Gson gson = new GsonBuilder().create();
                Profile internalProfile = gson.fromJson(apiResponse.getResponseDataAsString(), Profile.class);
                com.icebreakers.nexxus.models.Profile profile = com.icebreakers.nexxus.models.Profile.convertFromInternalProfile(internalProfile);
                NexxusSharePreferences.putProfileId(thisActivity, profile.id);
                Database.instance().insertProfileValue(profile);
//                Intent intent = new Intent(thisActivity, ProfileActivity.class);
//                intent.putExtra(PROFILE_EXTRA, Parcels.wrap(profile));
//                startActivity(intent);
            }

            @Override
            public void onApiError(LIApiError error) {
                Log.e(TAG, "Error fetching profile information " + error);
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
        return Scope.build(Scope.R_FULLPROFILE, Scope.R_EMAILADDRESS);
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
