package com.fromapps.voizapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
//import android.support.v7.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Toolbar vToolbar;
    private ViewPager myViewPager;
    private TabLayout myTablayout;
    private TabsAccessorAdapter myTabs;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootReference;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vToolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(vToolbar);
        getSupportActionBar().setTitle("VoizApp");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //currentUserID = mAuth.getCurrentUser().getUid();
        RootReference = FirebaseDatabase.getInstance().getReference();

        myViewPager=(ViewPager)findViewById(R.id.main_tabs_pager);
        myTabs=new TabsAccessorAdapter (getSupportFragmentManager());
        myViewPager.setAdapter(myTabs);

        myTablayout=(TabLayout)findViewById(R.id.main_tabs);
        myTablayout.setupWithViewPager(myViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null)
        {
            SendUserToVLoginActivity();
        }
        else
        {
            updateUserStatus("online");
            VerifyExistenceUser();
        }

    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }

    private void VerifyExistenceUser()
    {
        String currentUserID = mAuth.getCurrentUser().getUid();
        RootReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.child("name").exists()))
                {
                    //Toast.makeText(MainActivity.this, "Welcome !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendUserToVSettingsActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() ==R.id.main_Logout_option)
        {
            updateUserStatus("offline");
            mAuth.signOut();
            SendUserToVLoginActivity();
        }
        if(item.getItemId() ==R.id.main_settings_option)
        {
            SendUserToVSettingsActivity();
        }
        if(item.getItemId() ==R.id.main_people_option)
        {
            SendUserToFindPeopleActivity();
        }
        if(item.getItemId() ==R.id.main_groups_create_option)
        {
            RequestNewGroups();
        }
        return true;
    }

    private void RequestNewGroups()
    {
        AlertDialog.Builder Builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        Builder.setTitle("Enter Group Name you want to create");

        final EditText groupNameArea = new EditText(MainActivity.this);
        groupNameArea.setHint("e.g. Voiz Group");
        Builder.setView(groupNameArea);

        Builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameArea.getText().toString();
                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write your Group Name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }
            }
        });
        Builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });
        Builder.show();
    }

    private void CreateNewGroup(final String groupName)
    {
        RootReference.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) 
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this,  "Your Group" +groupName + "is created ", Toast.LENGTH_SHORT).show();
                        }
                        
                    }
                });
    }


    private void SendUserToVLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this,VLoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToVSettingsActivity()
    {
        Intent settingsIntent = new Intent(MainActivity.this,VSettingsActivity.class);
        //settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        //finish();
    }

    private void SendUserToFindPeopleActivity()
    {
        Intent findPeopleIntent = new Intent(MainActivity.this, FindPeopleActivity.class);
        startActivity(findPeopleIntent);
    }

    private void updateUserStatus(String state)
    {
        String currentUserID = mAuth.getCurrentUser().getUid();
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        RootReference.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }
}


