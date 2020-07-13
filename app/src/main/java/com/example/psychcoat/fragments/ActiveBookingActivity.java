package com.example.psychcoat.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.psychcoat.R;
import com.example.psychcoat.adapter.AdapterUser;
import com.example.psychcoat.model.BookingSession;
import com.example.psychcoat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActiveBookingActivity extends Fragment {

    RecyclerView recyclerView;
    List<User> userList;
    FirebaseAuth firebaseAuth;
    AdapterUser adapterUser;
    BookingSession user1 = new BookingSession();

    View view;

    public ActiveBookingActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active_booking_activity, container, false);

        super.onCreate(savedInstanceState);
        recyclerView= view.findViewById(R.id.active_recyclerView);
        firebaseAuth =FirebaseAuth.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userList= new ArrayList<>();
        getAllUsers();
        return view;
    }

    private void searchUser(final String query) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Bookings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){

                    user1 = ds.getValue(BookingSession.class);
                    if(!user1.getPsychologistId().equals(firebaseUser.getUid())){
                        //userList.add(user);

                        if(!user1.getStatus().equals("Booked")){
                            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    userList.clear();
                                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                                        User user = new User();
                                        user = ds.getValue(User.class);
                                        if(!user.getUid().equals(user1.getUserId())){
                                            if(user.getFirstName().toLowerCase().contains(query.toLowerCase())||
                                                    user.getLastName().toLowerCase().contains(query.toLowerCase())||
                                                    user.getEmail().toLowerCase().contains(query.toLowerCase())){

                                                userList.add(user);
                                            }
                                        }

                                        adapterUser = new AdapterUser(getActivity(),userList,user1);
                                        recyclerView.setAdapter(adapterUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Bookings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){

                    user1 = ds.getValue(BookingSession.class);
                    if(user1.getPsychologistId().equals(firebaseUser.getUid())){
                        //userList.add(user);

                        if(user1.getStatus().equals("Booked")){
                            final FirebaseUser firebaseUser1 = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("Users");
                            reference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    userList.clear();
                                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                                        //User user = new User();
                                        User user = ds.getValue(User.class);
                                        if(user.getUid().equals(user1.getUserId())){
                                            userList.add(user);
                                        }

                                        adapterUser = new AdapterUser(getActivity(),userList,user1);
                                        recyclerView.setAdapter(adapterUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.homescreen, menu);

        //menu.findItem(R.id.action_add_post).setVisible(false);
        MenuItem item = menu.findItem(R.id.action_search);
//        menu.findItem(R.id.action_add_post).setVisible(false);
//        menu.findItem(R.id.action_add_participant).setVisible(false);
//        menu.findItem(R.id.action_groupinfo).setVisible(false);
//        menu.findItem(R.id.action_create_group).setVisible(false);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchUser(query);
                }else {
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchUser(newText);
                }else {
                    getAllUsers();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

}