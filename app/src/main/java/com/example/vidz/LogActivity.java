package com.example.vidz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.example.vidz.ui.login.LoginViewModel;
import com.example.vidz.ui.login.LoginViewModelFactory;

public class LogActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
     EditText usernameEditText ;
     EditText passwordEditText;
     Button loginButton;
     ProgressBar loadingProgressBar ;
     ImageView logo;
     Boolean isConfirm=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        Amplify.Auth.fetchAuthSession(
                result -> {
                    if(result.isSignedIn())
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    Log.i("AmplifyQuickstart", result.toString());
                },
                error -> Log.e("AmplifyQuickstart", error.toString())
        );
        logo=findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String username=usernameEditText.getText().toString();
              String password=passwordEditText.getText().toString();

               if(isUserNameValid(usernameEditText.getText().toString())&&isPasswordValid(passwordEditText.getText().toString())) {
                   loadingProgressBar.setVisibility(View.VISIBLE);
                   if(!isConfirm) {
                       signUp(usernameEditText.getText().toString(),
                               passwordEditText.getText().toString());
                   }else{
                       confirmSignin(username,password);
                   }
               }else if(!isUserNameValid(username)){
                    usernameEditText.setError("Invalid Username");
               }else if (!isPasswordValid(password)){
                   passwordEditText.setError("Password Invalid");
               }else {
                   usernameEditText.setError("Invalid Username");
                   passwordEditText.setError("Password Invalid");

               }
            }
        });
    }

    private void signUp(String username, String password) {
        Amplify.Auth.signUp(
                username,
                password,
                AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), username).build(),
                result -> {
                    loadingProgressBar.setVisibility(View.GONE);
                    loginButton.setText("Confirm");
                    passwordEditText.setText("");
                    passwordEditText.setHint("Confirmation Code");

                    Log.i("AuthQuickStart", "Result: " + result.toString());
                },
                error -> {
                    if(error.getCause().getMessage().contains("Error Code: UsernameExistsException")){
                        signIn(username,password);
                    }
                    Log.e("AuthQuickStart", "Sign up failed", error);
                }
        );
    }
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 8;
    }
    void signOut(){
        Amplify.Auth.signOut(
                AuthSignOutOptions.builder().globalSignOut(true).build(),
                () -> Log.i("AuthQuickstart", "Signed out globally"),
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }
    void confirmSignin(String username,String code){
        Amplify.Auth.confirmSignUp(
                username,
                code,
                result -> {
                    if(result.isSignUpComplete()){
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));

                    }
                    Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                },

                error -> Log.e("AuthQuickstart", error.toString())
        );
    }
    void signIn(String username, String password){
        Amplify.Auth.signIn(
                username,
                password,
                result -> {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                    Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                },
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }
}