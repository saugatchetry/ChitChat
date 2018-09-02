package saugatchetry.com.chitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton sendMessageButton;
    private EditText sendMessageEditText;
    private ScrollView scrollView;
    private TextView displayTextMessages;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootReference, groupNameReference, groupMessageKeyReference;
    private String currentGroupName, currentUserName, currentUserId,currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();

        firebaseAuth = FirebaseAuth.getInstance();

        currentUserId = firebaseAuth.getCurrentUser().getUid();
        rootReference = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        
        initializeUIComponents();

        getUserInformation();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessageToDatabase();
                sendMessageEditText.setText("");
            }
        });
    }




    private void initializeUIComponents() {
        toolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        sendMessageEditText = (EditText) findViewById(R.id.input_group_message);
        displayTextMessages = (TextView) findViewById(R.id.group_chat_text_display);
        scrollView = (ScrollView) findViewById(R.id.my_scroll_view);
    }


    private void getUserInformation() {

        rootReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void saveMessageToDatabase() {

        String enteredMessage = sendMessageEditText.getText().toString();
        String messageKey = groupNameReference.push().getKey();

        if(TextUtils.isEmpty(enteredMessage)){
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar dateCalendar = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM-dd-yyyy");
            currentDate = currentDateFormat.format(dateCalendar.getTime());

            Calendar timeCalendar = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(timeCalendar.getTime());


            HashMap<String,Object> groupMessageKey = new HashMap<>();

            groupNameReference.updateChildren(groupMessageKey);

            groupMessageKeyReference = groupNameReference.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",enteredMessage);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            groupMessageKeyReference.updateChildren(messageInfoMap);

        }
    }
}
