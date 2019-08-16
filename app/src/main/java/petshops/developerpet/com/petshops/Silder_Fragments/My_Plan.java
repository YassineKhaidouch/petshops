package petshops.developerpet.com.petshops.Silder_Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import petshops.developerpet.com.petshops.R;

public class My_Plan extends Fragment {
    public My_Plan() {
        // Required empty public constructor
    }

    int[] ImagePlan = {
            R.mipmap.pay_month1,
            R.mipmap.pay_month3,
            R.mipmap.pay_month6,
            R.mipmap.pay_month12,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_plan_fragment, container, false);

        ImageView CurrentPlan = (ImageView) rootView.findViewById(R.id.currentPlan);
        ImageView image1 = (ImageView) rootView.findViewById(R.id.image1);
        ImageView image2 = (ImageView) rootView.findViewById(R.id.image2);
        ImageView image3 = (ImageView) rootView.findViewById(R.id.image3);

        ((Button) rootView.findViewById(R.id.upgradePlan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  startActivity(new Intent(getContext(), Payment_activity.class));

            }
        });

        return rootView;
    }


}