package com.imastudio.ojekonlineuserjuly.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imastudio.ojekonlineuserjuly.DetaillOrderActivity;
import com.imastudio.ojekonlineuserjuly.R;
import com.imastudio.ojekonlineuserjuly.helper.MyContants;
import com.imastudio.ojekonlineuserjuly.model.DataHistory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.MyVieHolder> {
    List<DataHistory> dataHistory;
    FragmentActivity activity;
        int i;

    public CustomRecyclerAdapter(List<DataHistory> dataHistory, FragmentActivity activity, int i) {
        this.dataHistory = dataHistory;
        this.activity = activity;
        this.i=i;
    }

    @NonNull
    @Override
    public MyVieHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View     view = LayoutInflater.from(activity).inflate(R.layout.custom_recyclerview, viewGroup, false);

        return new MyVieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVieHolder myVieHolder, final int position) {
        myVieHolder.texttgl.setText(dataHistory.get(position).getBookingTanggal());
        myVieHolder.txtawal.setText(dataHistory.get(position).getBookingFrom());
        myVieHolder.txtakhir.setText(dataHistory.get(position).getBookingTujuan());
        myVieHolder.txtharga.setText(dataHistory.get(position).getBookingBiayaUser());
        myVieHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i==2){

                    Intent intent = new Intent(activity, DetaillOrderActivity.class);
                    intent.putExtra(MyContants.INDEX,position);
                    intent.putExtra(MyContants.STATUS,i);
                    activity.startActivity(intent);
                }
                else{
                    Intent intent = new Intent(activity,DetaillOrderActivity.class);
                    intent.putExtra(MyContants.INDEX,position);
                    intent.putExtra(MyContants.STATUS,i);
                    activity.startActivity(intent);

                }
               }
        });
    }

    @Override
    public int getItemCount() {
        return dataHistory.size();
    }

    public class MyVieHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.texttgl)
        TextView texttgl;
        @BindView(R.id.txtawal)
        TextView txtawal;
        @BindView(R.id.txtakhir)
        TextView txtakhir;
        @BindView(R.id.txtharga)
        TextView txtharga;
        public MyVieHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
