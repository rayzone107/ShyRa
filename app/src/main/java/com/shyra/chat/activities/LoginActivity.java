package com.shyra.chat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pixplicity.easyprefs.library.Prefs;
import com.shyra.chat.R;
import com.shyra.chat.helper.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_GOOGLE_SIGN_IN = 101;
    private static final String PREFS_IS_FACEBOOK_LOGIN = "is_facebook_login";

    @BindView(R.id.login_facebook_original_btn)
    LoginButton mLoginFacebookOriginalButton;

    @BindView(R.id.login_facebook_btn)
    Button mLoginFacebookButton;

    @BindView(R.id.login_google_btn)
    Button mLoginGoogleButton;

    @BindView(R.id.sign_up_btn)
    Button mSignUpButton;

    @BindView(R.id.login_btn)
    Button mLoginButton;

    @BindView(R.id.user_email_et)
    EditText mUserEmailET;

    @BindView(R.id.user_password_et)
    EditText mUserPasswordET;

    @BindView(R.id.forgot_password_tv)
    TextView mForgotPasswordTV;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private boolean isFacebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performFirebaseSetup();
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        mUserEmailET.clearFocus();
        mUserPasswordET.clearFocus();

        performFacebookSetup();
        performGoogleSetup();

        isFacebookLogin = Prefs.getBoolean(PREFS_IS_FACEBOOK_LOGIN, false);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        Prefs.putBoolean(PREFS_IS_FACEBOOK_LOGIN, isFacebookLogin);
        super.onDestroy();
    }

    @OnClick(R.id.login_facebook_btn)
    void onLoginFacebookClicked() {
        showProgress();
        mLoginFacebookOriginalButton.performClick();
    }

    @OnClick(R.id.login_google_btn)
    void onLoginGoogleClicked() {
        showProgress();
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(googleSignInIntent, RC_GOOGLE_SIGN_IN);
    }

    @OnClick(R.id.sign_up_btn)
    void onSingUpEmailClicked() {
        showProgress();
        String email = mUserEmailET.getText().toString();
        String password = mUserPasswordET.getText().toString();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmailAndPassword:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            showErrorToast(task.getException().getMessage());
                        } else {
                            task.getResult().getUser().sendEmailVerification();
                            Toast.makeText(LoginActivity.this, "Verification Email has been sent to your Email ID. Please verify and Login here.", Toast.LENGTH_SHORT).show();
                            hideProgress();
                        }
                    }
                });
    }

    @OnClick(R.id.login_btn)
    void onLoginEmailClicked() {
        showProgress();
        String email = mUserEmailET.getText().toString();
        String password = mUserPasswordET.getText().toString();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmailAndPassword:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            showErrorToast(task.getException().getMessage());
                        }
                    }
                });
    }

    @OnClick(R.id.forgot_password_tv)
    void onForgotPasswordClicked() {
        if (Helper.isEmailValid(mUserEmailET.getText().toString())) {
            mFirebaseAuth.sendPasswordResetEmail(mUserEmailET.getText().toString());
            Toast.makeText(this, "An Email has been sent to the Email ID above. It contains the Reset Password link.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
        }
    }

    private void performFacebookSetup() {
        mCallbackManager = CallbackManager.Factory.create();
        mLoginFacebookOriginalButton.setReadPermissions("email");

        mLoginFacebookOriginalButton.registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        showErrorToast(error.getMessage());
                    }
                });
    }

    private void performGoogleSetup() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    private void performFirebaseSetup() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    if (isFacebookLogin || mFirebaseUser.isEmailVerified()) {
                        launchMainActivity();
                    }
                }
            }
        };
    }

    private void launchMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken: " + token);
        isFacebookLogin = true;
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        signInWithCredential(credential);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        signInWithCredential(credential);
    }

    private void signInWithCredential(AuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            showErrorToast(task.getException().getMessage());
                            hideProgress();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_GOOGLE_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    Log.e(TAG, "onActivityResult: Google Sign In Failed.");
                    hideProgress();
                }
            }
        } else {
            Log.d(TAG, "onActivityResult: COULD NOT LOGIN SUCCESSFULLY");
            hideProgress();
        }
    }

    private void showProgress() {
        mLoginFacebookButton.setEnabled(false);
        mLoginGoogleButton.setEnabled(false);
        mSignUpButton.setEnabled(false);
        mLoginButton.setEnabled(false);
        mForgotPasswordTV.setClickable(false);
        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("Signing In");
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }

    private void hideProgress() {
        mProgressDialog.dismiss();
        mLoginFacebookButton.setEnabled(true);
        mLoginGoogleButton.setEnabled(true);
        mSignUpButton.setEnabled(true);
        mLoginButton.setEnabled(true);
        mForgotPasswordTV.setClickable(true);
    }

    private void showErrorToast(String errorMessage) {
        Toast.makeText(this, "Authentication Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
