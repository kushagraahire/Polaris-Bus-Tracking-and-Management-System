package com.company.polarisstudent;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private BusAdapter busAdapter;
    private ProgressDialog progressDialog;
    private SearchView svBus;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        busAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        svBus = view.findViewById(R.id.svBus);
        database = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Bus");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Buses...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseRecyclerOptions<BusModel> options =
                new FirebaseRecyclerOptions.Builder<BusModel>()
                        .setQuery(reference, BusModel.class)
                        .build();

        busAdapter = new BusAdapter(options);
        busAdapter.startListening();
        recyclerView.setAdapter(busAdapter);

        busAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                progressDialog.dismiss();
            }
        });

        svBus.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    private void searchText(String str) {
        Query query = reference.orderByChild("busroute").startAt(str).endAt(str+"\uf8ff");

        FirebaseRecyclerOptions<BusModel> options =
                new FirebaseRecyclerOptions.Builder<BusModel>()
                        .setLifecycleOwner(this)
                        .setQuery(query, BusModel.class)
                        .build();

        busAdapter.updateOptions(options);
        busAdapter.startListening();
        recyclerView.setAdapter(busAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        busAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        busAdapter.stopListening();
    }
}