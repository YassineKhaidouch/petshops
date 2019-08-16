package petshops.developerpet.com.petshops.Auth_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import petshops.developerpet.com.petshops.Activities.Successfull;
import petshops.developerpet.com.petshops.MainActivity;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.DataBase.SharedReferenceHelper;

/**
 * Created by AJ
 * Created on 09-Jun-17.
 */

public class PhoneAuthActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mPhoneNumberField, mVerificationField;
    Button mStartButton, mVerifyButton;
    TextView mResendButton;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;

    // fields numbers
    TextView field_1 ,field_2 ,field_3 ,field_4 ,field_5 ,field_6;
    // buttons numbers keyboard
    Button key_clean, key_0, key_1, key_2, key_3, key_4, key_5, key_6, key_7, key_8, key_9;

    TableLayout keyboard;

    String NumberVerification = "";

     SharedReferenceHelper SharedReferancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.verivfy_number);

        SharedReferancel = SharedReferenceHelper.getInstance(PhoneAuthActivity.this);

        String numberphone = SharedReferancel.getPhoneNumber();
        // keyboard number
        keyboard = (TableLayout) findViewById(R.id.keyboard);

         key_0 = (Button) findViewById(R.id.key_0);
         key_1 = (Button) findViewById(R.id.key_1);
         key_2 = (Button) findViewById(R.id.key_2);
         key_3 = (Button) findViewById(R.id.key_3);
         key_4 = (Button) findViewById(R.id.key_4);
         key_5 = (Button) findViewById(R.id.key_5);
         key_6 = (Button) findViewById(R.id.key_6);
         key_7 = (Button) findViewById(R.id.key_7);
         key_8 = (Button) findViewById(R.id.key_8);
         key_9 = (Button) findViewById(R.id.key_9);
         key_clean = (Button) findViewById(R.id.key_clean);

        key_0.setOnClickListener(this);
        key_1.setOnClickListener(this);
        key_2.setOnClickListener(this);
        key_3.setOnClickListener(this);
        key_4.setOnClickListener(this);
        key_5.setOnClickListener(this);
        key_6.setOnClickListener(this);
        key_7.setOnClickListener(this);
        key_8.setOnClickListener(this);
        key_9.setOnClickListener(this);
        key_clean.setOnClickListener(this);

        // field number

        field_1 = (TextView) findViewById(R.id.field_1);
        field_2 = (TextView) findViewById(R.id.field_2);
        field_3 = (TextView) findViewById(R.id.field_3);
        field_4 = (TextView) findViewById(R.id.field_4);
        field_5 = (TextView) findViewById(R.id.field_5);
        field_6 = (TextView) findViewById(R.id.field_6);

        TextView title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText(getResources().getString(R.string.enter_number)+" "+numberphone);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);

        mStartButton = (Button) findViewById(R.id.button_start_verification);
        mVerifyButton = (Button) findViewById(R.id.button_verify_phone);
        mResendButton = (TextView) findViewById(R.id.button_resend);



        mPhoneNumberField.setText(numberphone);

        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setVisibility(View.VISIBLE);
                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        if (!validatePhoneNumber()) {
            return;
        }
        startPhoneNumberVerification(mPhoneNumberField.getText().toString());

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        startActivity(new Intent(PhoneAuthActivity.this, Successfull.class));
        finish();

        /*
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            try {
                                //SharedReferancel.setPhoneNumber(mPhoneNumberField.getText().toString());
                                //SharedReferenceHelper.getInstance(PhoneAuthActivity.this).setPhoneVerify(true);
                                startActivity(new Intent(PhoneAuthActivity.this, Successfull.class));
                                finish();
                            }catch (Exception e){}


                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError("Invalid code.");
                            }
                        }
                    }
                });
        */
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);


    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setVisibility(View.VISIBLE);
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
          //  startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class));
           // finish();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   void addNumberToField(String text){

       if(NumberVerification.length() < 6){
           NumberVerification += text;
           switch (NumberVerification.length()){
               case 1 :
                   field_1.setText(text);
                   break;
               case 2 :
                   field_2.setText(text);
                   break;
               case 3 :
                   field_3.setText(text);
                   break;
               case 4 :
                   field_4.setText(text);
                   break;
               case 5 :
                   field_5.setText(text);
                   break;
               case 6 :
                   field_6.setText(text);

                   String code = NumberVerification.toString();
                   if (TextUtils.isEmpty(code)) {
                       msg("Error in text code empty code");
                       return;
                   }
                   try {
                       msg("Please waite ....");
                         verifyPhoneNumberWithCode(mVerificationId, code);
                   }catch (Exception e){
                       msg("Error 678"+e);
                   }
                   break;
           }
       }

   }

   void msg(String text){
       Toast.makeText(this, text, Toast.LENGTH_LONG).show();
   }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                break;
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;

            case R.id.key_clean:
                // delete number from
                field_1.setText("");
                field_2.setText("");
                field_3.setText("");
                field_4.setText("");
                field_5.setText("");
                field_6.setText("");
                NumberVerification = "";

                break;
            case R.id.key_0:
                addNumberToField("0");
                break;
            case R.id.key_1:
                addNumberToField("1");
                break;
            case R.id.key_2:
                addNumberToField("2");
                break;
            case R.id.key_3:
                addNumberToField("3");
                break;
            case R.id.key_4:
                addNumberToField("4");
                break;
            case R.id.key_5:
                addNumberToField("5");
                break;
            case R.id.key_6:
                addNumberToField("6");
                break;
            case R.id.key_7:
                addNumberToField("7");
                break;
            case R.id.key_8:
                addNumberToField("8");
                break;
            case R.id.key_9:
                addNumberToField("9");
                break;
        }
    }

}