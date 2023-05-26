package fr.zante.go4lunch.ui.restaurantdetail;

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

public class RestaurantDetailItemViewHolder extends RecyclerView.ViewHolder{

    private ImageView memberPhoto;
    private TextView info;

    public RestaurantDetailItemViewHolder(@NonNull View itemView) {
        super(itemView);
        memberPhoto = itemView.findViewById(R.id.item_workmates_joining_photo);
        info = itemView.findViewById(R.id.item_workmates_joining_name);
    }

    public void bind(Member member) {
        String stringToDisplay = member.getName() + " is joining!";
        // TODO recuperer url pour affichage du portrait!
        Glide.with(this.memberPhoto.getContext())
                .load(member.getAvatarUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(memberPhoto);
        info.setText(stringToDisplay);
    }
}
