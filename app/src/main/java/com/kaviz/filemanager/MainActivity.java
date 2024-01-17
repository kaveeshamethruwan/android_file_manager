package com.kaviz.filemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.kaviz.filemanager.Fragments.CardFragment;
import com.kaviz.filemanager.Fragments.HomeFragment;
import com.kaviz.filemanager.Fragments.InternalStorageFragment;
import com.kaviz.filemanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //binding.drawerLayout.openDrawer(GravityCompat.START);

        setSupportActionBar(binding.toolBar);
        binding.navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolBar, R.string.open_drawer, R.string.close_drawer);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        launchFragment(new HomeFragment());

        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.navigationView.setCheckedItem(R.id.navigationHome);


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.navigationHome:
                launchFragment(new HomeFragment());
                break;

            case R.id.navigationInternal:
                launchFragment(new InternalStorageFragment());
                break;

            case R.id.navigationSdCard:
                launchFragment(new CardFragment());
                break;

            default:
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();

        }

        binding.drawerLayout.close();

        return true;
    }

    @Override
    public void onBackPressed() {

        getSupportFragmentManager().popBackStackImmediate();

        if (binding.drawerLayout.isOpen()) {

            binding.drawerLayout.close();

        } else {

            super.onBackPressed();

        }
    }

    private void launchFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();

    }

}