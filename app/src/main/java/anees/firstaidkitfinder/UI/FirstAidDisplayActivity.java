/*
References used:
Saillén, G., Stevenson, D., Pereira, A., CEASAR, M. and Ghosh, S. (2019). taskSnapshot.getDownloadUrl() is deprecated. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/50467814/tasksnapshot-getdownloadurl-is-deprecated/50468622 [Accessed 7 Jan. 2019].
Mikael Mengistu. (2016). Android Loading Image From Url with Picasso. [video online] Available at: https://www.youtube.com/watch?v=vrtsiY__Y3Y [Accessed 7 Jan. 2019].
Trivedi, H. and Sarwar, R. (2019). open google maps through intent for specific location in android. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/22704451/open-google-maps-through-intent-for-specific-location-in-android [Accessed 8 Jan. 2019].
A., Tran, H. and Ali, N. (2019). Always Null returned after cropping a photo from a Uri in Android Lollipop?. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/31262080/always-null-returned-after-cropping-a-photo-from-a-uri-in-android-lollipop [Accessed 10 Jan. 2019].
Firebase. (2018). Documentation  |  Firebase. [online] Available at: https://firebase.google.com/docs/ [Accessed 18 Oct. 2018].
Kartika, J. and virani, J. (2019). Android calculate days between two dates. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/42553017/android-calculate-days-between-two-dates/42553096 [Accessed 26 Feb. 2019].
 */

package anees.firstaidkitfinder.UI;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import anees.firstaidkitfinder.MapActivity;
import anees.firstaidkitfinder.Model.firstaidInformation;
import anees.firstaidkitfinder.Model.votingAid;
import anees.firstaidkitfinder.R;

public class FirstAidDisplayActivity extends AppCompatActivity {

    DatabaseReference databaseFirstAid;
    DatabaseReference votingDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aid_display);

        databaseFirstAid = FirebaseDatabase.getInstance().getReference("First Aid");

        Intent intent = getIntent();
        final String aidKey = intent.getStringExtra("key");
        final TextView dispContents = (TextView) findViewById(R.id.firstaidcontents);
        final ImageView dispImage = (ImageView) findViewById(R.id.cameraOutputDisplay);
        final Button directBtn = (Button) findViewById(R.id.directBtn);
        final TextView firstaidTitle = (TextView) findViewById(R.id.firstaidTitle);
        final TextView locationInfo = (TextView) findViewById(R.id.locationAns);
        final TextView firstaidcontentsTitle = (TextView) findViewById(R.id.FirstAidContentsTitle);
        final TextView descInfo = (TextView) findViewById(R.id.descAns);
        final TextView uploadDate = (TextView) findViewById(R.id.uploadDay);
        final ImageButton upvoteButton = (ImageButton) findViewById(R.id.upvoteBtn);
        final ImageButton downvoteButton = (ImageButton) findViewById(R.id.downvoteBtn);
        final TextView upvoteAns = (TextView) findViewById(R.id.upvoteAns);
        final ImageView uploadColours = (ImageView) findViewById(R.id.uploadColour);
        final TextView downvoteAns = (TextView) findViewById(R.id.downvoteAns);

        databaseFirstAid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final DataSnapshot getInfoRef = dataSnapshot.child(aidKey);
                firstaidInformation fs = getInfoRef.getValue(firstaidInformation.class);
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if(currentFirebaseUser == null)
                {
                    Toast.makeText(getApplicationContext(), "You are not Logged in", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), loginPage.class);
                    startActivity(intent);
                }

                votingDatabase = databaseFirstAid.child(aidKey).child("Votes");

                String boxContentsStr = "";
                try
                {
                    if(fs.getName().equals("First Aid Kit"))
                    {
                        for (int i = 0; i < fs.getBoxData().size(); i++)
                        {
                            boxContentsStr = boxContentsStr + " " + fs.getBoxData().get(i) + "\n";
                        }
                    }
                    else
                    {
                        firstaidcontentsTitle.setText("");
                    }
                    dispContents.setText(boxContentsStr);
                    String imagePathURL = fs.getImagePath();
                    Picasso.get().load(imagePathURL).placeholder(R.drawable.imageplaceholdercamera).into(dispImage);
                    firstaidTitle.setText(fs.getName());


                    final Double latDouble = fs.getLatitude();
                    final Double lngDouble = fs.getLongitude();

                    locationInfo.setText(fs.getLocation());
                    if(fs.getDescription().isEmpty())
                    {
                        descInfo.setText("Description Not Provided");
                    }
                    else
                    {
                        descInfo.setText(fs.getDescription());
                    }

                    String dateUploaded = fs.getDate();
                    String daysAns;
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    final String currentDate = dateFormat.format(calendar.getTime());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date cDate = sdf.parse(currentDate);//catch exception
                        Date upload = sdf.parse(dateUploaded);
                        long diff = cDate.getTime() - upload.getTime();
                        Long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                        daysAns = "Added " + days + " days ago";
                        uploadDate.setText(daysAns);

                        if(days >= 0 && days <=30)
                        {
                            Picasso.get().load(R.drawable.greenandroid).into(uploadColours);
                        }
                        else if(days >= 31 && days <=90)
                        {
                            Picasso.get().load(R.drawable.yellowandroid).into(uploadColours);
                        }
                        else if(days > 91)
                        {
                            Picasso.get().load(R.drawable.redandroid).into(uploadColours);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }



                    directBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String strUri = "http://maps.google.com/maps?q=loc:" + latDouble + "," + lngDouble + " (" + "First Aid Kit" + ")";
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                            startActivity(intent);
                        }
                    });
                }
                catch (NullPointerException e)
                {
                    Log.d("Aid Display", "No object");
                }





                votingDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        int upvoteCount = 0;
                        int downvoteCount = 0;
                        for(DataSnapshot snapshot: dataSnapshot.getChildren())
                        {
                            votingAid vd = snapshot.getValue(votingAid.class);
                            if(vd.getVoteNum() == 1)
                            {
                                upvoteCount++;
                            }
                            if(vd.getVoteNum() == -1)
                            {
                                downvoteCount--;
                            }
                           // Toast.makeText(getApplicationContext(), "number " + vd.voteNum, Toast.LENGTH_SHORT).show();
                        }
                        upvoteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference databaseUpvote = databaseFirstAid.child(aidKey).child("Votes");
                                String currentUser = currentFirebaseUser.getUid();
                                if(!(dataSnapshot.child(currentFirebaseUser.getUid()).equals(currentFirebaseUser.getUid())))
                                {
                                    votingAid voteData = new votingAid(0);
                                    databaseUpvote.child(currentUser).setValue(voteData);
                                }
                                DataSnapshot userInfo = dataSnapshot.child(currentFirebaseUser.getUid());
                                databaseUpvote.child(currentUser).child("voteNum").setValue(1);
                            }
                        });
                        downvoteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference databaseUpvote = databaseFirstAid.child(aidKey).child("Votes");
                                String currentUser = currentFirebaseUser.getUid();
                                if(!(dataSnapshot.child(currentFirebaseUser.getUid()).equals(currentFirebaseUser.getUid())))
                                {
                                    votingAid voteData = new votingAid(0);
                                    databaseUpvote.child(currentUser).setValue(voteData);
                                }
                                DataSnapshot userInfo = dataSnapshot.child(currentFirebaseUser.getUid());
                                databaseUpvote.child(currentUser).child("voteNum").setValue(-1);
                            }
                        });
                        upvoteAns.setText(Integer.toString(upvoteCount));
                        downvoteAns.setText(Integer.toString(downvoteCount));

                        if(downvoteCount == -3)
                        {
                            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                            startActivity(intent);
                            databaseFirstAid.child(aidKey).removeValue();
                            Toast.makeText(getApplicationContext(), "Post has been deleted", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MapActivity.class));
    }
}
