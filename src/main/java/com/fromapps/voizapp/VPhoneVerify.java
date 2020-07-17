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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class VPhoneVerify extends AppCompatActivity {

    private Button sendOtpButton, verifyButton;
    private EditText inputMobileNo, inputOtp;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_phone_verify);

        mAuth = FirebaseAuth.getInstance();

        sendOtpButton = (Button) findViewById(R.id.send_otp_button);
        verifyButton = (Button) findViewById(R.id.verify_otp_button);
        inputMobileNo = (EditText) findViewById(R.id.mobile_number);
        inputOtp = (EditText) findViewById(R.id.otp_code);
        loadingBar = new ProgressDialog(this);


        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String phoneNumber = inputMobileNo.getText().toString();

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(VPhoneVerify.this, "Please Enter your Mobile Number", Toast.LENGTH_SHORT).show();
                } else
                    {
                        loadingBar.setTitle("Phone Verification");
                        loadingBar.setMessage("Please Wait, while your Mobile Number is being authenticated . . .");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            VPhoneVerify.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendOtpButton.setVisibility(View.INVISIBLE);
                inputMobileNo.setVisibility(View.INVISIBLE);

                String otpCode = inputOtp.getText() .toString();

                if(TextUtils.isEmpty(otpCode))
                {
                    Toast.makeText(VPhoneVerify.this, "Please Write Code", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle(" OTP Code");
                    loadingBar.setMessage("Please Wait, while OTP Code is being verified . . .");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
                    signInWithPhoneAuthCredential(credential);

                }

            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e)
            {
                loadingBar.dismiss();

                Toast.makeText(VPhoneVerify.this, "Invalid Mobile Number, Please enter correct Mobile Number with Country Code ", Toast.LENGTH_SHORT).show();

                sendOtpButton.setVisibility(View.VISIBLE);
                inputMobileNo.setVisibility(View.VISIBLE);

                verifyButton.setVisibility(View.INVISIBLE);
                inputOtp.setVisibility(View.INVISIBLE);
            }


            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();

                Toast.makeText(VPhoneVerify.this, "OTP has been sent, Please Check and Verify", Toast.LENGTH_SHORT).show();

                sendOtpButton.setVisibility(View.INVISIBLE);
                inputMobileNo.setVisibility(View.INVISIBLE);

                verifyButton.setVisibility(View.VISIBLE);
                inputOtp.setVisibility(View.VISIBLE);
            }


        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(VPhoneVerify.this, "Congratulation! Your Mobile Number id Verified", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        }
                        else
                            {
                                String message =task.getException().toString();
                                Toast.makeText(VPhoneVerify.this, "Error : " +message, Toast.LENGTH_SHORT).show();
                                

                        }
                    }
                });
    }

    private void sendUserToMainActivity()
    {
        Intent mainIntent = new Intent(VPhoneVerify.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}