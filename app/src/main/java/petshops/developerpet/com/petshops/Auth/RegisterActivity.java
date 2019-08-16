package petshops.developerpet.com.petshops.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.data.StaticConfig;


public class RegisterActivity extends AppCompatActivity {
    FloatingActionButton fab;
    CardView cvAdd;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText editTextUsername, editTextPassword, editTextRepeatPassword, editTextName, editTextMobile;
    public static String STR_EXTRA_ACTION_REGISTER = "register";
    RadioGroup radioGroupCate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        cvAdd = (CardView) findViewById(R.id.cv_add);
        editTextUsername = (EditText) findViewById(R.id.et_username);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        editTextName = (EditText) findViewById(R.id.et_name);
        editTextMobile = (EditText) findViewById(R.id.et_mobile);

        editTextRepeatPassword = (EditText) findViewById(R.id.et_repeatpassword);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
            }
        });
    }


    public void clickRegister(View view) {

        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String NameUser = editTextName.getText().toString();
        String Mobile = editTextMobile.getText().toString();
        String repeatPassword = editTextRepeatPassword.getText().toString();
        if(validate(username, password, repeatPassword)){
            Intent data = new Intent();
            data.putExtra(StaticConfig.STR_EXTRA_USERNAME, username);
            data.putExtra(StaticConfig.STR_EXTRA_PASSWORD, password);
          //  data.putExtra(StaticConfig.STR_EXTRA_NAME, NameUser);
          //  data.putExtra(StaticConfig.STR_EXTRA_MOBILE, Mobile);
            data.putExtra(StaticConfig.STR_EXTRA_ACTION, STR_EXTRA_ACTION_REGISTER);
            setResult(RESULT_OK, data);
            finish();
        }else {
            Toast.makeText(this, "Invalid email or not match password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate(String emailStr, String password, String repeatPassword) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return password.length() > 0 && repeatPassword.equals(password) && matcher.find();
    }
}
