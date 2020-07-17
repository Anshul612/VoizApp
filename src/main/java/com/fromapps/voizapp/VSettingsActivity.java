package com.fromapps.voizapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class VSettingsActivity extends AppCompatActivity {

    private Button UpdateAccount;
    private EditText Username, UserStat;
    private CircleImageView UserProfileImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootReference;

    private static final int GalleryPicking = 1;
    private StorageReference userProfilePicReference;
    private ProgressDialog loadingBar;

    private Toolbar SettingsToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_settings);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootReference = FirebaseDatabase.getInstance().getReference();
        userProfilePicReference = FirebaseStorage.getInstance() .getReference().child("Profile Pic");

        InitializeFields();

        //Username.setVisibility(View.INVISIBLE);                 //can be removed if you want user can change username anytime

        UpdateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateAcc();
            }
        });

        RetrieveUserInformation();

        UserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPicking);
            }
        });

    }


    private void InitializeFields() {
        UpdateAccount = (Button) findViewById(R.id.update_button);
        Username = (EditText) findViewById(R.id.set_user_name);
        UserStat = (EditText) findViewById(R.id.set_profile_stat);
        UserProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);
        SettingsToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Update Profile");


    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)        //this method will show the picked image
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPicking && resultCode == RESULT_OK && data != null) {
            Uri PicUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode ==RESULT_OK)
            {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your Profile Pic is Updating . . .");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                

                Uri resultUri = result.getUri();
                final StorageReference imagePath =  userProfilePicReference.child(currentUserID + ".jpg");
                imagePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(VSettingsActivity.this, "Profile Pic Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    //final String downloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                                    //final String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();


                                    //final String  downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();              // use .getStorage() before getDownloadUrl()
                                    RootReference.child("Users").child(currentUserID).child("Pic")
                                            .setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(VSettingsActivity.this, "Pic Saved Successfully", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    } else {
                                                        String message = task.getException().toString();
                                                        Toast.makeText(VSettingsActivity.this, "Error Occurred" + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });

                                }
                            });
                        }
                        else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(VSettingsActivity.this, "Error Occurred" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                   
                });
            }
            
        }
    }   */
    @Override
       protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
       {
           super.onActivityResult(requestCode, resultCode, data);

           if (requestCode==GalleryPicking  &&  resultCode==RESULT_OK  &&  data!=null)
           {
               Uri ImageUri = data.getData();

               CropImage.activity()
                       .setGuidelines(CropImageView.Guidelines.ON)
                       .setAspectRatio(1, 1)
                       .start(this);
           }

           if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
           {
               CropImage.ActivityResult result = CropImage.getActivityResult(data);

               if (resultCode == RESULT_OK)
               {
                   loadingBar.setTitle("Set Profile Image");
                   loadingBar.setMessage("Please wait, your profile image is updating...");
                   loadingBar.setCanceledOnTouchOutside(false);
                   loadingBar.show();


                   Uri resultUri=result.getUri();//This contains the cropped image

                   final StorageReference filePath=userProfilePicReference.child(currentUserID +".jpg");//This way we link the userId with image. This is the file name of the image stored in firebase database.

                   UploadTask uploadTask=filePath.putFile(resultUri);
                   Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                       @Override
                       public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                           if (!task.isSuccessful()) {
                               throw task.getException();
                           }

                           // Continue with the task to get the download URL
                           return filePath.getDownloadUrl();
                       }
                   }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                       @Override
                       public void onComplete(@NonNull Task<Uri> task) {
                           if (task.isSuccessful()) {
                               Uri downloadUri = task.getResult();
                               Toast.makeText(VSettingsActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                               if (downloadUri != null) {

                                   String downloadUrl = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                   RootReference.child("Users").child(currentUserID).child("pic").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           loadingBar.dismiss();
                                           if(!task.isSuccessful()){
                                               String error=task.getException().toString();
                                               Toast.makeText(VSettingsActivity.this,"Error : "+error,Toast.LENGTH_LONG).show();
                                           }else{

                                           }
                                       }
                                   });
                               }

                           } else {
                               // Handle failures
                               // ...
                               Toast.makeText(VSettingsActivity.this,"Error",Toast.LENGTH_LONG).show();
                               loadingBar.dismiss();
                           }
                       }
                   });
                   /*Uri resultUri = result.getUri();


                   StorageReference filePath = userProfilePicReference.child(currentUserID + ".jpg");

                   filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                       {
                           if (task.isSuccessful())
                           {
                               Toast.makeText(VSettingsActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                               final String downloaedUrl = task.getResult().getStorage().getDownloadUrl().toString();

                               RootReference.child("Users").child(currentUserID).child("pic")
                                       .setValue(downloaedUrl)
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task)
                                           {
                                               if (task.isSuccessful())
                                               {
                                                   Toast.makeText(VSettingsActivity.this, "Image save in Database, Successfully...", Toast.LENGTH_SHORT).show();
                                                   loadingBar.dismiss();
                                               }
                                               else
                                               {
                                                   String message = task.getException().toString();
                                                   Toast.makeText(VSettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                   loadingBar.dismiss();
                                               }
                                           }
                                       });
                           }
                           else
                           {
                               String message = task.getException().toString();
                               Toast.makeText(VSettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();
                           }
                       }
                   });*/
               }
           }
       }












    private void UpdateAcc()
    {
        String setUName = Username.getText().toString();
        String setStat = UserStat.getText().toString();

        if (TextUtils.isEmpty(setUName))
        {
            Toast.makeText(this, "Please write the Username you want to use", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStat))
        {
            Toast.makeText(this, "Please Write Status", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> profileMap = new HashMap<>();
                profileMap.put("uid", currentUserID);
                profileMap.put("name", setUName);
                profileMap.put("status", setStat);
            RootReference.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) 
                        {
                            if (task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(VSettingsActivity.this, "Your Profile is Updated ", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                               String message = task.getException().toString();
                                Toast.makeText(VSettingsActivity.this, "Error Occured:" +message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

  /*  private void RetrieveUserInformation()
    {
        RootReference.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("pic"))))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStat = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfilePic = dataSnapshot.child("pic").getValue().toString();

                            Username.setText(retrieveUserName);
                            UserStat.setText(retrieveStat);
                            Picasso.get().load(retrieveProfilePic).into(UserProfileImage);

                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStat = dataSnapshot.child("status").getValue().toString();

                            Username.setText(retrieveUserName);
                            UserStat.setText(retrieveStat);


                        }
                        else
                        {
                            //Username.setVisibility(View.INVISIBLE);             // can be removed if you want user can change username anytime
                            Toast.makeText(VSettingsActivity.this, "Please Update your Profile Info.", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }*/

    private void RetrieveUserInformation()
    {
        RootReference.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("pic"))))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("pic").getValue().toString();

                            Username.setText(retrieveUserName);
                            UserStat.setText(retrievesStatus);
                            //Picasso.get().load(retrieveProfileImage).into(UserProfileImage);
                            Picasso.get().load(retrieveProfileImage).placeholder(R.drawable.profile_image).into(UserProfileImage);
                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("status").getValue().toString();

                            Username.setText(retrieveUserName);
                            UserStat.setText(retrievesStatus);
                        }
                        else
                        {
                            Username.setVisibility(View.VISIBLE);
                            Toast.makeText(VSettingsActivity.this, "Please set & update your profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    private void SendUserToMainActivity()
    {
        Intent mainIntent= new Intent(VSettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}