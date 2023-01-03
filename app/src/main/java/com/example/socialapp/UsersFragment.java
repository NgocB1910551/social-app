package com.example.socialapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.SearchView;

import com.example.socialapp.adapters.AdapterUser;
import com.example.socialapp.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    AdapterUser adapterUser;
    List<ModelUser> userList;

    FirebaseAuth firebaseAuth;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        //init recyclerview
        recyclerView = view.findViewById(R.id.users_recyclerView);
        //set it's properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init user list
        userList = new ArrayList<>();

        //get All users
        getAllUsers();

        return view;
    }

    private void getAllUsers() {
        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database name "Users"
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all users expect currently signed in user
                    if (!modelUser.getUid().equals(fUser.getUid())){
                        userList.add(modelUser);
                    }
                    //adapter
                    adapterUser = new AdapterUser(getActivity(), userList);
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void searchUsers(String query) {
        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database name "Users"
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all search users expect currently signed in user
                    if (!modelUser.getUid().equals(fUser.getUid())){
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) || modelUser.getEmail()
                            .toLowerCase().contains(query.toLowerCase()))
                        {
                            userList.add(modelUser);
                        }


                    }
                    //adapter
                    adapterUser = new AdapterUser(getActivity(), userList);
                    //refresh adapter
                    adapterUser.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public  void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            //set email for of logged in user
            //mProfileTv.setText(user.getEmail());
        }
        else {
            //user not signed in, go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    /*inflate options menu*/
    @SuppressWarnings("deprecation")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating enu
        inflater.inflate(R.menu.menu_main,menu);

        //hide add post icon from this fragment
        menu.findItem(R.id.action_add_post).setVisible(false);

        //searchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when user press search button from keyboard
                //if search query is not empty then search
                if (!TextUtils.isEmpty(query.trim())){
                    //search text contains text, search it
                    searchUsers(query);
                }
                else {
                    //search text empty get all user
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!TextUtils.isEmpty(newText.trim())){
                    //search text contains text, search it
                    searchUsers(newText);
                }
                else {
                    //search text empty get all user
                    getAllUsers();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }



    /*handle menu item click*/

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}