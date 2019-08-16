package petshops.developerpet.com.petshops.Auth_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import petshops.developerpet.com.petshops.MainActivity;
import petshops.developerpet.com.petshops.Model.User;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.data.SharedPreferenceHelper;
import petshops.developerpet.com.petshops.data.StaticConfig;
import petshops.developerpet.com.petshops.DataBase.SharedReferenceHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    String id;
    CheckBox rememberMe;
    SharedReferenceHelper GetCondition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Firebase auth instance

        GetCondition = new SharedReferenceHelper();
        GetCondition.getInstance(LoginActivity.this);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            id = auth.getCurrentUser().getUid();
            StaticConfig.UID = GetCondition.getUID();
            /*
            if(SharedReferenceHelper.getInstance(LoginActivity.this).GetPhoneVerify()){
                i = new Intent(LoginActivity.this, MainActivity.class);
            }else{
                i = new Intent(LoginActivity.this, PhoneAuthActivity.class);
            }
            */
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            finish();
        }else{
            /*
            if(!GetCondition.getGetstartScreen()){
                startActivity(new Intent(LoginActivity.this, StartActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                GetCondition.setGetstartScreen(true);
                finish();
            }
            */
        }
        // set the view now
        setContentView(R.layout.activity_login);

       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        rememberMe = (CheckBox) findViewById(R.id.rememberMe);


        if(GetCondition.getKeepMeLogin()){
            inputEmail.setText(GetCondition.getEmail());
            inputPassword.setText(GetCondition.getPassword());
        }

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean Checked = false;
                if(rememberMe.isChecked()){
                    Checked = true;}
                else{ Checked = false;}
                GetCondition.setKeepMeLogin(Checked);

                progressBar.setVisibility(View.VISIBLE);
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    saveUserInfo();
                                    GetCondition.setDataUser(auth.getCurrentUser().getUid(), email);
                                    GetCondition.setPassword(password);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                    finish();
                                }
                            }
                        });
            }
        });
    }
    void saveUserInfo(){
        FirebaseDatabase.getInstance().getReference().child("user/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap hashUser = (HashMap) dataSnapshot.getValue();
                User userInfo = new User();
                userInfo.name = (String) hashUser.get("name");
                userInfo.email = (String) hashUser.get("email");
                userInfo.avata = (String) hashUser.get("avata");
                SharedPreferenceHelper.getInstance(LoginActivity.this).saveUserInfo(userInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

