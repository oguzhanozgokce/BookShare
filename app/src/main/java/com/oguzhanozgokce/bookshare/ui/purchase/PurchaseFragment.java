package com.oguzhanozgokce.bookshare.ui.purchase;

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

import com.oguzhanozgokce.bookshare.R;
import com.oguzhanozgokce.bookshare.databinding.FragmentPurchaseBinding;
import com.oguzhanozgokce.bookshare.domain.model.Listing;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PurchaseFragment extends Fragment {

    private FragmentPurchaseBinding binding;
    private PurchaseViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PurchaseViewModel.class);

        // Get book information from arguments
        if (getArguments() != null) {
            String bookTitle = getArguments().getString("bookTitle", "");
            String bookId = getArguments().getString("bookId", "");
            double bookPrice = getArguments().getDouble("bookPrice", 0.0);
            Listing listing = (Listing) getArguments().getSerializable("listing");

            viewModel.setBookInfo(bookTitle, bookId, bookPrice);

            if (listing != null) {
                viewModel.setBookInfo(listing.getTitle(), listing.getId(), listing.getPrice());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPurchaseBinding.inflate(inflater, container, false);

        setupListeners();
        observeViewModel();
        initializeDefaults();
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

            double bookPrice = getArguments().getDouble("bookPrice", 0.0);
            if (bookPrice > 0) {
                binding.textTotalAmount.setText(viewModel.getFormattedPrice());
            }
        }
    }

    private void initializeDefaults() {
        // Set default payment method
        binding.radioCashOnDelivery.setChecked(true);
    }

    private void setupListeners() {
        setupTextWatchers();
        setupPaymentMethodListener();
        setupButtonListener();
        setupToolbar(); // Added toolbar setup
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });
    }

    private void setupButtonListener() {
        binding.buttonCompletePurchase.setOnClickListener(v -> viewModel.completePurchase());
    }


    private void setupPaymentMethodListener() {
        binding.radioGroupPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            PurchaseState.PaymentMethod method = checkedId == R.id.radioCreditCard
                    ? PurchaseState.PaymentMethod.CREDIT_CARD
                    : PurchaseState.PaymentMethod.CASH_ON_DELIVERY;

            viewModel.updatePaymentMethod(method);

            binding.layoutCreditCardDetails.setVisibility(
                    method == PurchaseState.PaymentMethod.CREDIT_CARD ? View.VISIBLE : View.GONE
            );
        });
    }


    private void setupTextWatchers() {
        binding.editTextFullName.addTextChangedListener(createTextWatcher(viewModel::updateFullName));
        binding.editTextPhone.addTextChangedListener(createTextWatcher(viewModel::updatePhone));
        binding.editTextAddress.addTextChangedListener(createTextWatcher(viewModel::updateAddress));
        binding.editTextCity.addTextChangedListener(createTextWatcher(viewModel::updateCity));
        binding.editTextPostalCode.addTextChangedListener(createTextWatcher(viewModel::updatePostalCode));

        binding.editTextCardNumber.addTextChangedListener(createTextWatcher(viewModel::updateCardNumber));
        binding.editTextExpiry.addTextChangedListener(createTextWatcher(viewModel::updateExpiry));
        binding.editTextCVV.addTextChangedListener(createTextWatcher(viewModel::updateCvv));
        binding.editTextCardHolderName.addTextChangedListener(createTextWatcher(viewModel::updateCardHolderName));
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

    private void updateUI(PurchaseState state) {
        updateButtonState(state);

        // Only show error once and handle completion
        if (state.isCompleted()) {
            handleCompletionState(state);
        } else if (!state.getErrorMessage().isEmpty() && !state.isLoading()) {
            handleErrorMessage(state);
        }
    }

    private void updateButtonState(PurchaseState state) {
        binding.buttonCompletePurchase.setEnabled(state.canCompletePurchase() && !state.isLoading());
        binding.buttonCompletePurchase.setText(state.isLoading()
                ? "İşleniyor..."
                : "Satın Almayı Tamamla");
    }

    private void handleErrorMessage(PurchaseState state) {
        Toast.makeText(getContext(), state.getErrorMessage(), Toast.LENGTH_SHORT).show();
        // Clear error message to prevent repeated toasts
        viewModel.clearErrorMessage();
    }

    private void handleCompletionState(PurchaseState state) {
        Toast.makeText(getContext(),
                "Satın alma işlemi başarıyla tamamlandı!",
                Toast.LENGTH_LONG).show();
        requireActivity().onBackPressed();
    }

    @FunctionalInterface
    private interface TextChangeCallback {
        void onTextChanged(String text);
    }
}
