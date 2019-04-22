/*
References used:
EDMT Dev. (2016). Android Studio Tutorial - Take picture with Camera. [video online] Available at: https://www.youtube.com/watch?v=ondCeqlAwEI [Accessed 6 Jan. 2019].
TVAC Studio. (2016). Android Studio Tutorial - Capture Image - Firebase Storage - Retrieve Image - Part 14. [video online] Available at: https://www.youtube.com/watch?v=Zy2DKo0v-OY [Accessed 6 Jan. 2019].
A., Tran, H. and Ali, N. (2019). Always Null returned after cropping a photo from a Uri in Android Lollipop?. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/31262080/always-null-returned-after-cropping-a-photo-from-a-uri-in-android-lollipop [Accessed 10 Jan. 2019].
Shaikhhamadali.blogspot.com. (2019). Capture Images and Crop Images using Intent (Android). [online] Available at: https://shaikhhamadali.blogspot.com/2013/09/capture-images-and-crop-images-using.html [Accessed 27 Dec. 2018].
Mustafa, B. and Jadhav, H. (2019). android-bitmap low quality issue. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/32066932/android-bitmap-low-quality-issue/38695995 [Accessed 12 Jan. 2019].
The Code City. (2018). Capture picture from camera - Android Studio Tutorial. [video online] Available at: https://www.youtube.com/watch?v=32RG4lR0PMQ&t=1015s [Accessed 19 Jan. 2019].
Coding in Flow. (2017) Radio Buttons & Radio Groups - Android Studio Tutorial. [video online] Available at: https://www.youtube.com/watch?v=fwSJ1OkK304&t=259s [Accessed 27 Jan. 2019].
KodighTer. (2018). Android Studio Tutorial - Checkbox. [video online] Available at: https://www.youtube.com/watch?v=7c1MkLGIgQs [Accessed 8 Feb. 2019].
H. and Ansodariya, J. (2019). How to grey out text unless checkbox is checked?. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/13281841/how-to-grey-out-text-unless-checkbox-is-checked [Accessed 12 Feb. 2019].
 */
package anees.firstaidkitfinder.UI;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import anees.firstaidkitfinder.MapActivity;
import anees.firstaidkitfinder.Model.firstaidInformation;
import anees.firstaidkitfinder.Model.votingAid;
import anees.firstaidkitfinder.R;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class AddFirstAidActivity extends AppCompatActivity {

    Button saveAidBtn;
    ImageButton cameraBtn;
    Button cancelBtn;
    ImageView cameraOutput;
    EditText descField;
    EditText locateField;
    RadioGroup radioGroup;
    RadioButton radioButton;
    DatabaseReference databaseFirstAid;
    DatabaseReference databaseUpvote;
    private static final String TAG = "AddKitActivity";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private StorageReference sReference;
    CheckBox cPlasters, cBandages, cWetwipes, cPins, cGloves, cDressingsL, cDressingsM, cEyePads;
    ArrayList<String> checkboxData;
    String imagePath;
    Boolean pictureTaken = false;
    Boolean aidLocation = false;
    String imageURL;
    private ProgressDialog mProgress;
    private Uri imageToUploadUri;
    String dataType = "First Aid Kit";

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int CROP_PIC = 2;
    private Uri uri;
    private Uri picURI;

    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";//temp file
    private static Uri tmpUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_first_aid);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            Toast.makeText(getApplicationContext(), "You are not Logged in", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), loginPage.class);
            startActivity(intent);
        }

        sReference = FirebaseStorage.getInstance().getReference();

        getPermissions();

        mProgress = new ProgressDialog(this);
        databaseFirstAid = FirebaseDatabase.getInstance().getReference("First Aid");
        cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        saveAidBtn = (Button) findViewById(R.id.saveAidBtn);
        locateField = (EditText) findViewById(R.id.locateField);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupAdd);
        locateField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        descField = (EditText) findViewById(R.id.descField);

        cPlasters = (CheckBox) findViewById(R.id.plasterBox);
        cBandages = (CheckBox) findViewById(R.id.bandagesBox);
        cDressingsL = (CheckBox) findViewById(R.id.dressingsLBox);
        cDressingsM = (CheckBox) findViewById(R.id.dressingsMBox);
        cPins = (CheckBox) findViewById(R.id.safetypinsBox);
        cGloves = (CheckBox) findViewById(R.id.glovesBox);
        cEyePads = (CheckBox) findViewById(R.id.eyepadsBox);
        cWetwipes = (CheckBox) findViewById(R.id.wipesBox);
        checkboxData = new ArrayList<String>();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                switch (checkedID)
                {
                    case R.id.firstaidradio:
                        cPlasters.setEnabled(true);
                        cBandages.setEnabled(true);
                        cDressingsL.setEnabled(true);
                        cDressingsM.setEnabled(true);
                        cPins.setEnabled(true);
                        cGloves.setEnabled(true);
                        cEyePads.setEnabled(true);
                        cWetwipes.setEnabled(true);
                        break;
                    case R.id.defibrillatorradio:
                        cPlasters.setEnabled(false);
                        cBandages.setEnabled(false);
                        cDressingsL.setEnabled(false);
                        cDressingsM.setEnabled(false);
                        cPins.setEnabled(false);
                        cGloves.setEnabled(false);
                        cEyePads.setEnabled(false);
                        cWetwipes.setEnabled(false);
                        //checkboxData = new ArrayList<String>();
                        break;
                }
            }
        });

        cPlasters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cPlasters.isChecked())
                {
                    checkboxData.add(cPlasters.getText().toString());
                }
                else
                {
                    checkboxData.remove(cPlasters.getText().toString());
                }
            }
        });
        cWetwipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cWetwipes.isChecked())
                {
                    checkboxData.add(cWetwipes.getText().toString());
                }
                else
                {
                    checkboxData.remove(cWetwipes.getText().toString());
                }
            }
        });
        cEyePads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cEyePads.isChecked())
                {
                    checkboxData.add(cEyePads.getText().toString());
                }
                else
                {
                    checkboxData.remove(cEyePads.getText().toString());
                }
            }
        });
        cPins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cPins.isChecked())
                {
                    checkboxData.add(cPins.getText().toString());
                }
                else
                {
                    checkboxData.remove(cPins.getText().toString());
                }
            }
        });
        cGloves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cGloves.isChecked())
                {
                    checkboxData.add(cGloves.getText().toString());
                }
                else
                {
                    checkboxData.remove(cGloves.getText().toString());
                }
            }
        });
        cDressingsM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cDressingsM.isChecked())
                {
                    checkboxData.add(cDressingsM.getText().toString());
                }
                else
                {
                    checkboxData.remove(cDressingsM.getText().toString());
                }
            }
        });
        cDressingsL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cDressingsL.isChecked())
                {
                    checkboxData.add(cDressingsL.getText().toString());
                }
                else
                {
                    checkboxData.remove(cDressingsL.getText().toString());
                }
            }
        });
        cBandages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cBandages.isChecked())
                {
                    checkboxData.add(cBandages.getText().toString());
                }
                else
                {
                    checkboxData.remove(cBandages.getText().toString());
                }
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    //StrictMode.setVmPolicy(builder.build());
                    //File f = new File(Environment.getExternalStorageDirectory(), "POST_IMAGE.jpg");
                    // use standard intent to capture an image
                    Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File image = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    try
                    {
                        File f = File.createTempFile("test", ".jpg", image);
                        Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "anees.firstaidkitfinder.fileprovider", f);
                        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                        imageToUploadUri = Uri.fromFile(f);
                        startActivityForResult(chooserIntent, CAMERA_REQUEST_CODE);
                    }
                    catch(IOException e)
                    {
                        Log.d("log", "error: " + e.toString());
                    }


                } catch (ActivityNotFoundException anfe) {
                    Toast toast = Toast.makeText(getApplicationContext(), "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });



        saveAidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pictureTaken)
                {
                    if(locateField.getText().toString().trim().length() > 0)
                    {
                        aidLocation = true;
                    }
                    if(aidLocation)
                    {
                        getDeviceLocation();
                        Intent s = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(s);
                        Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Enter Where to find the Kit", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {

                    Toast.makeText(getApplicationContext(), "Please take a picture", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void radioCheck(View v)
    {
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);
        Toast.makeText(this, "Selected: " + radioButton.getText(), Toast.LENGTH_SHORT).show();
        dataType = radioButton.getText().toString();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("picUri", imageToUploadUri);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        imageToUploadUri = savedInstanceState.getParcelable("picUri");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {

                Uri selectedImage = imageToUploadUri;
                //getContentResolver().notifyChange(selectedImage, null);
                Bitmap reducedSizeBitmap = getBitmap(imageToUploadUri.getPath());
                //Crop the captured image using an other intent

                //Bitmap photo = (Bitmap) data.getExtras().get("data");
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                picURI = getImageUri(getApplicationContext(), reducedSizeBitmap);
                performCrop();

            } else if (requestCode == CROP_PIC) {
                mProgress.setMessage("Uploading...");
                mProgress.show();

                Uri croppedUri = data.getData();
                Bitmap bitmap = decodeUriAsBitmap(croppedUri);

                cameraBtn.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

                byte[] dataBaos = baos.toByteArray();

                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mapstest1-222018.appspot.com/Images");
                imagePath = "filename" + new Date().getTime();
                StorageReference imageRef = storageReference.child(imagePath);

                final UploadTask uploadTask = imageRef.putBytes(dataBaos);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete()) ;
                        Uri url = uri.getResult();
                        imageURL = url.toString();
                        pictureTaken = true;
                        mProgress.dismiss();
                    }
                });
            }

        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }


    private void performCrop() {
        // take care of exceptions
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(picURI, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 3500);
        intent.putExtra("outputY", 3500);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, CROP_PIC);
        } else {
            Toast.makeText(getApplicationContext(), "No Crop App Available", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    private void getDeviceLocation()
    {
        Log.d(TAG, "getDeviceLocation: Getting current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try
        {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful() && task.getResult() != null)
                    {
                        Log.d(TAG, "onComplete: Found Current Location");
                        Location currentLocation = (Location) task.getResult();
                        saveKit(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    }
                    else {
                        Log.d(TAG, "onComplete: Cannot find Location");
                        Toast.makeText(AddFirstAidActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch(SecurityException e)
        {
            Log.e(TAG, "getDeviceLocation: Security Exception: " + e.getMessage());
        }
    }

    private void saveKit(LatLng latLng)
    {
        String name = dataType;
        String description = descField.getText().toString().trim();
        String locationAns = locateField.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentTime = timeFormat.format(calendar.getTime());
        String currentDate = dateFormat.format(calendar.getTime());
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        String id = databaseFirstAid.push().getKey();
        firstaidInformation firstaidData = new firstaidInformation(id, name, description, locationAns, latitude, longitude, imageURL, currentTime, currentDate, checkboxData);
        databaseFirstAid.child(id).setValue(firstaidData);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseUpvote = databaseFirstAid.child(id).child("Votes");
        String currentUser = currentFirebaseUser.getUid();
        votingAid voteData = new votingAid(0);
        databaseUpvote.child(currentUser).setValue(voteData);
    }

    private void getPermissions()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1234);
        }
    }


}