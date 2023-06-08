package com.company.polaris_bustrackingsystem.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.company.polaris_bustrackingsystem.AddRoutes;
import com.company.polaris_bustrackingsystem.EditStop;
import com.company.polaris_bustrackingsystem.Models.StopModel;
import com.company.polaris_bustrackingsystem.R;
import com.company.polaris_bustrackingsystem.SetStop;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class StopAdapter extends FirebaseRecyclerAdapter<StopModel,StopAdapter.viewHolder> {


    public StopAdapter(Query query){
        super(new FirebaseRecyclerOptions.Builder<StopModel>()
                .setQuery(query, StopModel.class)
                .build());
    }
    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull StopModel model) {

        DataSnapshot snapshot = getSnapshots().getSnapshot(position);
        DatabaseReference reference = getRef(position);

        if (snapshot.exists()) {
            holder.bind(model, v -> {
                reference.removeValue();
                notifyDataSetChanged();
            },

            v -> {
                Context context = holder.itemView.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.input_dialog_layout,null);
                builder.setView(dialogView);
                EditText etStopName = dialogView.findViewById(R.id.etStopNameAdd);
                //getting the value of Stop Name from database
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Object stopNameObject = snapshot.child("stopName").getValue();
                        if (stopNameObject != null) {
                            String stopName = stopNameObject.toString();
                            etStopName.setText(stopName);
                        } else {
                            etStopName.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String stopNameUpdated = etStopName.getText().toString();
                        if(stopNameUpdated.isEmpty()){
                            etStopName.setError("This field cannot be empty");
                            etStopName.requestFocus();
                            return;
                        }
                        String updatedStopName = etStopName.getText().toString();
                        reference.child("stopName").setValue(updatedStopName);

                        String stopKey = reference.getKey();
                        String routeKey = Objects.requireNonNull(reference.getParent().getParent()).getKey();
                        Intent intent = new Intent(holder.itemView.getContext(), EditStop.class);
                        intent.putExtra("stopKey",stopKey);
                        intent.putExtra("routeKey",routeKey);
                        holder.itemView.getContext().startActivity(intent);
                        notifyItemChanged(position);
                        notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            });
        } else {
            Log.e("TAG","Data Not Found");
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stop_card,parent,false);
        return new viewHolder(view);
    }

    public static class viewHolder extends RecyclerView.ViewHolder{

        Button btnEditStop, btnDeleteStop;
        TextView tvStopName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            btnDeleteStop = itemView.findViewById(R.id.btnDeleteStop);
            btnEditStop = itemView.findViewById(R.id.btnEditStop);
            tvStopName = itemView.findViewById(R.id.tvStopName);
        }

        public void bind(StopModel stopModel, View.OnClickListener deleteClickListener, View.OnClickListener editClickListener){
            tvStopName.setText(stopModel.getStopName());
            btnDeleteStop.setOnClickListener(deleteClickListener);
            btnEditStop.setOnClickListener(editClickListener);
        }
    }
}
