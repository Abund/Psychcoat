package com.example.psychcoat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.psychcoat.MainActivity;
import com.example.psychcoat.R;
import com.example.psychcoat.adapter.AdapterChatList;
import com.example.psychcoat.model.BookingSession;
import com.example.psychcoat.model.ChatList;
import com.example.psychcoat.model.Chats;
import com.example.psychcoat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //firebase auth
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ChatList> chatlistList;
    List<User> userList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatList adapterChatlist;
    private ArrayList<String> bloodPressureKey;
    String timeStamp;

    public MessageListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageListFragment newInstance(String param1, String param2) {
        MessageListFragment fragment = new MessageListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_message_list, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        bloodPressureKey= new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);

        chatlistList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlistList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ChatList chatlist = ds.getValue(ChatList.class);
                    chatlistList.add(chatlist);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void loadChats() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    final User user = ds.getValue(User.class);
                    for (ChatList chatlist: chatlistList){
                        if (user.getUid() != null && user.getUid().equals(chatlist.getId())){
                            System.out.println("add--"+user.getUid());
                            DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("Bookings");
                            reference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //userList.clear();
                                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                                        BookingSession bookingSession = ds.getValue(BookingSession.class);
                                        bloodPressureKey.add(ds.getKey());
                                        //userList.add(user);
                                        if(bookingSession.getUserId().equals(user.getUid())){
                                            if(bookingSession.getStatus().equals("chatting")){
                                                System.out.println("add"+user.getUid());
                                                userList.add(user);
                                                timeStamp=bookingSession.getTimeStamp();
                                            }else {

                                            }

                                        }
                                    }
                                    //adapter
                                    adapterChatlist = new AdapterChatList(getContext(), userList,timeStamp);
                                    new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
                                    //setAdapter
                                    recyclerView.setAdapter(adapterChatlist);
                                    recyclerView.invalidate();
                                    //set last message
                                    for (int i=0; i<userList.size(); i++){
                                        lastMessage(userList.get(i).getUid());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Chats chat = ds.getValue(Chats.class);
                    if (chat==null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null){
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUser.getUid()) &&
                            chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) &&
                                    chat.getSender().equals(currentUser.getUid())){
                        //instead of displaying url in message show "sent photo"
                        if (chat.getType().equals("image")){
                            theLastMessage = "Sent a photo";
                        }
                        else {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }
                adapterChatlist.setLastMessageMap(userId, theLastMessage);
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            //set email of logged in user
            //mProfileTv.setText(user.getEmail());
        } else {
            //user not signed in, go to main acitivity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    /*inflate options menu*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.homescreen, menu);
        super.onCreateOptionsMenu(menu,inflater);
        menu.findItem(R.id.action_search).setVisible(true);
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchUser(query);
                }else {
                    loadChats();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())){
                    searchUser(newText);
                }else {
                    loadChats();
                }
                return false;
            }
        });
    }

    private void searchUser(final String query) {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    final User user = ds.getValue(User.class);
                    for (ChatList chatlist: chatlistList){
                        if (user.getUid() != null && user.getUid().equals(chatlist.getId())){
                            System.out.println("add--"+user.getUid());
                            DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("Bookings");
                            reference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //userList.clear();
                                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                                        BookingSession bookingSession = ds.getValue(BookingSession.class);
                                        bloodPressureKey.add(ds.getKey());
                                        //userList.add(user);
                                        if(bookingSession.getUserId().equals(user.getUid())){
                                            if(bookingSession.getStatus().equals("chatting")){

                                                if(user.getFirstName().toLowerCase().contains(query.toLowerCase())||
                                                        user.getLastName().toLowerCase().contains(query.toLowerCase())||
                                                        user.getEmail().toLowerCase().contains(query.toLowerCase())){

                                                    userList.add(user);
                                                    timeStamp=bookingSession.getTimeStamp();
                                                }
                                            }else {

                                            }

                                        }
                                    }
                                    //adapter
                                    adapterChatlist = new AdapterChatList(getContext(), userList,timeStamp);
                                    new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
                                    //setAdapter
                                    recyclerView.setAdapter(adapterChatlist);
                                    recyclerView.invalidate();
                                    //set last message
                                    for (int i=0; i<userList.size(); i++){
                                        lastMessage(userList.get(i).getUid());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*handle menu item clicks*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            firebaseAuth.signOut();
            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }

    ItemTouchHelper.SimpleCallback simpleCallback= new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Bookings")
                    .child(bloodPressureKey.get(viewHolder.getAdapterPosition()));

            Log.e("eee2",""+viewHolder.getAdapterPosition());
            HashMap<String,Object> hashMap= new HashMap<>();
            hashMap.put("status", "deleted");

            databaseReference.getRef().updateChildren(hashMap);
            loadChats();
        }
    };

}