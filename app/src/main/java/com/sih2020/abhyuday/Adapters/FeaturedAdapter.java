package com.sih2020.abhyuday.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sih2020.abhyuday.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeatureViewHolder> {
    ArrayList<FeaturedHelperClass> featuredHelperClasses;

    public FeaturedAdapter(ArrayList<FeaturedHelperClass> featuredHelperClasses) {
        this.featuredHelperClasses = featuredHelperClasses;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_card_design,parent,false);
        FeatureViewHolder featureViewHolder= new FeatureViewHolder(view);
        return featureViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {

        FeaturedHelperClass featuredHelperClass=featuredHelperClasses.get(position);
        //holder.image.setImageResource(featuredHelperClass.getImage());
        Picasso.get().load(featuredHelperClass.getImage()).into(holder.image);
        holder.title.setText(featuredHelperClass.getTitle());
        holder.desc.setText(featuredHelperClass.getDescription());

    }

    @Override
    public int getItemCount() {
        return featuredHelperClasses.size();
    }

    public static class FeatureViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title,desc;


        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.featured_image);
            title=itemView.findViewById(R.id.featured_title);
            desc=itemView.findViewById(R.id.featured_desc);


        }
    }
}
