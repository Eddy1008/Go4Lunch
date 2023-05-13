package fr.zante.go4lunch.ui.workmates;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.zante.go4lunch.R;
import fr.zante.go4lunch.model.Member;

public class WorkmatesItemViewHolder extends RecyclerView.ViewHolder{

    private TextView name;
    private TextView restaurantName;

    public WorkmatesItemViewHolder(@NonNull View itemView) {
        super(itemView);
        // TODO
        name = itemView.findViewById(R.id.item_workmates_name);
        restaurantName = itemView.findViewById(R.id.item_workmates_restaurant_name);
    }

    public void bind(Member member) {
        name.setText(member.getName());
        restaurantName.setText(member.getSelectedRestaurantId());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Show workmate selected restaurant
                Toast.makeText(view.getContext(), "ouvrira le detail du restaurant choisi par ce collegue", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
