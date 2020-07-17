package com.fromapps.voizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
//import android.widget.Toolbar;

public class FindPeopleActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private RecyclerView findPeopleRecyclerList;
    private DatabaseReference UserRefernce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        UserRefernce = FirebaseDatabase.getInstance().getReference().child("Users");

        findPeopleRecyclerList = (RecyclerView) findViewById(R.id.find_people_recycler_list);
        findPeopleRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.find_people_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);          //    for back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);          //    for back button
        getSupportActionBar().setTitle("Find People");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<VContacts> options =
                new FirebaseRecyclerOptions.Builder<VContacts>()
                .setQuery(UserRefernce, VContacts.class)
                .build();

        FirebaseRecyclerAdapter<VContacts, FindPeopleViewHolder> adapter =
                new FirebaseRecyclerAdapter<VContacts, FindPeopleViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindPeopleViewHolder holder, final int position, @NonNull VContacts model)
                    {
                        holder.userName.setText(model.getName());
                        holder.userStat.setText(model.getStatus());
                        Picasso.get().load(model.getPic()).placeholder(R.drawable.profile_image).into(holder.profilePic);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String visit_user_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(FindPeopleActivity.this, VProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindPeopleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        FindPeopleViewHolder viewHolder = new FindPeopleViewHolder(view);
                        return viewHolder;
                    }
                };

        findPeopleRecyclerList.setAdapter(adapter);

        adapter.startListening();
    }

    public static class FindPeopleViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStat;
        CircleImageView profilePic;

        public FindPeopleViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStat = itemView.findViewById(R.id.user_status);
            profilePic = itemView.findViewById(R.id.user_profile_pic);
        }
    }
}