package com.example.inframedic.ui;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inframedic.Issue;
import com.example.inframedic.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Issue> issueList;
    private Context context;


    public RecyclerViewAdapter(List<Issue> issueList, Context context) {
        this.issueList = issueList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.issue_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Issue issue = issueList.get(position);
        String name = issue.getHospitalName();
        String address = issue.getHospitalAddress();
        String imageUrl = issue.getImageUrl();

        holder.hospitalName.setText(name);
        holder.hospitalAddress.setText(address);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.img)
                .fit()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView hospitalName;
        private TextView hospitalAddress;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            imageView = itemView.findViewById(R.id.issue_row_image);
            hospitalName = itemView.findViewById(R.id.issue_row_hospital_name_textView);
            hospitalAddress = itemView.findViewById(R.id.issue_row_address_textView);
        }
    }
}
