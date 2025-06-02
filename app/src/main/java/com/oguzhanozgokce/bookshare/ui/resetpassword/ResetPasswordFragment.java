package com.oguzhanozgokce.bookshare.ui.resetpassword;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.oguzhanozgokce.bookshare.R;
import com.oguzhanozgokce.bookshare.databinding.FragmentResetPasswordBinding;

import java.util.Objects;


import javax.annotation.Nullable;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment} factory method to
 *
 */

@AndroidEntryPoint
public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;
    private ResetPasswordViewModel viewModel;

    public ResetPasswordFragment() { super(R.layout.fragment_reset_password); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);

        /* ----- UI listeners ----- */
        binding.iconBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());

        binding.editTextEmail.addTextChangedListener(simpleWatcher(
                s -> viewModel.onEmailChanged(s)));

        binding.buttonSendResetLink.setOnClickListener(v -> viewModel.sendResetLink());

        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
            binding.loadingOverlay.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);

            binding.buttonSendResetLink.setEnabled(state.isEmailValid() && !state.isLoading());

            if (!Objects.requireNonNull(binding.editTextEmail.getText()).toString().equals(state.getEmail())) {
                binding.editTextEmail.setText(state.getEmail());
            }

            binding.textInputLayoutEmail.setError(state.getErrorMessage());

            if (state.isSuccess()) {
                Snackbar.make(requireView(),
                        R.string.reset_link_sent,
                        Snackbar.LENGTH_LONG).show();
                NavHostFragment.findNavController(this).popBackStack();
            }
        });
    }

    private TextWatcher simpleWatcher(TextChanged onTextChanged) {
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextChanged.invoke(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        };
    }
    private interface TextChanged { void invoke(String text); }

    private void backNavigate() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}