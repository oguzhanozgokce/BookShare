package com.oguzhanozgokce.bookshare;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.oguzhanozgokce.bookshare.data.repository.BookRepositoryImpl;
import com.oguzhanozgokce.bookshare.databinding.ActivityMainBinding;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    BookRepositoryImpl bookRepositoryImpl;
    private ActivityMainBinding binding;
    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            boolean showBottomNav = destination.getId() == R.id.homeFragment
                    || destination.getId() == R.id.profileFragment;

            binding.bottomNavigationView.setVisibility(showBottomNav ? View.VISIBLE : View.GONE);
        });
    }
}