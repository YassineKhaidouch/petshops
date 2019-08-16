package petshops.developerpet.com.petshops.Silder_Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import petshops.developerpet.com.petshops.R;

public class Contact_Us extends Fragment {
    public Contact_Us() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_us, container, false);
        ((Button) rootView.findViewById(R.id.sendmail)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"+"phptutorsql@gmail.com")); // only email apps should handle this
             //   intent.putExtra(Intent.EXTRA_EMAIL, "satz3150@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "PAPP has ...");
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });
        return rootView;
    }


}