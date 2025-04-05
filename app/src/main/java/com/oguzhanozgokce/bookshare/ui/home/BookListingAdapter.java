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

public class BookListingAdapter extends ListAdapter<Listing, BookListingAdapter.BookViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Listing listing);
    }

    private final OnItemClickListener clickListener;

    public BookListingAdapter(OnItemClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
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
        holder.bind(getItem(position), clickListener);
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        private final BookItemLayoutBinding binding;

        public BookViewHolder(BookItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Listing listing, OnItemClickListener clickListener) {
            String formattedPrice = listing.getPrice() > 0
                    ? listing.getPrice() + " \u20BA"
                    : "Free";

            binding.textTitle.setText(listing.getTitle());
            binding.textDescription.setText(listing.getDescription());
            binding.textPrice.setText(formattedPrice);
            binding.textLocation.setText(listing.getLocation());
            binding.textType.setText(listing.getType().name());
            GradientDrawable background = (GradientDrawable)
                    ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.genre_tag_background);

            if (background != null) {
                background.setColor(ContextCompat.getColor(binding.getRoot().getContext(), listing.getGenre().getColorResId()));
                binding.textGenre.setBackground(background);
            }

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
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