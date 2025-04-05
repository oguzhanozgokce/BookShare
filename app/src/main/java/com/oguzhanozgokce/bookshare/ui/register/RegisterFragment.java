package com.oguzhanozgokce.bookshare.ui.register;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.oguzhanozgokce.bookshare.R;
import com.oguzhanozgokce.bookshare.databinding.FragmentRegisterBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private RegisterViewModel viewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        setupObservers();
        binding.buttonRegister.setOnClickListener(v -> {
            String name = binding.editName.getText().toString();
            String email = binding.editEmail.getText().toString();
            String password = binding.editPassword.getText().toString();

            viewModel.registerUser(name, email, password);
        });
    }

    private void setupObservers() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.loadingOverlay.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
            binding.progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
            switch (state.getStatus()) {
                case SUCCESS:
                    Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.action_global_homeFragment);
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), "Registration failed: " + state.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("RegisterFragment", state.getErrorMessage());
                    break;
            }
        });
    }
}