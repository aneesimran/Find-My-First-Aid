/*
References used:
Firebase. (2018). Documentation  |  Firebase. [online] Available at: https://firebase.google.com/docs/ [Accessed 18 Oct. 2018].
TVAC Studio. (2016). Android Studio Tutorial - Firebase User Authentication - User Sign In - Part 9. [video online] Available at: https://www.youtube.com/watch?v=oi-UAwiBigQ [Accessed 5 Nov. 2018].
 */

package anees.firstaidkitfinder.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import anees.firstaidkitfinder.R;

public class loginPage extends AppCompatActivity {


    private FirebaseAuth auth;
    private ProgressBar progressBar;
    EditText usernameField, passwordField, emailField;
    Button loginSubmit;
    ProgressDialog loginProgress;
    Button registerBtn;
    Button forgotBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Firebase
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(loginPage.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login_page);

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        loginSubmit = (Button) findViewById(R.id.loginSubmit);
        forgotBtn = (Button) findViewById(R.id.forgotBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        loginProgress = new ProgressDialog(this);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s = new Intent(getApplicationContext(), registerPage.class);
                startActivity(s);
            }
        });

        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginPage.this, forgotPasswordActivity.class));
            }
        });


        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                final String password = passwordField.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginProgress.setMessage("Logging in");
                loginProgress.show();
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(loginPage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        passwordField.setError("Password is greater than 6 characters");
                                        loginProgress.dismiss();
                                    } else {
                                        Toast.makeText(loginPage.this, "Login Failed", Toast.LENGTH_LONG).show();
                                        loginProgress.dismiss();
                                    }
                                } else {
                                    loginProgress.dismiss();
                                    Intent intent = new Intent(loginPage.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });

    }

}
