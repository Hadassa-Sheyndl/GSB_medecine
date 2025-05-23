package com.example.gsb_medecine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MedicamentAdapter extends ArrayAdapter<Medicament> { // class qui permet d'adapter pour l affichage des medoc
    public MedicamentAdapter(Context context, List<Medicament> medicaments) {
        super(context, 0, medicaments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Medicament medicament = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_medicament, parent, false);
        }

        TextView CodeCIS = convertView.findViewById(R.id.text_view_CodeCIS);//find view by id permet de recuperer les composants de ma vue avec les attributs de la classe pour pouvoir les manipuler
        TextView Denomination = convertView.findViewById(R.id.text_view_Denomination);
        TextView FormePharmaceutique = convertView.findViewById(R.id.text_view_Forme_Pharmaceutique);
        TextView VoiesAdmin = convertView.findViewById(R.id.text_view_Voies_admin);
        TextView Titulaires = convertView.findViewById(R.id.text_view_titulaires);
        TextView StatutAdmin = convertView.findViewById(R.id.text_view_statutAdmin);
        TextView NbMolecules = convertView.findViewById(R.id.text_view_nbMolecules);

        CodeCIS.setText(String.valueOf(medicament.getCodeCIS()));
        Denomination.setText(medicament.getDenomination());
        FormePharmaceutique.setText(medicament.getFormePharmaceutique());
        VoiesAdmin.setText(medicament.getVoiesAdmin());
        Titulaires.setText(medicament.getTitulaires());
        StatutAdmin.setText(medicament.getStatutAdministratif());
        NbMolecules.setText(medicament.getnbMolecules());


        return convertView;

    }
}
