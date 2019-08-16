package petshops.developerpet.com.petshops.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import petshops.developerpet.com.petshops.R;


public class CatigoriesPage extends AppCompatActivity implements View.OnClickListener {
    int p=0;
    ImageView shops ;
    ImageView clinics;
    ImageView spas;
    ImageView grooming;
    ImageView others;
    String[] TypesCatigories = {"shops","clinics","spas","grooming","others" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catigories_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_catigories);

        shops  = (ImageView)findViewById(R.id.shops);
        clinics  = (ImageView)findViewById(R.id.clinics);
        spas = (ImageView)findViewById(R.id.spas);
        grooming = (ImageView)findViewById(R.id.grooming);
        others  = (ImageView)findViewById(R.id.others);

        shops.setImageResource(R.mipmap.shops_s);
        clinics.setImageResource(R.mipmap.clinics);
        spas.setImageResource(R.mipmap.spas);
        grooming.setImageResource(R.mipmap.grooming);
        others.setImageResource(R.mipmap.others);

        shops.setOnClickListener(this);
        clinics.setOnClickListener(this);
        spas.setOnClickListener(this);
        grooming.setOnClickListener(this);
        others.setOnClickListener(this);

        FloatingActionButton addData = (FloatingActionButton) findViewById(R.id.addData);
        addData.setOnClickListener(this);

    }

    void SetImages(int i){

        shops.setImageResource(R.mipmap.shops);
        clinics.setImageResource(R.mipmap.clinics);
        spas.setImageResource(R.mipmap.spas);
        grooming.setImageResource(R.mipmap.grooming);
        others.setImageResource(R.mipmap.others);

        switch (i){
            case 0 :
                shops.setImageResource(R.mipmap.shops_s);
                break;
            case 1:
                clinics.setImageResource(R.mipmap.clinics_s);
                break;
            case 2 :
                spas.setImageResource(R.mipmap.spas_s);
                break;
            case 3:
                grooming.setImageResource(R.mipmap.grooming_s);
                break;
            case 4 :
                others.setImageResource(R.mipmap.others_s);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shops:
                p = 0;
                SetImages(p);
                break;
            case R.id.clinics:
                p = 1;
                SetImages(p);
                break;
            case R.id.spas:
                p = 2;
                SetImages(p);
                break;
            case R.id.grooming:
                p = 3;
                SetImages(p);
                break;
            case R.id.others:
                p = 4;
                SetImages(p);
                break;
            case R.id.addData :
                startActivity(new Intent(CatigoriesPage.this, Add_page.class).putExtra("type",TypesCatigories[p]));
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                finish();
                break;
        }
    }

    // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
