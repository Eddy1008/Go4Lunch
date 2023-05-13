package fr.zante.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.zante.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.zante.go4lunch.model.Member;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;
    private RecyclerView recyclerView;
    List<Member> members;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.workmatesRecyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        initList();

        return root;
    }

    private void initList() {
        members = Arrays.asList(
                new Member("0","eddy", "abcdef0@test.com", "blabla", "McDo", new ArrayList<>()),
                new Member("1","melody", "abcdef1@test.com","blabla", "Flunch", new ArrayList<>()),
                new Member("2","ethan", "abcdef2@test.com","blabla", "Quick", new ArrayList<>()),
                new Member("3","cassandre", "abcdef3@test.com","blabla", "BK", new ArrayList<>())
        );
        recyclerView.setAdapter(new WorkmatesRecyclerViewAdapter(members));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}