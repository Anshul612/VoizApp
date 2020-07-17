package com.fromapps.voizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VLoginActivity extends AppCompatActivity {

    //private FirebaseUser currentUser;                 can remove
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPwd;
    private TextView WantAccountLink, ForgetPwdLink;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_login);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
       // currentUser = vAuth.getCurrentUser();         can remove

        InitializeFields();

        WantAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToVRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AllowUserToLogin();
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent mobileLoginIntent = new Intent (VLoginActivity.this, VPhoneVerify.class );
                startActivity(mobileLoginIntent);

            }
        });
    }

    private void AllowUserToLogin()
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
            loadingBar.setTitle("Signing In . . . .");
            loadingBar.setMessage("Please Wait a sec . . . . ");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                //String currentUserId = mAuth.getCurrentUser().getUid();
                                //String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                SendUserToMainActivity();
                                Toast.makeText(VLoginActivity.this, "You have Successfully Logged In", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message=task.getException().toString();
                                Toast.makeText(VLoginActivity.this, "Error Occurred: Invalid Email ID or Password ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }


    private void InitializeFields()
    {
        LoginButton=(Button) findViewById(R.id.login_button);
        PhoneLoginButton=(Button) findViewById(R.id.phone_login_button);
        UserEmail=(EditText) findViewById(R.id.login_email);
        UserPwd=(EditText) findViewById(R.id.login_password);
        WantAccountLink=(TextView) findViewById(R.id.want_account);
        ForgetPwdLink=(TextView) findViewById(R.id.forget_password_link);
        loadingBar=new ProgressDialog(this);

    }

   /* protected void onStart() {                can remove
        super.onStart();

        if (currentUser != null)
        {
            SendUserToMainActivity();
        }
    }  */

    private void SendUserToMainActivity()
    {
        Intent mainIntent= new Intent(VLoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    private void SendUserToVRegisterActivity()
    {
        Intent registerIntent = new Intent(VLoginActivity.this,VRegisterActivity.class);
        startActivity(registerIntent);
    }
}