package com.company.polaris_bustrackingsystem.Adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.polaris_bustrackingsystem.EditRoute;
import com.company.polaris_bustrackingsystem.Models.RouteModel;
import com.company.polaris_bustrackingsystem.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class RouteAdapter extends FirebaseRecyclerAdapter<RouteModel,RouteAdapter.viewHolder> {


    public RouteAdapter(Query query){
        super(new FirebaseRecyclerOptions.Builder<RouteModel>()
                .setQuery(query,RouteModel.class)
                .build());
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull RouteModel model) {
        DataSnapshot snapshot = getSnapshots().getSnapshot(position);
        if (snapshot.exists()) {
            holder.bind(model, v -> {
                AlertDialog.Builder builder =new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Once deleted can't be undone");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getRef(position).removeValue();
                        notifyDataSetChanged();
                        Toast.makeText(holder.itemView.getContext(), "Route Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.itemView.getContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }, v -> {
                String routeKey = getRef(position).getKey();
                Intent intent = new Intent(holder.itemView.getContext(), EditRoute.class);
                intent.putExtra("routeKey",routeKey);
                holder.itemView.getContext().startActivity(intent);
                notifyItemChanged(position);
                notifyDataSetChanged();
            });
        } else {
            Log.e("TAG","Data Not Found");
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_card,parent,false);
        return new viewHolder(view);
    }

    public static class viewHolder extends RecyclerView.ViewHolder{

        Button btnRouteEdit, btnRouteDelete;
        TextView tvRouteNo, tvRoute;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            btnRouteEdit = itemView.findViewById(R.id.btnRouteEdit);
            btnRouteDelete = itemView.findViewById(R.id.btnRouteDelete);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvRouteNo = itemView.findViewById(R.id.tvRouteNo);
        }

        public void bind(RouteModel routeModel, View.OnClickListener deleteClickListener, View.OnClickListener editClickListener){
            tvRoute.setText(routeModel.getRouteName());
            String routeNo = "Route No: "+routeModel.getRouteNo();
            tvRouteNo.setText(routeNo);
            btnRouteDelete.setOnClickListener(deleteClickListener);
            btnRouteEdit.setOnClickListener(editClickListener);
        }
    }
}
