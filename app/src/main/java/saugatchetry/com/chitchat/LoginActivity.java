package saugatchetry.com.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;

    private Button loginButton, phoneLoginButton;
    private EditText userEmail,userPassword;
    private TextView needNewAccountLink, forgotPasswordLink;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        initializeUIComponents();

        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your email address",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }
        else{
                progressDialog.setTitle("Signing In");
                progressDialog.setMessage("Please Wait .....");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            redirectToMainActivity();
                                            Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                        else{
                                            String message = task.getException().toString();
                                            Toast.makeText(LoginActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                                            Log.d("FirebaseError",message);
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
        }
    }

    private void initializeUIComponents() {

        loginButton = (Button) findViewById(R.id.login_button);
        phoneLoginButton = (Button) findViewById(R.id.phone_login_button);
        userEmail = (EditText) findViewById(R.id.login_email);
        userPassword = (EditText) findViewById(R.id.login_password);
        needNewAccountLink = (TextView) findViewById(R.id.need_new_account_link);
        forgotPasswordLink = (TextView) findViewById(R.id.forgot_password_link);
        progressDialog = new ProgressDialog(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser != null){
            redirectToMainActivity();
        }
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void redirectToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
