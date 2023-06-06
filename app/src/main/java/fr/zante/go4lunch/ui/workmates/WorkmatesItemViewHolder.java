package fr.zante.go4lunch.ui.workmates;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import fr.zante.go4lunch.R;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.restaurantdetail.RestaurantActivity;

public class WorkmatesItemViewHolder extends RecyclerView.ViewHolder{

    private ImageView memberPhoto;
    private TextView info;

    public WorkmatesItemViewHolder(@NonNull View itemView) {
        super(itemView);
        memberPhoto = itemView.findViewById(R.id.item_workmates_photo);
        info = itemView.findViewById(R.id.item_workmates_info);
    }

    public void bind(Member member) {
        String toDisplay = member.getName() + " is eating here: " + member.getSelectedRestaurantName();
        // TODO recuperer url !!
        Glide.with(this.memberPhoto.getContext())
                .load(member.getAvatarUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(memberPhoto);
        info.setText(toDisplay);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RestaurantActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("RESTAURANT_PLACE_ID", member.getSelectedRestaurantId());
                myBundle.putString("USER_NAME", member.getName());
                intent.putExtra("BUNDLE_RESTAURANT_SELECTED", myBundle);
                view.getContext().startActivity(intent);
            }
        });
    }
}
