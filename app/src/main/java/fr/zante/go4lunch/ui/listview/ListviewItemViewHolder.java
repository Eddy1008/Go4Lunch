package fr.zante.go4lunch.ui.listview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.zante.go4lunch.R;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.ui.restaurantdetail.RestaurantActivity;

public class ListviewItemViewHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView address;
    private TextView opening_info;
    private TextView distance;
    private TextView subscription_number;
    private TextView rating;
    private ImageView restaurant_photo;


    public ListviewItemViewHolder(@NonNull View itemView) {
        super(itemView);
        // TODO image
        name = itemView.findViewById(R.id.item_restaurant_textview_name);
        address = itemView.findViewById(R.id.item_restaurant_textview_address);
        opening_info = itemView.findViewById(R.id.item_restaurant_textview_opening_info);
        distance = itemView.findViewById(R.id.item_restaurant_textview_distance);
        subscription_number = itemView.findViewById(R.id.item_restaurant_textview_subscription_number);
        rating = itemView.findViewById(R.id.item_restaurant_textview_rating);
    }

    public void bind(RestaurantJson restaurant) {
        // TODO image
        name.setText(restaurant.getName());
        address.setText(restaurant.getVicinity());
        if (restaurant.getOpening_hours() == null) {
            opening_info.setText("non renseigné");
        } else {
            if (restaurant.getOpening_hours().isOpen_now()) {
                opening_info.setText("Ouvert");
            } else {
                opening_info.setText("Fermé");
            }
        }
        // TODO calculer la distance entre ma position et celle du restaurant
        distance.setText("2km");
        // TODO recupérer le nombre de collegues inscrits pour ce resaurant
        subscription_number.setText("3");
        // TODO recupérer la note d'evaluation du restaurant
        rating.setText("XX");
        // TODO
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RestaurantActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putSerializable("RESTAURANT_OBJECT", restaurant);
                intent.putExtra("BUNDLE_RESTAURANT_CLICKED", myBundle);
                view.getContext().startActivity(intent);
                //Toast.makeText(view.getContext(), "ouvrira la fiche detail du restaurant : " + restaurant.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
