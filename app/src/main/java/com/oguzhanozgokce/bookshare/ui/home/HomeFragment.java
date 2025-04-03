package com.oguzhanozgokce.bookshare.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oguzhanozgokce.bookshare.R;
import com.oguzhanozgokce.bookshare.databinding.FragmentHomeBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private BookListingAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        adapter = new BookListingAdapter(listing -> {
            // TODO: item click action
        });
        binding.recyclerListings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerListings.setAdapter(adapter);

        viewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            if (uiState.isLoading()) {
                // TODO: loading göster
            } else if (uiState.getError() != null) {
                // TODO: hata mesajı göster (Snackbar, Toast vs.)
            } else if (uiState.getListings() != null) {
                adapter.submitList(uiState.getListings());
            }
        });
        viewModel.loadAllListings();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}