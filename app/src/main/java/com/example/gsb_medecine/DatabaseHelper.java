package com.example.gsb_medecine;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medicaments.db";
    private String DATABASE_PATH; // vérifie si la BDD existe
    private static final int DATABASE_VERSION = 2;
    private static DatabaseHelper sInstance; // permet la connexion à la BDD
    private Context mycontext;
    private static final String PREMIERE_VOIE = "Choisir une voie d'administration : ";

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mycontext = context;
        String filesDir = context.getFilesDir().getPath(); // /data/data/com.package.nom/files/
        DATABASE_PATH = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases/"; // /data/data/com.package.nom/databases/

        // Si la bdd n'existe pas dans le dossier de l'app
        if (!checkdatabase()) { // si database existe pas
            // copy db de 'assets' vers DATABASE_PATH
            Log.d("APP", "BDD a copier"); // classe qui permet d'afficher une erreur
            copydatabase(); // fonction qui copie la BDD au bonne endroit dans l'app

        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: Define the tables and necessary structures
        // Note: You should execute the appropriate CREATE TABLE queries here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            //Log.d("debug", "onUpgrade() : oldVersion=" + oldVersion + ",newVersion=" + newVersion);
            mycontext.deleteDatabase(DATABASE_NAME);
            copydatabase();
        }
    } // onUpgrade

    // TODO: Implement methods to interact with the database, such as fetching distinct Voies_dadministration


    private boolean checkdatabase() { // fonction qui vérifie si dans la BDD existe en fonction du nom
        // retourne true/false si la bdd existe dans le dossier de l'app
        File dbfile = new File(DATABASE_PATH + DATABASE_NAME);
        return dbfile.exists();
    }

    private void copydatabase() {

        final String outFileName = DATABASE_PATH + DATABASE_NAME;

        //AssetManager assetManager = mycontext.getAssets();
        InputStream myInput;

        try {
            // Ouvre le fichier de la  bdd de 'assets' en lecture
            myInput = mycontext.getAssets().open(DATABASE_NAME);

            // dossier de destination
            File pathFile = new File(DATABASE_PATH);
            if (!pathFile.exists()) {
                if (!pathFile.mkdirs()) {
                    Toast.makeText(mycontext, "Erreur : copydatabase(), pathFile.mkdirs()", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Ouverture en écriture du fichier bdd de destination
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfert de inputfile vers outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Fermeture
            Log.d("APP", "BDD copiée");
            myOutput.flush();
            myOutput.close();
            myInput.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ERROR", "erreur copie de la base");
            Toast.makeText(mycontext, "Erreur : copydatabase()", Toast.LENGTH_SHORT).show();
        }

        // on greffe le numéro de version
        try {
            SQLiteDatabase checkdb = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            checkdb.setVersion(DATABASE_VERSION);
        } catch (SQLiteException e) {
            // bdd n'existe pas
        }


    }

    public List<Medicament> searchMedicament(String denomination, String formePharmaceutique, String titulaires, String denominationSubstance, String voiesAdmin) {
        List<Medicament> medicamentList = new ArrayList<>();
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add("%" + denomination + "%");
        selectionArgs.add("%" + formePharmaceutique + "%");
        selectionArgs.add("%" + titulaires + "%");
        selectionArgs.add("%" + removeAccents(denominationSubstance) + "%");
        SQLiteDatabase db = this.getReadableDatabase(); // fonction qui permet d'avoir acces a la base de donnees
        String finSQL ="";
        if (!voiesAdmin.equals(PREMIERE_VOIE)) { //ds le cas ou le user a entrer qqch ds la barre de recherche
            finSQL = "AND Voies_dadministration LIKE ?"; //je rajoute a la requete SQL
            selectionArgs.add("%" + voiesAdmin + "%");
        }
        String SQLSubstance = "SELECT CODE_CIS FROM CIS_COMPO_bdpm WHERE replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(upper(Denomination_substance), 'Â','A'),'Ä','A'),'À','A'),'É','E'),'Á','A'),'Ï','I'), 'Ê','E'),'È','E'),'Ô','O'),'Ü','U'), 'Ç','C' ) LIKE ?" ;
        String query = "SELECT * FROM CIS_bdpm WHERE " +
                "Denomination_du_medicament LIKE ? AND " + //comme une requete prepare on ne met pas les valeurs en dur on met ?
                "Forme_pharmaceutique LIKE ? AND " +
                "Titulaires LIKE ? AND " +
                "Code_CIS IN (" + SQLSubstance + ") " +
                finSQL;
        // les valeurs a remplacer dds la requete
        Cursor cursor = db.rawQuery(query, selectionArgs.toArray(new String[0]));//cursor contient le resultat de query c la requete SQL
        if (cursor.moveToFirst()) { //il est plein
            do {
                // recupere les valeurs de la ligne actuelle
                int codeCIS = cursor.getInt(cursor.getColumnIndex("Code_CIS"));
                String denominationMedicament = cursor.getString(cursor.getColumnIndex("Denomination_du_medicament"));
                String formePharmaceutiqueMedicament = cursor.getString(cursor.getColumnIndex("Forme_pharmaceutique"));
                String voiesAdminMedicament = cursor.getString(cursor.getColumnIndex("Voies_dadministration"));
                String titulairesMedicament = cursor.getString(cursor.getColumnIndex("Titulaires"));
                String statutAdminMedicament = cursor.getString(cursor.getColumnIndex("Statut_administratif_de_lAMM"));
                // parcour ligne apres ligne en recuperer chaque valeur
                Medicament medicament = new Medicament(); // instancie une instance de la classe medicament
                medicament.setCodeCIS(codeCIS); //je rentre ds cet objet la valeur codeCIS que le user a entrer grace a la fonction set qui modifie (dans la class medicament)
                medicament.setDenomination(denominationMedicament); // pareil pour denominationMedicament
                medicament.setFormePharmaceutique(formePharmaceutiqueMedicament);
                medicament.setVoiesAdmin(voiesAdminMedicament);
                medicament.setTitulaires(titulairesMedicament);
                medicament.setStatutAdministratif(statutAdminMedicament);
                medicament.setNbMolecule(getNbMollecules(codeCIS));
                medicamentList.add(medicament); // ajoute a chaque fois le medicament ds une liste parce que sinon a chaque tour de boucle il ecrase le precedent
            } while (cursor.moveToNext()); //tant qu'il n'est pas vide et qu'il n'est pas arrivé a la fin
        } else {
            Toast.makeText(mycontext, "Aucun résultat", Toast.LENGTH_LONG).show();//toast affiche makeText class java qui existe deja qui permet d'afficher du text
        }
        cursor.close(); //pour eviter les conflits de connexion
        db.close();

        return medicamentList;
    }

    public int getNbMollecules(int codeCIS) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM CIS_compo_bdpm WHERE Code_CIS = ?",new String[]{String.valueOf(codeCIS)});
        cursor.moveToFirst();
        return cursor.getInt(0);

    }



    private String removeAccents(String input) {
        if (input == null) {
            return null;
        }

        // Normalisation en forme de décomposition (NFD)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Remplacement des caractères diacritiques
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
    public List<String> getDistinctVoiesAdmin() {
        List<String> voiesAdminList = new ArrayList<>(); //declare une liste
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT upper(Voies_dadministration) FROM CIS_bdpm WHERE Voies_dadministration NOT LIKE '%;%' ORDER BY Voies_dadministration", null);
        // upper transforme en majuscules
        //retourne les differentes voies admin qui existent
        voiesAdminList.add(PREMIERE_VOIE); //ajoute a la liste tt ce quon a recuperer
        if (cursor.moveToFirst()){
            do {
                String voieAdmin = cursor.getString(0).toString();
                voiesAdminList.add(voieAdmin);
            }while (cursor.moveToNext());
            }
        cursor.close();
        db.close();

        return voiesAdminList;
        }
    public List<String> getCompositionMedicament(int codeCIS) {
        List<String> compositionList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase(); //Lire la BDD
        Cursor cursor = db.rawQuery("SELECT * FROM CIS_compo_bdpm WHERE Code_CIS = ?", new String[]{String.valueOf(codeCIS)});//Syntaxe pour requete SQL
        int i=0;
        if (cursor.moveToFirst()) { //Parcourir un tableau
            do {
                i++;
                String substance = cursor.getString(cursor.getColumnIndex("Denomination_substance"));//Recupere ds substance la valeur que je trouve dans la colonne denomination substance et je transforme en chaine de char
                String dosage = cursor.getString(cursor.getColumnIndex("Dosage_substance"));
                compositionList.add(i+":"+substance + "(" + dosage + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return compositionList;
    }


    public List<String> getPresentationMedicament(int codeCIS) {
        List<String> presentationList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT Libelle_presentation FROM CIS_CIP_bdpm WHERE Code_CIS = ?", new String[]{String.valueOf(codeCIS)});
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                i++;
                String presentation = cursor.getString(cursor.getColumnIndex("Libelle_presentation"));
                presentationList.add(i+":"+presentation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return presentationList;
    }


}
