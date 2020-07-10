package com.example.psychcoat.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.psychcoat.ForgetPasswordActivity;
import com.example.psychcoat.R;
import com.example.psychcoat.adapter.AdapterChatList;
import com.example.psychcoat.adapter.AdapterUser;
import com.example.psychcoat.model.BookingSession;
import com.example.psychcoat.model.ChatList;
import com.example.psychcoat.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HomeFragment extends Fragment {

    Button updateProfile,noOfActiveBookings,noOfActiveChats;


    //firebase auth
    FirebaseAuth firebaseAuth;
    BookingSession user1 = new BookingSession();
    List<BookingSession> bookingSessionList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    int num;
    int sum;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        updateProfile = view.findViewById(R.id.updateProfile);
        noOfActiveBookings = view.findViewById(R.id.noOfActiveBookings);
        noOfActiveChats = view.findViewById(R.id.noOfActiveChats);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Bookings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingSessionList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){

                    user1 = ds.getValue(BookingSession.class);
                    if(!user1.getPsychologistId().equals(firebaseUser.getUid())){
                        if(!user1.getStatus().equals("Booked")){
                            bookingSessionList.add(user1);
                        }
                    }
                }
                num=bookingSessionList.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final FirebaseUser firebaseUser1 = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("Bookings");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingSessionList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){

                    user1 = ds.getValue(BookingSession.class);
                    if(!user1.getPsychologistId().equals(firebaseUser1.getUid())){
                        if(!user1.getStatus().equals("Chatting")){
                            bookingSessionList.add(user1);
                        }
                    }
                }
                sum=bookingSessionList.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        noOfActiveBookings.setText(""+num);
        noOfActiveChats.setText(""+sum);

        updateProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment newFragment =  new ProfileFragment();
                replaceFragment(newFragment);
            }
        });

        noOfActiveBookings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment newFragment =  new ActiveBookingActivity();
                replaceFragment(newFragment);
            }
        });

        noOfActiveChats.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment newFragment =  new MessageListFragment();
                replaceFragment(newFragment);
            }
        });

        return view;
    }

    public void replaceFragment(Fragment destFragment)
    {
        // First get FragmentManager object.
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Begin Fragment transaction.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the layout holder with the required Fragment object.
        fragmentTransaction.replace(R.id.content_frame, destFragment);

        // Commit the Fragment replace action.
        fragmentTransaction.addToBackStack(null).commit();
    }
}