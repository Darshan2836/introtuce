package com.darshanudagire.introtuce;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darshanudagire.introtuce.adapter.AllUsersAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Users extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.recyclerView_id)
     RecyclerView recyclerView;
    @BindView(R.id.noUsersTxt_id)
    AppCompatTextView noUsersTxt;

    private String mParam1;
    private String mParam2;

    private View parentHolder;

    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private List<HashMap> allUsersList;



    public Users() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);
    }



    public static Users newInstance(String param1, String param2) {
        Users fragment = new Users();
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
        // Inflate the layout for this fragment
        parentHolder =  inflater.inflate(R.layout.fragment_users, container, false);

        ButterKnife.bind(this,parentHolder);

        //firebase instance
        db = FirebaseFirestore.getInstance();

        //progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading data from firebase...!!");

        //list
        allUsersList = new ArrayList<>();

        getDataFromFirebase();
        

        return parentHolder;
    }

    private void getDataFromFirebase() {
        progressDialog.show();
        db.collection("Users").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot documentSnapshot : task.getResult())
                    {
                        HashMap hashMap = (HashMap) documentSnapshot.getData();
                        allUsersList.add(hashMap);
                    }
                    setUpRecyclerView();
                }
            }
        });
    }

    private void setUpRecyclerView() {
        AllUsersAdapter allUsersAdapter = new AllUsersAdapter(allUsersList,getContext());
        recyclerView.setHasFixedSize(false);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(allUsersAdapter);

        progressDialog.dismiss();

        if (allUsersList.size() == 0){
            noUsersTxt.setVisibility(View.VISIBLE);
        }
        else
        {
            noUsersTxt.setVisibility(View.INVISIBLE);
        }
    }


}