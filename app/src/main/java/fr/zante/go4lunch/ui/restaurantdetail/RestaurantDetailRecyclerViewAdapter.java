package fr.zante.go4lunch.ui.restaurantdetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.zante.go4lunch.R;
import fr.zante.go4lunch.model.Member;

public class RestaurantDetailRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantDetailItemViewHolder>{

    private final List<Member> members;

    public RestaurantDetailRecyclerViewAdapter(List<Member> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public RestaurantDetailItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_workmates_joining, parent, false);
        return new RestaurantDetailItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantDetailItemViewHolder holder, int position) {
        holder.bind(members.get(position));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}
