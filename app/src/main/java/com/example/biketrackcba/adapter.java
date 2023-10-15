package com.example.biketrackcba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import java.util.ArrayList;

public class adapter extends RecyclerView.Adapter<adapter.MyViewHolder>{

//TONG CLASS NA TO IS PARA SA ADAPTER NI ROUTES SCREEN
    Context context;
    ArrayList<routesinfo> list;

    public adapter(Context context, ArrayList<routesinfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.displayroutes_design,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        routesinfo user = list.get(position);
        holder.desc.setText(user.getDesc());
        holder.points.setText(user.getPoints());
        holder.title.setText(user.getTitle());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView desc, points, title;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            desc=itemView.findViewById(R.id.tvdesc);
            points=itemView.findViewById(R.id.tvpoints);
            title=itemView.findViewById(R.id.tvtitle);

        }
    }

}
