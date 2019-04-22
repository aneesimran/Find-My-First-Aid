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


public class registerPage extends AppCompatActivity {

    // firebase

    private FirebaseAuth auth;
    private ProgressBar progressBar;
    ProgressDialog registerProgress;


    EditText passwordField, emailField;
    EditText confirmedPassword;
    Button registerSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        confirmedPassword = (EditText) findViewById(R.id.confirmpasswordField);
        registerSubmit = (Button) findViewById(R.id.registerSubmit);

        registerProgress = new ProgressDialog(this);

        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                String confirmedPasswordAns = confirmedPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum of 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!(passwordField.getText().toString().trim().equals(confirmedPasswordAns)))
                {
                    Toast.makeText(getApplicationContext(), "The passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerProgress.setMessage("Creating your Account...");
                registerProgress.show();
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(registerPage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(registerPage.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(registerPage.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                            registerProgress.dismiss();
                                } else {
                                    registerProgress.dismiss();
                                    startActivity(new Intent(registerPage.this, loginPage.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }
}
