package com.fromapps.voizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VRegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText UserEmail, UserPwd;
    private TextView AlreadyRegistered;
    private DatabaseReference RootReference;

    private FirebaseAuth vAuth;


    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_register);

        vAuth=FirebaseAuth.getInstance();
        RootReference = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        AlreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToVLoginActivity();

            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount()
    {
        String email=UserEmail.getText().toString();
        String pwd=UserPwd.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter your Email Id", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(pwd))
        {
            Toast.makeText(this, "Please enter your Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating your Account . . . .");
            loadingBar.setMessage("Please Wait for a while, your Account is creating . . . . ");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            vAuth.createUserWithEmailAndPassword(email,pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) 
                        {
                            if (task.isSuccessful())
                            {
                                String currentUserID = vAuth.getCurrentUser().getUid();
                                RootReference.child(currentUserID).setValue("");

                                SendUserToMainActivity();
                                Toast.makeText(VRegisterActivity.this, "Your Account Created", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message=task.getException().toString();
                                Toast.makeText(VRegisterActivity.this, "Error Occurred: User Already exist or Invalid Email ID", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }


    private void InitializeFields()
    {
        CreateAccountButton =(Button) findViewById(R.id.register_button);
        UserEmail =(EditText) findViewById(R.id.register_email);
        UserPwd =(EditText) findViewById(R.id.register_password);
        AlreadyRegistered =(TextView) findViewById(R.id.already_registered);

        loadingBar =new ProgressDialog(this);
    }

    private void SendUserToVLoginActivity()
    {
        Intent loginIntent = new Intent(VRegisterActivity.this, VLoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent= new Intent(VRegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}