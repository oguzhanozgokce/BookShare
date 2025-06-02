package com.oguzhanozgokce.bookshare.ui.payment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.oguzhanozgokce.bookshare.databinding.FragmentPaymentBinding;
import com.oguzhanozgokce.bookshare.domain.model.Listing;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PaymentFragment extends Fragment {

    private FragmentPaymentBinding binding;
    private PaymentViewModel viewModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        // Get book information from arguments
        if (getArguments() != null) {
            String bookTitle = getArguments().getString("bookTitle", "");
            String bookId = getArguments().getString("bookId", "");
            Listing listing = (Listing) getArguments().getSerializable("listing");

            viewModel.setBookInfo(bookTitle, bookId);

            if (listing != null) {
                viewModel.setBookInfo(listing.getTitle(), listing.getId());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentBinding.inflate(inflater, container, false);

        setupListeners();
        observeViewModel();
        updateBookInfo();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateBookInfo() {
        // Update UI with book information
        if (getArguments() != null) {
            String bookTitle = getArguments().getString("bookTitle", "");
            if (!bookTitle.isEmpty()) {
                binding.textBookTitle.setText(bookTitle);
            }
        }
    }

    private void setupListeners() {
        setupTextWatchers();
        setupDatePickers();
        setupButtonListener();
        setupToolbar();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });
    }

    private void setupButtonListener() {
        binding.buttonCompletePayment.setOnClickListener(v -> viewModel.completeBorrowing());
    }


    private void setupTextWatchers() {
        binding.editTextFullName.addTextChangedListener(createTextWatcher(viewModel::updateFullName));
        binding.editTextPhone.addTextChangedListener(createTextWatcher(viewModel::updatePhone));
        binding.editTextAddress.addTextChangedListener(createTextWatcher(viewModel::updateAddress));
        binding.editTextCity.addTextChangedListener(createTextWatcher(viewModel::updateCity));
        binding.editTextPostalCode.addTextChangedListener(createTextWatcher(viewModel::updatePostalCode));
    }

    private void setupDatePickers() {
        binding.editTextStartDate.setOnClickListener(v -> showDatePicker(true));
        binding.editTextEndDate.setOnClickListener(v -> showDatePicker(false));
    }


    private TextWatcher createTextWatcher(TextChangeCallback callback) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                callback.onTextChanged(s.toString());
            }
        };
    }

    private void observeViewModel() {
        viewModel.state.observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(PaymentState state) {
        updateBorrowPeriodDisplay();
        updateButtonState(state);

        // Only show error once and handle completion
        if (state.isCompleted()) {
            handleCompletionState(state);
        } else if (!state.getErrorMessage().isEmpty() && !state.isLoading()) {
            handleErrorMessage(state);
        }
    }

    private void updateBorrowPeriodDisplay() {
        String period = viewModel.calculateBorrowPeriod();
        if (!period.isEmpty()) {
            binding.textBorrowPeriod.setText(period);
            binding.layoutBorrowPeriod.setVisibility(View.VISIBLE);
        } else {
            binding.layoutBorrowPeriod.setVisibility(View.GONE);
        }
    }

    private void updateButtonState(PaymentState state) {
        binding.buttonCompletePayment.setEnabled(state.canCompleteBorrow() && !state.isLoading());
        binding.buttonCompletePayment.setText(state.isLoading()
                ? "İşleniyor..."
                : "Ödünç Almayı Tamamla");
    }

    private void handleErrorMessage(PaymentState state) {
        Toast.makeText(getContext(), state.getErrorMessage(), Toast.LENGTH_SHORT).show();
        // Clear error message to prevent repeated toasts
        viewModel.clearErrorMessage();
    }

    private void handleCompletionState(PaymentState state) {
        Toast.makeText(getContext(),
                "Ödünç alma işlemi başarıyla tamamlandı!",
                Toast.LENGTH_LONG).show();
        requireActivity().onBackPressed();
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String formattedDate = dateFormat.format(calendar.getTime());

                    if (isStartDate) {
                        binding.editTextStartDate.setText(formattedDate);
                        viewModel.updateStartDate(formattedDate);

                        // Auto-set end date to start date + 1 day
                        Calendar autoEndDate = (Calendar) calendar.clone();
                        autoEndDate.add(Calendar.DAY_OF_MONTH, 1);
                        binding.editTextEndDate.setText(dateFormat.format(autoEndDate.getTime()));
                        viewModel.updateEndDate(dateFormat.format(autoEndDate.getTime()));
                    } else {
                        binding.editTextEndDate.setText(formattedDate);
                        viewModel.updateEndDate(formattedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        configureDatePickerConstraints(datePickerDialog, isStartDate);
        datePickerDialog.show();
    }

    private void configureDatePickerConstraints(DatePickerDialog dialog, boolean isStartDate) {
        if (isStartDate) {
            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        } else {
            PaymentState state = viewModel.state.getValue();
            if (state != null && state.areDatesSelected()) {
                try {
                    Calendar startDate = Calendar.getInstance();
                    startDate.setTime(dateFormat.parse(state.getStartDate()));

                    Calendar minEndDate = (Calendar) startDate.clone();
                    minEndDate.add(Calendar.DAY_OF_MONTH, 1);

                    Calendar maxEndDate = (Calendar) startDate.clone();
                    maxEndDate.add(Calendar.DAY_OF_MONTH, 10);

                    dialog.getDatePicker().setMinDate(minEndDate.getTimeInMillis());
                    dialog.getDatePicker().setMaxDate(maxEndDate.getTimeInMillis());
                } catch (Exception e) {
                    // Handle parsing error gracefully
                }
            }
        }
    }

    @FunctionalInterface
    private interface TextChangeCallback {
        void onTextChanged(String text);
    }
}
