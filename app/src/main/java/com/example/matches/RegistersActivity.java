package com.example.matches;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistersActivity extends AppCompatActivity implements OnMapReadyCallback {
    EditText nom, prenom, adresseMail, motDePasse, telephone, adresse, age, description;
    Button register, photo;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;
    final static int SELECT_PICTURE = 1;
    ImageView imagePhoto;
    MapView mapView;
    SeekBar distanceSeekBar;
    Circle circle;
    private GoogleMap gMap;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenomE);
        adresseMail = (EditText) findViewById(R.id.mail);
        motDePasse = (EditText) findViewById(R.id.MDP);
        telephone = (EditText) findViewById(R.id.tel);
        adresse = (EditText) findViewById(R.id.adresse);
        age = (EditText) findViewById(R.id.age);
        register = (Button) findViewById(R.id.register);
        imagePhoto = (ImageView) findViewById(R.id.imagePhoto);
        description = (EditText) findViewById(R.id.desc);
        mapView = (MapView) findViewById(R.id.mapView);
        distanceSeekBar = (SeekBar) findViewById(R.id.distanceSeekBar);
        ((Button) findViewById(R.id.photo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btGalleryClick(v);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = adresseMail.getText().toString().trim();
                String motDP = motDePasse.getText().toString().trim();
                final String nom2 = nom.getText().toString().trim();
                final String prenom2 = prenom.getText().toString().trim();
                final String tel = telephone.getText().toString().trim();
                final String adres = adresse.getText().toString().trim();
                final String age2 = age.getText().toString().trim();
                final String description2 = description.getText().toString().trim();

                /*if(firebaseAuth.getCurrentUser() !=null){
                    startActivity(new Intent(getApplicationContext(),matchActivity.class));
                    finish();
                }*/
                if (TextUtils.isEmpty(prenom2)) {
                    prenom.setError("Name is required");
                    return;
                }

                if (TextUtils.isEmpty(nom2)) {
                    nom.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    adresseMail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(motDP)) {
                    motDePasse.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(tel)) {
                    telephone.setError("Phone is required");
                    return;
                }
                if (TextUtils.isEmpty(adres)) {
                    adresse.setError("Adress is required");
                    return;
                }
                if (TextUtils.isEmpty(age2)) {
                    age.setError("Age is required");
                    return;
                }
                if (TextUtils.isEmpty(description2)) {
                    age.setError("you need to add a description");
                    return;
                }


                if (motDP.length() < 6) {
                    motDePasse.setError("Password Must be >= 6 Caracters");
                    return;
                }
                //enregister un utilisateur avec un email et un mdp
                firebaseAuth.createUserWithEmailAndPassword(email, motDP).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //lui ajouter DES ATTIBUTS dans la firebase avec firebaseauth et son id
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistersActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firestore.collection("etudiant").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("nom_etudiant", nom2);
                            user.put("prenom_etudiant", prenom2);
                            user.put("email_etudiant", email);
                            user.put("telephone_etudiant", tel);
                            user.put("adresse_etudiant", adres);
                            user.put("age_etudiant", age2);
                            user.put("description_etudiant", description2);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "OnSucces: user profile is created for" + userID);
                                    startregister2();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "OnFailure: " + e.toString());
                                }
                            });
                            //startActivity(new Intent(getApplicationContext(),matchActivity.class));

                        } else {
                            Toast.makeText(RegistersActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        int maxValue = distanceSeekBar.getMax();

        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChangedValue = i;
                circle.setRadius(progressChangedValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync( this);

    }
    public void btGalleryClick(View v) {
        //Création puis ouverture de la boite de dialogue
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, ""), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PICTURE:
                    String path = getRealPathFromURI(data.getData());
                    Log.d("Choose Picture", path);
                    //Transformer la photo en Bitmap
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    //Afficher le Bitmap
                    imagePhoto.setImageBitmap(bitmap);
                    break;
            }
        }
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void startregister2() {
        Intent intent = new Intent(this, matchActivity.class);
        startActivity(intent);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null){
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        gMap.setMinZoomPreference(12);
        LatLng me = new LatLng(40.7143528, -74.0059731);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(me));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(me);
        gMap.addMarker(markerOptions);

        circle = gMap.addCircle(new CircleOptions().center(new LatLng(me.latitude,me.longitude)).strokeColor(Color.RED));

    }
}
