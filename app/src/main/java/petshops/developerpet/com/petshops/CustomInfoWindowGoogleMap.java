package petshops.developerpet.com.petshops;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.text.DecimalFormat;
import java.util.List;

import petshops.developerpet.com.petshops.Model.Pet;

public class CustomInfoWindowGoogleMap implements
        GoogleMap.InfoWindowAdapter {
    private Context context;

    List<Pet> pets ;
    public CustomInfoWindowGoogleMap(Context ctx, List<Pet> pets){
        context = ctx;
        this.pets = pets;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
    @Override
    public View getInfoContents(Marker marker) {
        View view = null;
        try {
            final int i = Integer.parseInt(marker.getSnippet());
            view = ((Activity) context).getLayoutInflater().inflate(R.layout.markerviewdetails, null);

            TextView textname = (TextView) view.findViewById(R.id.textname);
            TextView distance = (TextView) view.findViewById(R.id.distance);
            TextView textRating = (TextView) view.findViewById(R.id.textRating);
            TextView available1 = (TextView) view.findViewById(R.id.available1);
            TextView available2 = (TextView) view.findViewById(R.id.available2);
            TextView textOpenHours = (TextView) view.findViewById(R.id.textOpenHours);

            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            ratingBar.setRating((float) pets.get(i).getRate());

            textname.setText(pets.get(i).getName());

            distance.setText(new DecimalFormat("###.#").format((pets.get(i).getDistance()))+"Km");
            textRating.setText("Ratings -" + String.valueOf(pets.get(i).getRate()));
            available1.setText("Avaliable Service - " + pets.get(i).getAvailablepets());
            String avaliable_foods = pets.get(i).getAvailableproducts();
            if(!avaliable_foods.equals("")){
                available2.setText("Avaliable Foods - " + pets.get(i).getAvailableproducts());
            }else{
                available2.setVisibility(View.GONE);
            }
            textOpenHours.setText("Open Hours - " + pets.get(i).getOpenfrom() + " " + pets.get(i).getOpento());

        } catch (Exception e) {
                view = ((Activity) context).getLayoutInflater().inflate(R.layout.markerme, null);
                ((TextView) view.findViewById(R.id.myAddress)).setText(marker.getSnippet());
        }

        return view;
    }
}

