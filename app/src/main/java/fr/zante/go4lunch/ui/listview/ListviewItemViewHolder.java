package fr.zante.go4lunch.ui.listview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import fr.zante.go4lunch.BuildConfig;
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
        restaurant_photo = itemView.findViewById(R.id.item_restaurant_photo);
        name = itemView.findViewById(R.id.item_restaurant_textview_name);
        address = itemView.findViewById(R.id.item_restaurant_textview_address);
        opening_info = itemView.findViewById(R.id.item_restaurant_textview_opening_info);
        distance = itemView.findViewById(R.id.item_restaurant_textview_distance);
        subscription_number = itemView.findViewById(R.id.item_restaurant_textview_subscription_number);
        rating = itemView.findViewById(R.id.item_restaurant_textview_rating);
    }

    public void bind(RestaurantJson restaurant, double lat, double lng, String userId) {
        if (restaurant.getPhotos() != null) {
            String myBasePhotoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=";
            String apiKey = "&key=" + BuildConfig.MAPS_API_KEY;
            String myPhotoURL = myBasePhotoURL + restaurant.getPhotos().get(0).getPhoto_reference() + apiKey;
            Glide.with(this.restaurant_photo.getContext())
                    .load(myPhotoURL)
                    .apply(RequestOptions.circleCropTransform())
                    .into(restaurant_photo);
        }
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
        int myDistanceToRestaurant = (int) getDistanceToRestaurant(restaurant, lat, lng);
        String myDistance = myDistanceToRestaurant + "m";
        distance.setText(myDistance);

        // TODO recupérer le nombre de collegues inscrits pour ce resaurant : onSnapshotListener
        subscription_number.setText("3");

        // TODO recupérer la note d'evaluation du restaurant
        rating.setText("XX");
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RestaurantActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("RESTAURANT_PLACE_ID", restaurant.getPlace_id());
                myBundle.putString("USER_ID", userId);
                intent.putExtra("BUNDLE_RESTAURANT_SELECTED", myBundle);
                view.getContext().startActivity(intent);
            }
        });
    }

    private static double getDistanceToRestaurant(RestaurantJson restaurant, double myLat, double myLng) {
        if ((restaurant.getGeometry().getLocation().getLat() == myLat)
                && (restaurant.getGeometry().getLocation().getLng() == myLng)) {
            return 0;
        } else {
            double theta = restaurant.getGeometry().getLocation().getLng() - myLng;
            double dist = Math.sin(Math.toRadians(restaurant.getGeometry().getLocation().getLat())) * Math.sin(Math.toRadians(myLat))
                    + Math.cos(Math.toRadians(restaurant.getGeometry().getLocation().getLat())) * Math.cos(Math.toRadians(myLat)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344 * 1000;
            return dist;
        }
    }
}
