package saugatchetry.com.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button sendVerificationCodeButton, verifyCodeButton;
    private EditText userPhoneNumber, verificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private FirebaseAuth mAuth;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        initializeUIComponents();

        sendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = userPhoneNumber.getText().toString();

                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(PhoneLoginActivity.this, "Please Enter your Phone Number ", Toast.LENGTH_SHORT).show();
                }
                else{

                    dialog.setTitle("Verifying");
                    dialog.setMessage("Verification in Process .. Please Wait");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);
                }
            }
        });


        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCodeButton.setVisibility(View.INVISIBLE);
                userPhoneNumber.setVisibility(View.INVISIBLE);
                String verificationCodeEntered = verificationCode.getText().toString();

                if(TextUtils.isEmpty(verificationCodeEntered)){
                    Toast.makeText(PhoneLoginActivity.this, "Please Enter Code", Toast.LENGTH_SHORT).show();
                }

                else{

                    dialog.setTitle("Verifying");
                    dialog.setMessage("Verification in Process .. Please Wait");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCodeEntered);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                dialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();

                sendVerificationCodeButton.setVisibility(View.VISIBLE);
                userPhoneNumber.setVisibility(View.VISIBLE);
                verifyCodeButton.setVisibility(View.INVISIBLE);
                verificationCode.setVisibility(View.INVISIBLE);
            }


            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                mVerificationId = verificationId;
                mResendToken = token;
                dialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();

                sendVerificationCodeButton.setVisibility(View.INVISIBLE);
                userPhoneNumber.setVisibility(View.INVISIBLE);
                verifyCodeButton.setVisibility(View.VISIBLE);
                verificationCode.setVisibility(View.VISIBLE);

            }
        };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            dialog.dismiss();
                            redirectToMainActivity();

                        } else {

                            String error = task.getException().getMessage();
                            Toast.makeText(PhoneLoginActivity.this, "Error - "+error, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void initializeUIComponents() {
        sendVerificationCodeButton = (Button) findViewById(R.id.send_verification_code);
        verifyCodeButton = (Button) findViewById(R.id.verify_button);

        userPhoneNumber = (EditText) findViewById(R.id.phone_number_input);
        verificationCode = (EditText) findViewById(R.id.verification_code_input);

        dialog = new ProgressDialog(PhoneLoginActivity.this);
    }


    private void redirectToMainActivity(){
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
