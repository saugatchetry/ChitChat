package saugatchetry.com.chitchat;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateSettings;
    private EditText userName, userStatus;
    private CircleImageView userImage;

    private String currentUserId;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        rootReference = FirebaseDatabase.getInstance().getReference();
        initializeUIComponents();
        
        updateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        getExistingSettings(); // retrieveUserInfo()
    }

    private void getExistingSettings() {

        rootReference.child("Users").child(currentUserId)
                     .addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))){
                                 String savedUserName = dataSnapshot.child("name").getValue().toString();
                                 String savedUserStatus = dataSnapshot.child("status").getValue().toString();
                                 String savedProfileImage = dataSnapshot.child("image").getValue().toString();


                                 userName.setText(savedUserName);
                                 userStatus.setText(savedUserStatus);

                             }
                             else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                                 String savedUserName = dataSnapshot.child("name").getValue().toString();
                                 String savedUserStatus = dataSnapshot.child("status").getValue().toString();
                                 userName.setText(savedUserName);
                                 userStatus.setText(savedUserStatus);
                             }
                             else{

                             }

                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {

                         }
                     });
    }


    private void initializeUIComponents() {
        updateSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userImage = (CircleImageView) findViewById(R.id.set_profile_image);
    }

    private void updateSettings() {

        String setUserName = userName.getText().toString();
        String setProfileStatus = userStatus.getText().toString();

        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please set user-name", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",setUserName); //key has to be same as the firebase database
            profileMap.put("status",setProfileStatus);

            rootReference.child("Users")
                         .child(currentUserId)
                         .setValue(profileMap)
                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SettingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                    redirectToMainActivity();
                                }
                                else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SettingsActivity.this, "Failed: "+error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
        }
    }

    private void redirectToMainActivity(){
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
