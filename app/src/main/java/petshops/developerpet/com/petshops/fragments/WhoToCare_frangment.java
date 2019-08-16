package petshops.developerpet.com.petshops.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import petshops.developerpet.com.petshops.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class WhoToCare_frangment extends Fragment {


    public WhoToCare_frangment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_who_to_care_frangment, container, false);
    }

}
