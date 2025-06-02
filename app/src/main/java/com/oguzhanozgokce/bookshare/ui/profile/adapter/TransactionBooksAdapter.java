package com.oguzhanozgokce.bookshare.ui.profile.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oguzhanozgokce.bookshare.databinding.ItemTransactionBookBinding;
import com.oguzhanozgokce.bookshare.domain.model.BorrowRecord;
import com.oguzhanozgokce.bookshare.domain.model.PurchaseRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionBooksAdapter extends RecyclerView.Adapter<TransactionBooksAdapter.ViewHolder> {

    private final List<Object> transactions = new ArrayList<>();
    private final boolean isBorrowedBooks;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public TransactionBooksAdapter(boolean isBorrowedBooks) {
        this.isBorrowedBooks = isBorrowedBooks;
    }

    public void updateBooks(List<?> books) {
        transactions.clear();
        if (books != null) {
            transactions.addAll(books);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBookBinding binding = ItemTransactionBookBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object transaction = transactions.get(position);

        if (isBorrowedBooks && transaction instanceof BorrowRecord) {
            holder.bind((BorrowRecord) transaction);
        } else if (!isBorrowedBooks && transaction instanceof PurchaseRecord) {
            holder.bind((PurchaseRecord) transaction);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionBookBinding binding;

        public ViewHolder(@NonNull ItemTransactionBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BorrowRecord borrowRecord) {
            binding.textBookTitle.setText(borrowRecord.getBookTitle());
            binding.textTransactionDate.setText(dateFormat.format(borrowRecord.getBorrowStartDate()));

            // Calculate duration
            long diffInMillis = borrowRecord.getBorrowEndDate().getTime() - borrowRecord.getBorrowStartDate().getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
            binding.textPriceOrDuration.setText(diffInDays + " gün");
        }

        public void bind(PurchaseRecord purchaseRecord) {
            binding.textBookTitle.setText(purchaseRecord.getBookTitle());
            binding.textTransactionDate.setText(dateFormat.format(purchaseRecord.getPurchaseDate()));
            binding.textPriceOrDuration.setText(String.format("%.0f ₺", purchaseRecord.getPricePaid()));
        }
    }
}