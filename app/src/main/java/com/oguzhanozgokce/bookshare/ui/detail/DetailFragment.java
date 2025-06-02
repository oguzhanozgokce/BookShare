package com.oguzhanozgokce.bookshare.ui.detail;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.oguzhanozgokce.bookshare.R;
import com.oguzhanozgokce.bookshare.data.model.ListingType;
import com.oguzhanozgokce.bookshare.databinding.FragmentDetailBinding;
import com.oguzhanozgokce.bookshare.domain.model.Listing;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;
    private DetailViewModel viewModel;
    private Listing currentListing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        if (getArguments() != null) {
            currentListing = (Listing) getArguments().getSerializable("listing");
            viewModel.setListing(currentListing);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupClickListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> {
            // Use NavController to navigate up
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_share) {
                shareBook();
                return true;
            }
            return false;
        });
    }

    private void setupClickListeners() {
        binding.buttonAction.setOnClickListener(v -> handleActionButton());
        binding.buttonSave.setOnClickListener(v -> {
            if (currentListing != null) {
                //viewModel.toggleSaveStatus();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                // TODO: Loading göster (örneğin ProgressBar)
            } else if (state.getError() != null) {
                Toast.makeText(requireContext(), "Hata: " + state.getError(), Toast.LENGTH_LONG).show();
            } else if (state.getListing() != null) {
                currentListing = state.getListing();
                populateUi(state.getListing());
                updateBookmarkIcon(state.isSaved());
            }
        });
    }

    private void populateUi(Listing listing) {
        binding.textBookTitle.setText(listing.getTitle());
        binding.textBookAuthor.setText(listing.getAuthor());
        binding.textBookDescription.setText(listing.getDescription());

        if (listing.getGenre() != null) {
            binding.textBookGenre.setText(listing.getGenre().getDisplayText());
            GradientDrawable genreBackground = (GradientDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.genre_tag_background);
            if (genreBackground != null) {
                genreBackground.setColor(ContextCompat.getColor(requireContext(), listing.getGenre().getColorResId()));
                binding.textBookGenre.setBackground(genreBackground);
                binding.textBookGenre.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            }
        }

        String condition = "Durumu: " + getConditionText(listing.getCondition());
        binding.textBookCondition.setText(condition);

        String priceText = listing.getPrice() > 0
                ? listing.getPrice() + " ₺"
                : "Ücretsiz";
        binding.textBookPrice.setText(priceText);
        binding.textBookPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));

        // Update type text and background based on listing type
        updateTypeVisuals(listing);

        // Update action button based on listing type
        updateActionButton(listing);

        if (listing.getImageUrls() != null && !listing.getImageUrls().isEmpty()) {
            Glide.with(requireContext())
                    .load(listing.getImageUrls().get(0))
                    .placeholder(R.drawable.book_cover_gradient)
                    .error(R.drawable.book_cover_gradient)
                    .into(binding.imageBookCover);
        } else {
            binding.imageBookCover.setImageResource(R.drawable.book_cover_gradient);
        }
    }

    private void updateTypeVisuals(Listing listing) {
        String typeText;
        int typeColorRes;
        int typeTextColorRes;

        if (listing.getType() == ListingType.FOR_SALE) {
            typeText = "Satılık";
            typeColorRes = R.color.genre_science_bg; // Greenish background
            typeTextColorRes = R.color.text_tag_green; // Greenish text
        } else { // FOR_RENT
            typeText = "Kiralık";
            typeColorRes = R.color.primary; // Primary light background
            typeTextColorRes = R.color.background;    // Primary dark text
        }
        binding.textBookType.setText(typeText);

        GradientDrawable typeBackground = (GradientDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.tag_background).mutate();
        if (typeBackground != null) {
            typeBackground.setColor(ContextCompat.getColor(requireContext(), typeColorRes));
            binding.textBookType.setBackground(typeBackground);
            binding.textBookType.setTextColor(ContextCompat.getColor(requireContext(), typeTextColorRes));
        }
    }

    private void updateActionButton(Listing listing) {
        if (listing.getType() == ListingType.FOR_SALE) {
            binding.buttonAction.setText("Satın Al");
            binding.buttonAction.setIconResource(R.drawable.ic_shopping_cart_24);
        } else {
            binding.buttonAction.setText("Ödünç Al");
            binding.buttonAction.setIconResource(R.drawable.ic_book_24);
        }
    }

    private void updateBookmarkIcon(boolean isSaved) {
        if (isSaved) {
            binding.buttonSave.setIconResource(R.drawable.ic_bookmark_24);
            binding.buttonSave.setIconTintResource(R.color.primary);
        } else {
            binding.buttonSave.setIconResource(R.drawable.ic_bookmark_border_24);
            binding.buttonSave.setIconTintResource(R.color.text_hint);
        }
    }

    private void handleActionButton() {
        if (currentListing == null) return;

        NavController navController = Navigation.findNavController(requireView());
        Bundle args = createNavigationArgs();

        if (currentListing.getType() == ListingType.FOR_SALE) {
            navController.navigate(R.id.action_detailFragment_to_purchaseFragment, args);
        } else { // FOR_RENT
            navController.navigate(R.id.action_detailFragment_to_paymentFragment, args);
        }
    }

    private Bundle createNavigationArgs() {
        Bundle args = new Bundle();
        if (currentListing != null) {
            args.putString("bookTitle", currentListing.getTitle());
            args.putString("bookId", currentListing.getId());
            args.putString("bookAuthor", currentListing.getAuthor());
            args.putDouble("bookPrice", currentListing.getPrice());
            args.putSerializable("listing", currentListing);
        }
        return args;
    }

    private void shareBook() {
        if (currentListing == null) return;
        String shareText = "📚 " + currentListing.getTitle() +
                "\n👤 " + currentListing.getAuthor() +
                "\n💰 " + (currentListing.getPrice() > 0 ? currentListing.getPrice() + " ₺" : "Ücretsiz") +
                "\n📍 " + currentListing.getLocation() +
                "\n\nBookShare uygulaması ile paylaşıldı";

        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        startActivity(android.content.Intent.createChooser(intent, "Kitabı Paylaş"));
    }

    private String getConditionText(String condition) {
        if (condition == null) return "Bilinmiyor";
        switch (condition) {
            case "New":
                return "Sıfır";
            case "Like New":
                return "Sıfır Gibi";
            case "Good":
                return "İyi";
            case "Used":
                return "Kullanılmış";
            default:
                return condition;
        }
    }

    private String getLanguageText(String language) {
        if (language == null) return "Bilinmiyor";
        switch (language) {
            case "English":
                return "İngilizce";
            case "Turkish":
                return "Türkçe";
            case "French":
                return "Fransızca";
            case "German":
                return "Almanca";
            default:
                return language;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
