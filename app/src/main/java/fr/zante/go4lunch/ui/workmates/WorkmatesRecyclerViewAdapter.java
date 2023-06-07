package fr.zante.go4lunch.ui.workmates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.zante.go4lunch.R;
import fr.zante.go4lunch.model.Member;

public class WorkmatesRecyclerViewAdapter extends RecyclerView.Adapter<WorkmatesItemViewHolder> {

    private List<Member> members;

    public WorkmatesRecyclerViewAdapter(List<Member> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public WorkmatesItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_workmates, parent, false);
        return new WorkmatesItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesItemViewHolder holder, int position) {
        holder.bind(members.get(position));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void updateMembers(List<Member> members) {
        this.members = members;
        this.notifyDataSetChanged();
    }
}
