package com.oguzhanozgokce.bookshare.ui.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.oguzhanozgokce.bookshare.R;
import com.oguzhanozgokce.bookshare.databinding.FragmentLoginBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        initObservers();
        initListeners();
        binding.iconBack.setOnClickListener(v -> backNavigate());
        binding.textForgotPassword.setOnClickListener(v -> navigateToResetPasswordFragment());
    }

    private void initListeners() {
        binding.buttonLogin.setOnClickListener(v -> {
            String email = safeText(binding.editEmail);
            String password = safeText(binding.editPassword);

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.login(email, password);
        });
    }

    private void initObservers() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            binding.loadingOverlay.setVisibility(state.isLoading() ? View.VISIBLE: View.GONE);
            binding.progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
            binding.editEmail.setText(state.getEmail());
            binding.editPassword.setText(state.getPassword());
            switch (state.getStatus()) {
                case SUCCESS:
                    navigateToHomeFragment();
                    break;
                case ERROR:
                    handleError(state.getErrorMessage());
                    break;
            }
        });
    }

    private void navigateToHomeFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_global_loginToHomeFragment);
    }

    private void handleError(String errorMessage) {
        Toast.makeText(requireContext(), errorMessage != null ? errorMessage : "An error occurred", Toast.LENGTH_LONG).show();
    }

    private String safeText(EditText editText) {
        Editable text = editText.getText();
        return text != null ? text.toString().trim() : "";
    }

    private void backNavigate() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
    }

    private void navigateToResetPasswordFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}