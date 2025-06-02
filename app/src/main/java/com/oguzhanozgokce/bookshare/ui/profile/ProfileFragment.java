package com.oguzhanozgokce.bookshare.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.oguzhanozgokce.bookshare.R;
import com.oguzhanozgokce.bookshare.databinding.FragmentProfileBinding;
import com.oguzhanozgokce.bookshare.ui.profile.adapter.TransactionBooksAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private TransactionBooksAdapter borrowedBooksAdapter;
    private TransactionBooksAdapter purchasedBooksAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        setupRecyclerViews();
        setupListeners();
        observeViewModel();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerViews() {
        // Borrowed books adapter
        borrowedBooksAdapter = new TransactionBooksAdapter(true); // true for borrowed books
        binding.recyclerBorrowedBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerBorrowedBooks.setAdapter(borrowedBooksAdapter);

        // Purchased books adapter
        purchasedBooksAdapter = new TransactionBooksAdapter(false); // false for purchased books
        binding.recyclerPurchasedBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerPurchasedBooks.setAdapter(purchasedBooksAdapter);
    }

    private void setupListeners() {
        // Logout button
        binding.buttonLogout.setOnClickListener(v -> {
            viewModel.logout();
        });

        // View all borrowed books
        binding.textViewAllBorrowed.setOnClickListener(v -> {
            // Navigate to borrowed books history
            // TODO: Implement navigation to borrowed books history
        });

        // View all purchased books
        binding.textViewAllPurchased.setOnClickListener(v -> {
            // Navigate to purchased books history
            // TODO: Implement navigation to purchased books history
        });
    }

    private void observeViewModel() {
        viewModel.state.observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(ProfileViewModel.ProfileState state) {
        // Handle loading state
        if (state.isLoading()) {
            showLoading(true);
        } else {
            showLoading(false);
        }

        // Handle logout
        if (state.isLoggedOut()) {
            handleLogout();
            return;
        }

        // Update user info
        updateUserInfo(state);

        // Update statistics
        updateStatistics(state);

        // Update transaction lists
        updateTransactionLists(state);

        // Handle error messages
        if (!state.getErrorMessage().isEmpty()) {
            Toast.makeText(getContext(), state.getErrorMessage(), Toast.LENGTH_SHORT).show();
            viewModel.clearErrorMessage();
        }
    }

    private void updateUserInfo(ProfileViewModel.ProfileState state) {
        binding.textUserEmail.setText(state.getUserEmail());

        // For join date, you could use user creation date if available
        // For now, showing a placeholder
        binding.textJoinDate.setText("BookShare üyesi");
    }

    private void updateStatistics(ProfileViewModel.ProfileState state) {
        binding.textBorrowedCount.setText(String.valueOf(state.getBorrowedBooksCount()));
        binding.textPurchasedCount.setText(String.valueOf(state.getPurchasedBooksCount()));
    }

    private void updateTransactionLists(ProfileViewModel.ProfileState state) {
        // Update borrowed books
        if (state.hasBorrowedBooks()) {
            binding.recyclerBorrowedBooks.setVisibility(View.VISIBLE);
            binding.layoutEmptyBorrowed.setVisibility(View.GONE);

            // Show max 3 items, hide "View All" if less than 4
            int borrowedCount = state.getBorrowedBooksCount();
            if (borrowedCount > 3) {
                binding.textViewAllBorrowed.setVisibility(View.VISIBLE);
                borrowedBooksAdapter.updateBooks(state.getBorrowedBooks().subList(0, 3));
            } else {
                binding.textViewAllBorrowed.setVisibility(View.GONE);
                borrowedBooksAdapter.updateBooks(state.getBorrowedBooks());
            }
        } else {
            binding.recyclerBorrowedBooks.setVisibility(View.GONE);
            binding.layoutEmptyBorrowed.setVisibility(View.VISIBLE);
            binding.textViewAllBorrowed.setVisibility(View.GONE);
        }

        // Update purchased books
        if (state.hasPurchasedBooks()) {
            binding.recyclerPurchasedBooks.setVisibility(View.VISIBLE);
            binding.layoutEmptyPurchased.setVisibility(View.GONE);

            // Show max 3 items, hide "View All" if less than 4
            int purchasedCount = state.getPurchasedBooksCount();
            if (purchasedCount > 3) {
                binding.textViewAllPurchased.setVisibility(View.VISIBLE);
                purchasedBooksAdapter.updateBooks(state.getPurchasedBooks().subList(0, 3));
            } else {
                binding.textViewAllPurchased.setVisibility(View.GONE);
                purchasedBooksAdapter.updateBooks(state.getPurchasedBooks());
            }
        } else {
            binding.recyclerPurchasedBooks.setVisibility(View.GONE);
            binding.layoutEmptyPurchased.setVisibility(View.VISIBLE);
            binding.textViewAllPurchased.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean isLoading) {
        // You can add a progress bar or skeleton loading here
        // For now, just disable/enable the logout button
        binding.buttonLogout.setEnabled(!isLoading);
    }

    private void handleLogout() {
        // Navigate to login screen or restart activity
        Toast.makeText(getContext(), "Çıkış yapıldı", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }
}
