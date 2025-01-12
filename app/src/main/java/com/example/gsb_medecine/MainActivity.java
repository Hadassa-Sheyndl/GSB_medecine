package com.example.gsb_medecine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import kotlin.reflect.KFunction;

public class MainActivity extends AppCompatActivity { //extends qd elle herite de qqun

    // on commence par declarer les attributs (comme des variables : typeCommenceParMaj + nomDeVariable)
    private static final String PREF_NAME = "UserPrefs"; //on a créé un attribut PREF_NAME de la classe MainActivity
    private static final String KEY_USER_STATUS = "UserStatus";
    private EditText editTextDenominationMedicament,editTextFormePharmaceutique,editTextTitulaire,editTextDenominationSubstance;
    private Button buttonRechercher,buttonDeconnexion,buttonQuitter;
    private ListView listViewResults;
    private Spinner spinnerVoiesAdministration;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState)  { //constructeur qui va etre executé il verifie si connecté inscris etc
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // associe la vue activity_main
        //initialiser les composants UI (Interface utilisateur) de la vue
        editTextDenominationMedicament = findViewById(R.id.edit_text_denominationdumedicament);//trouver ds la vue en fonction de l'ID
        editTextFormePharmaceutique = findViewById(R.id.edit_text_formepharmaceutique);
        editTextTitulaire = findViewById(R.id.edit_text_titulaire);
        editTextDenominationSubstance = findViewById(R.id.edit_text_denominationsubstance);
        buttonRechercher = findViewById(R.id.btn_rechercher);
        buttonDeconnexion = findViewById(R.id.btn_deconnexion);
        buttonQuitter = findViewById(R.id.btn_quitter);
        listViewResults = findViewById(R.id.list_view_result);
        spinnerVoiesAdministration = findViewById(R.id.spinner_voix_administration);

        dbHelper = new DatabaseHelper(this); //initialise la databaseHelper

        // Set up the spinner with Voies_dadministration data
        setupVoiesAdminSpinner(); // appel de fonction qui permet de remplir les valeurs ds le spinner (liste deroulante)

        // Set up the click listener for the search button
        buttonRechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the search and update the ListView
                performSearch();
                cacherClavier();
            }
        });

        listViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Get the selected item
                Medicament selectedMedicament = (Medicament) adapterView.getItemAtPosition(position);
                // Show composition of the selected medicament
                //afficherCompositionMedicament(selectedMedicament);
            }
        }
        );
    }



    private void setupVoiesAdminSpinner() {
        List<String> voiesAdminList = dbHelper.getDistinctVoiesAdmin();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, voiesAdminList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerVoiesAdministration.setAdapter(spinnerAdapter);
        
    }

    private void cacherClavier() {
        // Obtenez le gestionnaire de fenetre
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Obtenez la vue actuellement focalisÃ©e, qui devrait Ãªtre la vue avec le clavier
        View vueCourante = getCurrentFocus();

        // VÃ©rifiez si la vue est non nulle pour Ã©viter les erreurs
        if (vueCourante != null) {
            // Masquez le clavier
            imm.hideSoftInputFromWindow(vueCourante.getWindowToken(), 0);
        }
    }

    private boolean isUserAuthenticated() { // premiere methode
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String UserStatus = preferences.getString(KEY_USER_STATUS, "");
        return "Authentification = ok".equals(UserStatus);
    }


    //qd on clic btn_rechercher le code doit recuperer les saisis de l'utilisateur
    private void performSearch(){
        String denomination = editTextDenominationMedicament.getText().toString().trim(); // getText recupere et visualise la saisie  to string transforme en chaine de caracteres et trim enleve les espaces
        String formePharmaceutique = editTextFormePharmaceutique.getText().toString().trim();
        String titulaires = editTextTitulaire.getText().toString().trim();
        String denominationSubstance = editTextDenominationSubstance.getText().toString().trim();
        String voiesAdmin = spinnerVoiesAdministration.getSelectedItem().toString();
        List<Medicament> searchResults = dbHelper.searchMedicament(denomination,formePharmaceutique,titulaires,denominationSubstance,voiesAdmin);
        MedicamentAdapter adapter = new MedicamentAdapter(this, searchResults);
        listViewResults.setAdapter(adapter); // espace qui permettra d afficher la liste des medicaments recherchés
        //setText pour modifier
    }



}
