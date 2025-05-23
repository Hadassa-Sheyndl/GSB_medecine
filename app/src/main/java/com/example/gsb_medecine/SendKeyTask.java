package com.example.gsb_medecine;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendKeyTask extends AsyncTask<String, Void, String> {
    private Context mContext;

    // Constructeur pour recevoir le contexte
    public SendKeyTask(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String codeV = params[0];
        String secureKey = params[1];
        String token = params[2];

        String urlString = "http://connexiongsbapp.bethseferap.fr/index.php?codeV=" + codeV + "&secureKey=" + secureKey + "&Token=" + token;
        Log.d("TAG","URL:"+urlString);
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur de connexion";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Afficher le résultat à l'utilisateur avec un Toast
        Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
    }
}

