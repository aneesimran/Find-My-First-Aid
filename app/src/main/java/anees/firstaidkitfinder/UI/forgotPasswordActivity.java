/*
References used:
Firebase. (2018). Documentation  |  Firebase. [online] Available at: https://firebase.google.com/docs/ [Accessed 18 Oct. 2018].
TVAC Studio. (2016). Android Studio Tutorial - Firebase User Authentication - User Sign In - Part 9. [video online] Available at: https://www.youtube.com/watch?v=oi-UAwiBigQ [Accessed 5 Nov. 2018].
 */


package anees.firstaidkitfinder.UI;

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
import com.google.firebase.auth.FirebaseAuth;

import anees.firstaidkitfinder.R;

public class forgotPasswordActivity extends AppCompatActivity {

    private EditText emailField;
    private Button forgotSubmit;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailField = (EditText) findViewById(R.id.emailField);
        forgotSubmit = (Button) findViewById(R.id.forgotSubmit);


        auth = FirebaseAuth.getInstance();

        forgotSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailField.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email", Toast.LENGTH_SHORT).show();
                    return;
                }


                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(forgotPasswordActivity.this, "We have sent you instructions to reset your password", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(forgotPasswordActivity.this, loginPage.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(forgotPasswordActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
            }
        });
    }
}
