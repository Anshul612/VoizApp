package com.fromapps.voizapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SimpleTimeZone;

public class VGroupTalkActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private ImageButton sendTextButton;
    private EditText userMessage;
    private ScrollView myScrollView;
    private TextView showText;

    private FirebaseAuth mAuth;
    private DatabaseReference UserReference, GroupNameReference, GroupMessageKeReference;

    private String currentGroupName, currentUID, currentUName, curretDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_group_talk);

        currentGroupName = getIntent().getExtras().get("GroupName").toString();
        Toast.makeText(VGroupTalkActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();
        UserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        InitializeFields();

        getUserInformation();

        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveTextInfoToDatabase();
                userMessage.setText("");

                myScrollView.fullScroll(ScrollView.FOCUS_DOWN);             //for automatic scrolling
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GroupNameReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName)
            {
                if(dataSnapshot.exists())
                {
                    DisplayAllMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void InitializeFields() {
        myToolbar = (Toolbar) findViewById(R.id.group_talk_bar_layout);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendTextButton = (ImageButton) findViewById(R.id.send_text_button);
        userMessage = (EditText) findViewById(R.id.type_group_message);
        myScrollView = (ScrollView) findViewById(R.id.scroll_view);
        showText = (TextView) findViewById(R.id.group_talk_text_display);
    }

    private void getUserInformation()
    {
        UserReference.child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUName = dataSnapshot.child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SaveTextInfoToDatabase()
    {
        String message =  userMessage.getText().toString();
        String messageKey = GroupNameReference.push().getKey();
        
        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            curretDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm:ss a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameReference.updateChildren(groupMessageKey);

            GroupMessageKeReference = GroupNameReference.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
                messageInfoMap.put("name",currentUName);
                messageInfoMap.put("message",message);
                messageInfoMap.put("date",curretDate);
                messageInfoMap.put("time",currentTime);
            GroupMessageKeReference.updateChildren(messageInfoMap);

        }
    }

    private void DisplayAllMessages(DataSnapshot dataSnapshot)
    {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext())
        {
            String talkDate = (String)((DataSnapshot) iterator.next()).getValue();
            String talkText = (String)((DataSnapshot) iterator.next()).getValue();
            String talkName = (String)((DataSnapshot) iterator.next()).getValue();
            String talkTime = (String)((DataSnapshot) iterator.next()).getValue();

            showText.append(talkName + " :\n" +talkText + ":\n" + talkDate +"   " +talkTime + "\n\n\n");

            myScrollView.fullScroll(ScrollView.FOCUS_DOWN);             //for automatic scrolling
        }
    }
}