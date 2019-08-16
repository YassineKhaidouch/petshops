package petshops.developerpet.com.petshops.Auth_Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import petshops.developerpet.com.petshops.DataBase.AppConstants;
import petshops.developerpet.com.petshops.DataBase.SharedReferenceHelper;
import petshops.developerpet.com.petshops.Model.CountryBean;
import petshops.developerpet.com.petshops.Model.CountryListBean;
import petshops.developerpet.com.petshops.Model.User;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.data.StaticConfig;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    FloatingActionButton  btnSignUp;

    EditText PhoneNumber,Confirmpassword, UserName;
    //SQLiteHandler db;
    SharedReferenceHelper dataConditions;
    CheckBox check_conditions;

    private String otpCode = "";
    private Spinner spinnerCountryCodes;
    private ArrayAdapter<String> adapterCountryCodes;
    private ImageView ivFlag;
    private EditText etxtPhone;
    private CountryListBean countryListBean;
    private String phone = "";
    private LinearLayout llVerification;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.create_account));

     //   db = new SQLiteHandler(getApplicationContext());

        dataConditions = new SharedReferenceHelper().getInstance(SignupActivity.this);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        check_conditions = (CheckBox) findViewById(R.id.check_conditions);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);

        UserName = (EditText) findViewById(R.id.username);
        btnSignUp = (FloatingActionButton) findViewById(R.id.sign_up_button);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);

        PhoneNumber = (EditText) findViewById(R.id.phonenumber);
        Confirmpassword = (EditText) findViewById(R.id.cpassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);



        spinnerCountryCodes = (Spinner) findViewById(R.id.spinner_mobile_verification_country_code);
        countryListBean = AppConstants.getCountryBean();
        Collections.sort(countryListBean.getCountries());
        List<String> countryDialCodes = new ArrayList<>();
        for (CountryBean bean : countryListBean.getCountries()) {
            countryDialCodes.add(bean.getDialCode());
        }

//        adapterCountryCodes = ArrayAdapter.createFromResource(this, R.array.country_codes, android.R.layout.simple_spinner_item);
        adapterCountryCodes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countryDialCodes);
        adapterCountryCodes.setDropDownViewResource(R.layout.item_spinner);
        spinnerCountryCodes.setAdapter(adapterCountryCodes);

        ivFlag = (ImageView) findViewById(R.id.iv_mobile_verification_country_flag);
        etxtPhone = (EditText) findViewById(R.id.etxt_mobile_verification_phone);

      //  etxtPhone.setTypeface(typeface);

        Glide.with(getApplicationContext())
                .load("file:///android_asset/" + "flags/" + countryListBean.getCountries().get(20).getCountryCode().toLowerCase() + ".gif")
                .apply(new RequestOptions()
                        .centerCrop()
                        .circleCrop())
                .into(ivFlag);

//        Toast.makeText(MobileVerificationActivity.this, "Toast ....", Toast.LENGTH_SHORT).show();

        spinnerCountryCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //   Toast.makeText(MobileVerificationActivity.this, "Position : "+position, Toast.LENGTH_SHORT).show();
                Glide.with(getApplicationContext())
                        .load("file:///android_asset/" + "flags/"
                                + countryListBean.getCountries().get(position).getCountryCode().toLowerCase() + ".gif")
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(ivFlag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Glide.with(getApplicationContext())
                        .load("file:///android_asset/" + "flags/" + countryListBean.getCountries().get(0).getCountryCode().toLowerCase() + ".gif")
                        .apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop())
                        .into(ivFlag);
            }
        });






        if(!check_conditions.isChecked()){
            btnSignUp.setEnabled(false);
            btnSignUp.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue3)));
        }else{
            btnSignUp.setEnabled(true);
            btnSignUp.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
        }

        check_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!check_conditions.isChecked()){
                    btnSignUp.setEnabled(false);
                    btnSignUp.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue3)));
                }else{
                    btnSignUp.setEnabled(true);
                    btnSignUp.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                }
            }
        });


        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                String confirmPass = Confirmpassword.getText().toString().trim();
                String phone = PhoneNumber.getText().toString().trim();

                final String username = UserName.getText().toString().trim();
                final String PhoneNUmber = spinnerCountryCodes.getSelectedItem().toString() + etxtPhone.getText().toString();



                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter your username!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(PhoneNUmber)) {
                    Toast.makeText(getApplicationContext(), "Enter phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confirmPass)) {
                    Toast.makeText(getApplicationContext(), "Enter confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!confirmPass.equals(password)) {
                    Toast.makeText(getApplicationContext(), "password does not match !!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    //String PhoneNUmber = PhoneNumber.getText().toString();
                                    String id = auth.getCurrentUser().getUid();
                                    //(String name,String mobile,String email,String id)
                                   try {
                                       User newUser = new User();
                                       newUser.email = email;
                                       newUser.mobile = PhoneNUmber;
                                       newUser.id = id;
                                       newUser.password = password;
                                       newUser.name = username;
                                       newUser.planned = false;
                                       newUser.planType = "noplan";
                                       newUser.create_at = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date());
                                       newUser.avata = StaticConfig.STR_DEFAULT_BASE64;
                                       FirebaseDatabase.getInstance().getReference().child("user/" + id).setValue(newUser);

                                   }catch (Exception e){
                                           Toast.makeText(SignupActivity.this,"Error in put profile : "+e,Toast.LENGTH_LONG).show();
                                   }

                                    dataConditions.setName(username);
                                    dataConditions.setDataUser(id,email);
                                    dataConditions.setPassword(password);
                                    dataConditions.setPhoneNumber(PhoneNUmber);
                                   // dataConditions.setPhoneVerify(false);
                                    startActivity(new Intent(SignupActivity.this, PhoneAuthActivity.class));
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    finish();
                                }
                            }
                        });

            }
        });
    }

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