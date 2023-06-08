package com.company.polaris_bustrackingsystem.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.polaris_bustrackingsystem.AddBus;
import com.company.polaris_bustrackingsystem.Models.BusModel;
import com.company.polaris_bustrackingsystem.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BusAdapter extends FirebaseRecyclerAdapter<BusModel, BusAdapter.myViewHolder> {

    FirebaseDatabase database;
    DatabaseReference ref;
    public BusAdapter(@NonNull FirebaseRecyclerOptions<BusModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull BusModel model) {
    holder.BusNo.setText("Bus No. "+model.getBusno());
    holder.BusRoute.setText(model.getBusroute());
    holder.BusStop.setText(model.getStops());

    database = FirebaseDatabase.getInstance();
    ref = database.getReference();


    holder.butedit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String busKey = getRef(position).getKey();
            Intent intent = new Intent(holder.itemView.getContext(), AddBus.class);
            intent.putExtra("busKey",busKey);
            holder.itemView.getContext().startActivity(intent);
            notifyDataSetChanged();
        }
    });

        holder.butdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(holder.BusNo.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Once deleted can't be undone");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ref.child("Bus")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.BusNo.getContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

            }
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_card,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
       ImageView img;
        TextView BusNo,BusStop,BusRoute;
        Button butedit,butdelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.ivBus);
            BusNo = (TextView) itemView.findViewById(R.id.tvBusNo);
            BusRoute = (TextView) itemView.findViewById(R.id.tvBusRoute);
            BusStop = (TextView) itemView.findViewById(R.id.tvBusStops);
            butedit = (Button) itemView.findViewById(R.id.btnBusEdit);
            butdelete = (Button) itemView.findViewById(R.id.btnBusDelete);
        }
    }
}
