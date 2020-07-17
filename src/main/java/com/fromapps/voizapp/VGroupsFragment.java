package com.fromapps.voizapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VGroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VGroupsFragment extends Fragment
{
    private View FragmentGroupView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String > listOfGroups = new ArrayList<>();

    private DatabaseReference GroupReference;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VGroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VGroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VGroupsFragment newInstance(String param1, String param2) {
        VGroupsFragment fragment = new VGroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentGroupView = inflater.inflate(R.layout.fragment_v_groups, container, false);

        GroupReference = FirebaseDatabase.getInstance().getReference().child("Groups");

        InitializeFields();

        RetrieveAndShowGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int group_position, long group_id)
            {
                String currentGroupName = adapterView.getItemAtPosition(group_position).toString();
                Intent groupTalkIntent = new Intent(getContext(), VGroupTalkActivity.class);
                groupTalkIntent.putExtra("GroupName", currentGroupName);
                startActivity(groupTalkIntent);
            }
        });

        return FragmentGroupView;

    }



    private void InitializeFields()
    {
        listView = (ListView) FragmentGroupView.findViewById(R.id.list__view);
        arrayAdapter = new ArrayAdapter<String >(getContext(), android.R.layout.simple_list_item_1, listOfGroups);
        listView.setAdapter(arrayAdapter);

    }

    private void RetrieveAndShowGroups()
    {
        GroupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                listOfGroups.clear();
                listOfGroups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}