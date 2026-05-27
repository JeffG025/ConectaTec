package com.conectatec.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.conectatec.R;
import com.conectatec.databinding.ActivityLoginBinding;
import com.conectatec.ui.admin.MainAdminActivity;
import com.conectatec.ui.common.UiState;
import com.conectatec.ui.docente.MainDocenteActivity;
import com.conectatec.ui.estudiante.MainEstudianteActivity;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Pantalla de inicio de sesión.
 * El flujo real usa Google Sign-In vía Credential Manager + backend Spring Boot.
 * Los botones DEMO (Admin/Docente) se mantienen como selector provisional de rol.
 */
@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private GoogleAuthHelper googleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            int navHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, 0, 0, Math.max(imeHeight, navHeight));
            return insets;
        });

        viewModel    = new ViewModelProvider(this).get(LoginViewModel.class);
        googleHelper = new GoogleAuthHelper(this, getString(R.string.google_web_client_id));

        observeViewModel();
        setupListeners();
    }

    private void observeViewModel() {
        viewModel.getState().observe(this, state -> {
            boolean cargando = state instanceof UiState.Loading;
            binding.btnLoginGoogle.setEnabled(!cargando);

            if (state instanceof UiState.Success) {
                String rol = ((UiState.Success<String>) state).data;
                rutearPorRol(rol);
            } else if (state instanceof UiState.Error) {
                String mensaje = ((UiState.Error<?>) state).mensaje;
                Snackbar.make(binding.getRoot(), mensaje, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        // TODO: reemplazar por googleHelper.solicitarCredencial() cuando las credenciales OAuth estén configuradas
        binding.btnLoginGoogle.setOnClickListener(v ->
                viewModel.loginConGoogle("dummy-token", "demo@gmail.com"));

        // ── Selector de rol DEMO (provisional) ──────────────────────────
        // TODO: eliminar cuando el login real con Google esté integrado al 100%
        binding.btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, MainAdminActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.btnLoginDocente.setOnClickListener(v -> {
            startActivity(new Intent(this, MainDocenteActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.btnLoginEstudiante.setOnClickListener(v -> {
            startActivity(new Intent(this, MainEstudianteActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // ── Registro ─────────────────────────────────────────────────────
        binding.tvIrRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void rutearPorRol(String rol) {
        Intent intent;
        switch (rol) {
            case "ADMIN":
            case "ADMINISTRADOR":
                intent = new Intent(this, MainAdminActivity.class);
                break;
            case "DOCENTE":
                intent = new Intent(this, MainDocenteActivity.class);
                break;
            case "ESTUDIANTE":
                intent = new Intent(this, MainEstudianteActivity.class);
                break;
            case "PENDIENTE":
                intent = new Intent(this, WaitingApprovalActivity.class);
                break;
            default:
                Snackbar.make(binding.getRoot(),
                        "Rol no reconocido: " + rol, Snackbar.LENGTH_LONG).show();
                return;
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
