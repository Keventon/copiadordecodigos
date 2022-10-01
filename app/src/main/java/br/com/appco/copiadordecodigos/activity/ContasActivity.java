package br.com.appco.copiadordecodigos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.databinding.ActivityContasBinding;
import br.com.appco.copiadordecodigos.fragment.ContasPagasFragment;
import br.com.appco.copiadordecodigos.fragment.ContasPendentesFragment;

public class ContasActivity extends AppCompatActivity {

    ActivityContasBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mudarFragment(new ContasPendentesFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item ->  {

            switch (item.getItemId()) {
                case R.id.conta_pendente:
                    mudarFragment(new ContasPendentesFragment());
                    break;
                case R.id.conta_paga:
                    mudarFragment(new ContasPagasFragment());
                    break;
            }

            return true;

        });
    }

    private void mudarFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}