package com.example.gsb_medecine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.SecureRandom;

public class Authentification extends AppCompatActivity {
    //declaration des attributs en prive
    private EditText codeV;
    private EditText cle;
    private LinearLayout layoutKey;
    String myRandomKey;
    private static final String SECURETOKEN = "BethSepher1";
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_STATUS = "userStatus";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        layoutKey = findViewById(R.id.layoutKey);
        codeV = findViewById(R.id.edit_text_codeVisiteur);
        cle = findViewById(R.id.edit_text_cleSecrete);
        layoutKey.setVisibility(View.INVISIBLE);


    }



    public void afficherLayout(View v){
        layoutKey.setVisibility(View.VISIBLE);
        myRandomKey = genererChaineAleatoire(5);
        String codeVisiteur = codeV.getText().toString();
        SendKeyTask sendKeyTask = new SendKeyTask(getApplicationContext());
        sendKeyTask.execute(codeVisiteur,myRandomKey,SECURETOKEN);
    }



    private String genererChaineAleatoire(int longueur) {
        String caracteresPermis = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder chaineAleatoire = new StringBuilder();

        SecureRandom random = new SecureRandom();

        for (int i = 0; i < longueur; i++) {
            int index = random.nextInt(caracteresPermis.length());
            char caractereAleatoire = caracteresPermis.charAt(index);
            chaineAleatoire.append(caractereAleatoire);
        }

        return chaineAleatoire.toString();
    }

    public void comparerCles(View v){
        String cleVisiteur = cle.getText().toString().trim();
        if (cleVisiteur.equals(myRandomKey)){
            Toast.makeText(this,"Succes", Toast.LENGTH_LONG).show();
            setUserStatus("Authentification = OK");
            Intent authIntent = new Intent(this, MainActivity.class);
            startActivity(authIntent);
        }else {
            Toast.makeText(this,"Echec", Toast.LENGTH_LONG).show();
            setUserStatus("Authentification = KO");
        }
    }


    private void setUserStatus(String status) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_STATUS, status);
        editor.apply();
    }

}

