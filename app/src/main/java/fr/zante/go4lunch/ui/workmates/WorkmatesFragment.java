package fr.zante.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.MembersViewModel;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class WorkmatesFragment extends Fragment {

    private FragmentWorkmatesBinding binding;

    private List<Member> members = new ArrayList<>();
    private String userName;
    private RecyclerView recyclerView;
    private WorkmatesRecyclerViewAdapter adapter;
    private MembersViewModel membersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.workmatesRecyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        membersViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(MembersViewModel.class);
        membersViewModel.initMembersList();
        userName = membersViewModel.getMyUserName();
        adapter = new WorkmatesRecyclerViewAdapter(this.members, this.userName);

        getMembers();
        initList();

        initSearch();

        return root;
    }

    private void getMembers() {
        membersViewModel.getMembers().observe(this.getViewLifecycleOwner(), members -> {
            this.members = new ArrayList<>(members);
            if (members != null) {
                adapter.updateMembers(this.members);
            }
        });
    }

    private void initSearch() {
        SearchView searchView = binding.workmatesSearchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<Member> filteredMemberList = new ArrayList<>();
                for (Member member : members) {
                    if (member.getName().toLowerCase().contains(s.toLowerCase())) {
                        filteredMemberList.add(member);
                    }
                }
                adapter = new WorkmatesRecyclerViewAdapter(filteredMemberList, userName);
                recyclerView.setAdapter(adapter);
                return false;
            }
        });
    }

    public void initList() {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}