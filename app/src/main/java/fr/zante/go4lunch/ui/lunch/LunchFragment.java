package fr.zante.go4lunch.ui.lunch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.zante.go4lunch.databinding.FragmentLunchBinding;

public class LunchFragment extends Fragment {

    private FragmentLunchBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LunchViewModel lunchViewModel =
                new ViewModelProvider(this).get(LunchViewModel.class);

        binding = FragmentLunchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLunch;
        lunchViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
