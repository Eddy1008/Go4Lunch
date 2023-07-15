package fr.zante.go4lunch.ui.workmates;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import fr.zante.go4lunch.R;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.restaurantdetail.RestaurantActivity;

public class WorkmatesItemViewHolder extends RecyclerView.ViewHolder{

    private final ImageView memberPhoto;
    private final TextView info;

    public WorkmatesItemViewHolder(@NonNull View itemView) {
        super(itemView);
        memberPhoto = itemView.findViewById(R.id.item_workmates_photo);
        info = itemView.findViewById(R.id.item_workmates_info);
    }

    public void bind(Member member, String userName) {
        String toDisplay;
        if (member.getSelectedRestaurantName().equals("")) {
            info.setTypeface(null, Typeface.ITALIC);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                info.setTextColor(itemView.getContext().getColor(R.color.light_gray));
            }
            toDisplay = member.getName() + " " + itemView.getContext().getString(R.string.workmates_item_view_holder_no_choice);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), itemView.getContext().getString(R.string.workmates_item_view_holder_no_choice_toast), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            info.setTypeface(null, Typeface.BOLD);
            toDisplay = member.getName() + " " + itemView.getContext().getString(R.string.workmates_item_view_holder_choice_made) + " " + member.getSelectedRestaurantName();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), RestaurantActivity.class);
                    Bundle myBundle = new Bundle();
                    myBundle.putString("RESTAURANT_PLACE_ID", member.getSelectedRestaurantId());
                    myBundle.putString("USER_NAME", userName);
                    intent.putExtra("BUNDLE_RESTAURANT_SELECTED", myBundle);
                    view.getContext().startActivity(intent);
                }
            });
        }
        Glide.with(this.memberPhoto.getContext())
                .load(member.getAvatarUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(memberPhoto);
        info.setText(toDisplay);
    }
}
