/*
References used:
ProgrammingWizards TV. (2016). Android Firebase : ListView Multiple Fields - Save,Retrieve,Show [New Firebase]. [video online] Available at: https://www.youtube.com/watch?v=un0fZhUlaU4 [Accessed 3 Apr. 2019].
 */

package anees.firstaidkitfinder.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import anees.firstaidkitfinder.MapActivity;
import anees.firstaidkitfinder.Model.firstaidInformation;
import anees.firstaidkitfinder.R;
import anees.firstaidkitfinder.UI.FirstAidDisplayActivity;
import anees.firstaidkitfinder.UI.loginPage;

public class MapActivityList extends AppCompatActivity {

    DatabaseReference firstAidDatabase;
    DatabaseReference votingDatabase;
    TextView kitNameView;
    ListView mapListView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    ArrayList<String> typeofAid;
    ArrayList<String> tempList;
    ArrayList<Double> distanceList;
    ArrayList<String> kitIDList;
    ArrayAdapter<String> adapter;
    double currentLat;
    double currentLong;
    private Location currentLocation;
    LatLng cLatLng;

    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;

    String v2Txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);
        firstAidDatabase = FirebaseDatabase.getInstance().getReference("First Aid");
        typeofAid = new ArrayList<String>();
        tempList = new ArrayList<String>();
        kitIDList = new ArrayList<String>();
        distanceList = new ArrayList<Double>();
        adapter = new ArrayAdapter<String>(this, R.layout.customlayout, R.id.kitNameView, typeofAid);
        mapListView = (ListView) findViewById(R.id.mapListView);
        getLocation();
        firstAidDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                ArrayList<Integer> distList = new ArrayList<Integer>();
                ArrayList<String> typeofAidTemp = new ArrayList<String>();
                ArrayList<String> kitIDListTemp = new ArrayList<String>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    firstaidInformation fsData = ds.getValue(firstaidInformation.class);
                    int dist = 0;
                    float[] results = new float[5];
                    double cLat = currentLat;
                    double cLong = currentLong;
                    Location.distanceBetween(cLat, cLong, fsData.getLatitude(), fsData.getLongitude(), results);
                    dist = Math.round(results[0]);
                    distList.add(dist);
                    typeofAidTemp.add(fsData.getName() + " " + dist + "m");
                    kitIDListTemp.add(fsData.getKitID());
                }

                ArrayList<Integer> nstore = new ArrayList<Integer>(distList);
                Collections.sort(nstore);
                for (int n = 0; n < distList.size(); n++){
                    nstore.add(n, distList.indexOf(nstore.remove(n)));
                }
                Collections.sort(distList);

                int pos = 0;
                for(int j = 0; j < typeofAidTemp.size(); j++)
                {
                    pos = nstore.get(j);
                    typeofAid.add(typeofAidTemp.get(pos));
                    kitIDList.add(kitIDListTemp.get(pos));
                }

                mapListView.setAdapter(adapter);
                mapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String aidKey = kitIDList.get(i);
                        DataSnapshot markerRef = dataSnapshot.child(aidKey);
                        firstaidInformation fs = markerRef.getValue(firstaidInformation.class);
                        String keyAns = fs.getKitID();
                        Intent s = new Intent(getApplicationContext(), FirstAidDisplayActivity.class);
                        s.putExtra("key", keyAns);
                        startActivity(s);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.logoutBtn:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, loginPage.class));

                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                break;

            case R.id.switchView:
                startActivity(new Intent(this, MapActivity.class));
                break;
        }
        return true;
    }

    protected void getLocation() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            Log.e("TAG", "GPS is on");
            currentLat = location.getLatitude();
            currentLong = location.getLongitude();

        }
        else{

            locationManager.requestLocationUpdates(bestProvider, 1000, 0, (LocationListener) this);
        }
    }



}
