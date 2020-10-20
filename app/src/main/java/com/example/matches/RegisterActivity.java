package com.example.matches;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText nom, prenom, adresseMail, motDePasse, telephone, adresse, age;
    Button register;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nom = (TextInputEditText) findViewById(R.id.nom);
        prenom = (TextInputEditText) findViewById(R.id.prenom);
        adresseMail = (TextInputEditText) findViewById(R.id.mail);
        motDePasse = (TextInputEditText) findViewById(R.id.MDP);
        telephone = (TextInputEditText) findViewById(R.id.tel);
        adresse = (TextInputEditText) findViewById(R.id.adresse);
        age = (TextInputEditText) findViewById(R.id.age);
        register = (Button) findViewById(R.id.register);

        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = adresseMail.getText().toString();
                String motDP = motDePasse.getText().toString();
                String nom2 = nom.getText().toString();
                String prenom2 = prenom.getText().toString();
                String tel = telephone.getText().toString();
                String adres = adresse.getText().toString();
                String age2 = age.getText().toString();

                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), matchActivity.class));
                    finish();
                }

                if (TextUtils.isEmpty(email)) {
                    adresseMail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(motDP)) {
                    motDePasse.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(nom2)) {
                    nom.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(prenom2)) {
                    prenom.setError("Name is required");
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


                if (motDP.length() < 6) {
                    motDePasse.setError("Password Must be >= 6 Caracters");
                    return;
                }
                //Register the user into the firebase
                firebaseAuth.createUserWithEmailAndPassword(email, motDP).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), matchActivity.class));

                        } else {
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
