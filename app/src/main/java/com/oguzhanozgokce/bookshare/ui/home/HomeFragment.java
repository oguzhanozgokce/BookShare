package com.oguzhanozgokce.bookshare.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.oguzhanozgokce.bookshare.R;
import com.oguzhanozgokce.bookshare.data.model.ListingType;
import com.oguzhanozgokce.bookshare.databinding.FragmentHomeBinding;

import java.util.Objects;

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

        setupRecyclerView();
        setupTabLayout();
        observeViewModel();

        Objects.requireNonNull(binding.tabLayout.getTabAt(0)).select();
        viewModel.loadListingsByType(ListingType.FOR_SALE);
    }

    private void setupRecyclerView() {
        adapter = new BookListingAdapter(
                listing -> {
                    // Detail sayfasına git
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("listing", listing);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_homeFragment_to_detailFragment, bundle);
                },
                (listing, position) -> {
                    // Save/unsave işlemi
                    viewModel.toggleSaveListing(listing.getId());
                }
        );
        binding.recyclerListings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerListings.setAdapter(adapter);
    }

    private void setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Satın Al
                        viewModel.loadListingsByType(ListingType.FOR_SALE);
                        break;
                    case 1: // Ödünç Al
                        viewModel.loadListingsByType(ListingType.FOR_RENT);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            if (uiState.isLoading()) {
                binding.recyclerListings.setVisibility(View.GONE);
            } else if (uiState.getError() != null) {
                binding.recyclerListings.setVisibility(View.VISIBLE);
            } else if (uiState.getListings() != null) {
                binding.recyclerListings.setVisibility(View.VISIBLE);
                adapter.submitList(uiState.getListings());
                adapter.updateSavedListings(uiState.getSavedListingIds());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
