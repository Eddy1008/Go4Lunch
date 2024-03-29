package fr.zante.go4lunch.ui.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.zante.go4lunch.R;
import fr.zante.go4lunch.model.RestaurantJson;

public class ListviewRecyclerViewAdapter extends RecyclerView.Adapter<ListviewItemViewHolder>{

    private List<RestaurantJson> restaurants;
    private List<Integer> restaurantsMembersNumber;
    private final double myLat;
    private final double myLng;
    private final String userName;

    public ListviewRecyclerViewAdapter(List<RestaurantJson> items, double lat, double lng, String name, List<Integer> integerList) {
        restaurants = items;
        myLat = lat;
        myLng = lng;
        userName = name;
        restaurantsMembersNumber = integerList;
    }

    @NonNull
    @Override
    public ListviewItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_restaurant, parent, false);
        return new ListviewItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListviewItemViewHolder holder, int position) {
        holder.bind(restaurants.get(position), myLat, myLng, userName, restaurantsMembersNumber.get(position));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void updateRestaurants(final List<RestaurantJson> restaurants, List<Integer> integerList) {
        this.restaurants = restaurants;
        this.restaurantsMembersNumber = integerList;
        this.notifyDataSetChanged();
    }
}
