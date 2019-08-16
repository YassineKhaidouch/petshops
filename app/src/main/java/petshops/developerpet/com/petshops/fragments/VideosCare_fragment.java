package petshops.developerpet.com.petshops.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import petshops.developerpet.com.petshops.R;


public class VideosCare_fragment extends Fragment {


    public VideosCare_fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_videos_care_fragment, container, false);
    }

}
