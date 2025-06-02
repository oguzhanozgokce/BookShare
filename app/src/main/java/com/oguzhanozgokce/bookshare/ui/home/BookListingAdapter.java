package com.oguzhanozgokce.bookshare.ui.home;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.oguzhanozgokce.bookshare.R;
import com.oguzhanozgokce.bookshare.databinding.BookItemLayoutBinding;
import com.oguzhanozgokce.bookshare.domain.model.Listing;

import java.util.Set;

public class BookListingAdapter extends ListAdapter<Listing, BookListingAdapter.BookViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Listing listing);
    }

    public interface OnSaveClickListener {
        void onSaveClick(Listing listing, int position);
    }

    private final OnItemClickListener clickListener;
    private final OnSaveClickListener saveClickListener;
    private Set<String> savedListingIds;

    public BookListingAdapter(OnItemClickListener clickListener, OnSaveClickListener saveClickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
        this.saveClickListener = saveClickListener;
    }

    public void updateSavedListings(Set<String> savedListingIds) {
        this.savedListingIds = savedListingIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BookItemLayoutBinding binding = BookItemLayoutBinding.inflate(inflater, parent, false);
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        boolean isSaved = savedListingIds != null && savedListingIds.contains(getItem(position).getId());
        holder.bind(getItem(position), clickListener, saveClickListener, isSaved);
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        private final BookItemLayoutBinding binding;

        public BookViewHolder(BookItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Listing listing, OnItemClickListener clickListener, OnSaveClickListener saveClickListener, boolean isSaved) {
            String formattedPrice = listing.getPrice() > 0
                    ? listing.getPrice() + " \u20BA"
                    : "Free";

            binding.textTitle.setText(listing.getTitle());
            binding.textDescription.setText(listing.getDescription());
            binding.textPrice.setText(formattedPrice);
            binding.textLocation.setText(listing.getLocation());

            // Type formatla
            String typeText = listing.getType().name().equals("FOR_SALE") ? "Satılık" : "Kiralık";
            binding.textType.setText(typeText);

            // Genre göster
            if (listing.getGenre() != null) {
                binding.textGenre.setText(listing.getGenre().getDisplayText());
                GradientDrawable background = (GradientDrawable)
                        ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.genre_tag_background);

                if (background != null) {
                    background.setColor(ContextCompat.getColor(binding.getRoot().getContext(), listing.getGenre().getColorResId()));
                    binding.textGenre.setBackground(background);
                }
            }

            // Kaydetme ikonu durumunu güncelle
            if (isSaved) {
                binding.iconSave.setImageResource(R.drawable.ic_bookmark_24);
                binding.iconSave.setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(), R.color.primary));
            } else {
                binding.iconSave.setImageResource(R.drawable.ic_bookmark_border_24);
                binding.iconSave.setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(), R.color.text_hint));
            }

            // Save ikonu click listener
            binding.iconSave.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && saveClickListener != null) {
                    saveClickListener.onSaveClick(listing, position);
                }
            });

            // Ana item click listener
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onItemClick(listing);
                }
            });
        }
    }

    private static final DiffUtil.ItemCallback<Listing> DIFF_CALLBACK = new DiffUtil.ItemCallback<Listing>() {
        @Override
        public boolean areItemsTheSame(@NonNull Listing oldItem, @NonNull Listing newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Listing oldItem, @NonNull Listing newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getUpdatedAt().equals(newItem.getUpdatedAt());
        }
    };
}
