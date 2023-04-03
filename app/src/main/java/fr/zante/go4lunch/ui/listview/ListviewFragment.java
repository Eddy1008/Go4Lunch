package fr.zante.go4lunch.ui.listview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.zante.go4lunch.databinding.FragmentListviewBinding;

public class ListviewFragment extends Fragment {

    private FragmentListviewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ListviewViewModel listviewViewModel =
                new ViewModelProvider(this).get(ListviewViewModel.class);

        binding = FragmentListviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textListview;
        listviewViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}