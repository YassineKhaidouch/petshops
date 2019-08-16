package petshops.developerpet.com.petshops.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import petshops.developerpet.com.petshops.DataBase.SharedReferenceHelper;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.SplashActivity;


public class LanguageSettings extends AppCompatActivity {

    SharedReferenceHelper shar;
    RadioButton language_english, language_hindi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.language);

        language_english = (RadioButton) findViewById(R.id.language_english);
        language_hindi = (RadioButton) findViewById(R.id.language_hindi);

        shar = SharedReferenceHelper.getInstance(LanguageSettings.this);

        if(shar.getLanguage().equals("hi")){
            language_hindi.setChecked(true);
            language_english.setChecked(false);
        }else{
            language_hindi.setChecked(false);
            language_english.setChecked(true);
        }

        CardView english = (CardView) findViewById(R.id.english);
        CardView hindi = (CardView) findViewById(R.id.hindi);

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_hindi.setChecked(false);
                language_english.setChecked(true);
                SelectedLanguage();
            }
        });

        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_hindi.setChecked(true);
                language_english.setChecked(false);
                SelectedLanguage();
            }
        });

        language_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_hindi.setChecked(false);
                language_english.setChecked(true);
                SelectedLanguage();
            }
        });

        language_hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_hindi.setChecked(true);
                language_english.setChecked(false);
                SelectedLanguage();
            }
        });

    }

    void SelectedLanguage(){
        if(!language_english.isChecked() && language_hindi.isChecked()){
            shar.setLanguage("hi");
        }else if(language_english.isChecked() && !language_hindi.isChecked()){
            shar.setLanguage("en");
        }
        startActivity(new Intent(LanguageSettings.this, SplashActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
