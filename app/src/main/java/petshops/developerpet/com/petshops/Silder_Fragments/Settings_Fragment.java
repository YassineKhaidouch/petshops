package petshops.developerpet.com.petshops.Silder_Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;

import petshops.developerpet.com.petshops.Activities.LanguageSettings;
import petshops.developerpet.com.petshops.R;

public class Settings_Fragment extends Fragment {
    public Settings_Fragment() {
        // Required empty public constructor
    }
    View rootView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.settings_fragment, container, false);

        Switch switch1 = (Switch) rootView.findViewById(R.id.switch1);
        ((CardView) rootView.findViewById(R.id.languages)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),LanguageSettings.class));
            }
        });
        ((CardView) rootView.findViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
            }
        });

        return rootView;
    }


}