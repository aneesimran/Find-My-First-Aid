/*
References used:
CodingWithMitch. (2017). Google Maps & Google Places Android Course. [video online] Available at: https://www.youtube.com/watch?v=OknMZUnTyds&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt [Accessed 17 Oct. 2018].
 */

package anees.firstaidkitfinder.UI;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;

import anees.firstaidkitfinder.MapActivity;
import anees.firstaidkitfinder.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOGUE_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            Toast.makeText(getApplicationContext(), "You are not Logged in", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), loginPage.class);
            startActivity(intent);
        }

        if(isServicesOK())
        {
            init();
        }
    }

    private void init()
    {
        Button btnAccept = (Button) findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });

        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                System.exit(0);
            }
        });
    }

    public boolean isServicesOK()
    {
        Log.d(TAG, "checkPlayServices: Checking Google Play Services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS)
        {
            //check is fine and the user can make map requests
            Log.d(TAG, "checkPlayServices: Google Play Services is working");
            return true;
        }

        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            //an error occurred but can be resolved
            Log.d(TAG, "checkPlayServices: an error occurred but can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOGUE_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(this, "Unable to make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}

