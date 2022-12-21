package br.com.appco.copiadordecodigos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.databinding.ActivityContasBinding;
import br.com.appco.copiadordecodigos.fragment.ContasPagasFragment;
import br.com.appco.copiadordecodigos.fragment.ContasPendentesFragment;
import br.com.appco.copiadordecodigos.fragment.SobreFragment;
import br.com.appco.copiadordecodigos.model.Usuario;
import br.com.appco.copiadordecodigos.util.Base64Custom;

public class ContasActivity extends AppCompatActivity {

    ActivityContasBinding binding;

    private final Fragment
            fragmentContaPendente = new ContasPendentesFragment(),
            fragmentContaPaga = new ContasPagasFragment(),
            fragmentSobre = new SobreFragment();

    public BottomNavigationView getBottomMenuHome() {
        return binding.bottomNavigationView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mudarFragment(new ContasPendentesFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item ->  {

            switch (item.getItemId()) {
                case R.id.conta_pendente:
                    mudarFragment(fragmentContaPendente);
                    return true;
                case R.id.conta_paga:
                    mudarFragment(fragmentContaPaga);
                    return true;
                case R.id.sobre:
                    mudarFragment(fragmentSobre);
                    return true;
            }
            return false;
        });
        if(getIntent().hasExtra("menu")){
            if(getIntent().getStringExtra("menu").equals("contaPendente")){
                binding.bottomNavigationView.setSelectedItemId(R.id.conta_pendente);
            }
            if(getIntent().getStringExtra("menu").equals("contaPaga")){
                binding.bottomNavigationView.setSelectedItemId(R.id.conta_paga);
            }
        } else {
            binding.bottomNavigationView.setSelectedItemId(R.id.conta_pendente);
        }

        binding.bottomNavigationView.setOnItemReselectedListener(item -> {});
    }

    private void mudarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}