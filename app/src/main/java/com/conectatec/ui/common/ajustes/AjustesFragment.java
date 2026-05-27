package com.conectatec.ui.common.ajustes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.conectatec.R;
import com.conectatec.databinding.FragmentAjustesBinding;
import com.conectatec.ui.common.EntradaAnimator;
import com.conectatec.ui.common.ThemePreferenceManager;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AjustesFragment extends Fragment {

    private FragmentAjustesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAjustesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        marcarModoActual();
        setupListeners();
        EntradaAnimator.animar(
                binding.headerAjustes,
                binding.cardTema,
                binding.cardNotificaciones
        );
    }

    private void marcarModoActual() {
        int modo = ThemePreferenceManager.getCurrentMode(requireContext());
        if (modo == AppCompatDelegate.MODE_NIGHT_NO) {
            binding.radioClaro.setChecked(true);
        } else if (modo == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.radioOscuro.setChecked(true);
        } else {
            binding.radioSistema.setChecked(true);
        }
    }

    private void setupListeners() {
        binding.btnBackAjustes.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp());

        binding.radioGroupTema.setOnCheckedChangeListener((group, checkedId) -> {
            int modo;
            if (checkedId == R.id.radioClaro) {
                modo = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.radioOscuro) {
                modo = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                modo = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }
            ThemePreferenceManager.setThemeMode(requireContext(), modo);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
