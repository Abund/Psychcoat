package com.example.psychcoat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class HomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private TextView clientName,profileName;
    private ImageView imageViewProfile;
    FirebaseAuth firebaseAuth;
    boolean closeApp;
    private int TAKE_IMAGE_CODE=10001;
    private static  final  int PICK_IMAGE=1;
    //storage
    StorageReference storageReference;
    //path where images of user profile and cover will be stored
    String storagePath = "Users_Profile_Cover_Imgs/";
    private  static final String TAG="HomeScreenActivity";

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    String mUID;
    FirebaseUser user;

    ActionBar actionBar;


    //progress dialog
    ProgressDialog progressDialog;


    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    //arrays of permissions to be requested
    String cameraPermissions[];
    String storagePermissions[];

    //uri of picked image
    Uri image_uri;

    //for checking profile or cover photo
    String profileOrCoverPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Psychologist");
        storageReference = getInstance().getReference(); //firebase storage reference

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        profileName = navigationView.getHeaderView(0).findViewById(R.id.profileName);
        imageViewProfile = navigationView.getHeaderView(0).findViewById(R.id.imageViewProfile);
        //imageViewHomePageProfile = (ImageView) findViewById(R.id.imageViewHomePageProfile);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            // Handle the camera action

            Fragment newFragment =  new HomeFragment();
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.add(R.id.content_frame, newFragment).commit();
            replaceFragment(newFragment);
        }  if (id == R.id.sign_out) {

            firebaseAuth = FirebaseAuth.getInstance();
            //LoginManager.getInstance().logOut();
            firebaseAuth.signOut();
            Intent at = new Intent(HomeScreen.this, MainActivity.class);
            startActivity(at);
        }
//
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment destFragment)
    {
        // First get FragmentManager object.
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        // Begin Fragment transaction.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the layout holder with the required Fragment object.
        fragmentTransaction.replace(R.id.content_frame, destFragment);

        // Commit the Fragment replace action.
        fragmentTransaction.addToBackStack(null).commit();
    }
}