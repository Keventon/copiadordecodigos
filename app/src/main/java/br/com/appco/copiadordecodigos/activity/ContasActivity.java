package br.com.appco.copiadordecodigos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.controller.UsuarioFirebase;
import br.com.appco.copiadordecodigos.databinding.ActivityContasBinding;
import br.com.appco.copiadordecodigos.fragment.ContasPagasFragment;
import br.com.appco.copiadordecodigos.fragment.ContasPendentesFragment;
import br.com.appco.copiadordecodigos.fragment.PerfilFragment;

public class ContasActivity extends AppCompatActivity {

    ActivityContasBinding binding;
    private DatabaseReference firebaseRef = ConfiguracoesFirebase.getFirebase();

    private final Fragment
            fragmentContaPendente = new ContasPendentesFragment(),
            fragmentContaPaga = new ContasPagasFragment(),
            fragmentPerfil = new PerfilFragment();

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
                    mudarFragment(fragmentPerfil);
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

    private void verificarAssinatura() {
        DatabaseReference assinaturaRef = firebaseRef
                .child("usuario")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child("assinaturaPaga");
        assinaturaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    int assinatura = Integer.parseInt(snapshot.getValue().toString());

                    if (assinatura == 0) {
                        startActivity(new Intent(ContasActivity.this, AssinaturaActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarAssinatura();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}