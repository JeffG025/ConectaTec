package com.conectatec.ui.estudiante;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.conectatec.R;
import com.conectatec.databinding.ActivityMainEstudianteBinding;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity principal del rol Estudiante.
 * Maneja el NavController y el pill bottom nav custom (sin Toolbar).
 */
@AndroidEntryPoint
public class MainEstudianteActivity extends AppCompatActivity {

    private ActivityMainEstudianteBinding binding;
    private NavController navController;
    private NavItem[] items;
    private int currentActiveIndex = -1;

    private static final OvershootInterpolator OVERSHOOT  = new OvershootInterpolator(1.5f);
    private static final PathInterpolator      DECELERATE = new PathInterpolator(0.4f, 0f, 0.2f, 1f);

    /** Destinos del nav graph en el mismo orden que el array items. */
    private static final int[] DESTINATIONS = {
            R.id.estudianteDashboardFragment,
            R.id.estudianteGruposFragment,
            R.id.estudianteTareasFragment,
            R.id.estudianteChatFragment,
            R.id.estudiantePerfilFragment
    };

    private static class NavItem {
        final LinearLayout container;
        final FrameLayout circle;
        final ImageView icon;
        final TextView label;

        NavItem(LinearLayout c, FrameLayout fc, ImageView i, TextView l) {
            container = c; circle = fc; icon = i; label = l;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainEstudianteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostEstudiante);
        if (navHost == null) return;

        navController = navHost.getNavController();
        setupPillNav();
    }

    private void setupPillNav() {
        items = new NavItem[]{
                new NavItem(binding.itemDashboard, binding.circleDashboard,
                        binding.iconDashboard, binding.labelDashboard),
                new NavItem(binding.itemGrupos,    binding.circleGrupos,
                        binding.iconGrupos,    binding.labelGrupos),
                new NavItem(binding.itemTareas,    binding.circleTareas,
                        binding.iconTareas,    binding.labelTareas),
                new NavItem(binding.itemChat,      binding.circleChat,
                        binding.iconChat,      binding.labelChat),
                new NavItem(binding.itemPerfil,    binding.circlePerfil,
                        binding.iconPerfil,    binding.labelPerfil)
        };

        for (int i = 0; i < items.length; i++) {
            final int destId = DESTINATIONS[i];
            items[i].container.setOnClickListener(v -> {
                if (navController.getCurrentDestination() != null
                        && navController.getCurrentDestination().getId() == destId) {
                    return;
                }

                NavOptions options = new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(true)
                        .setPopUpTo(
                                navController.getGraph().getStartDestinationId(),
                                false,
                                true
                        )
                        .build();

                navController.navigate(destId, null, options);
            });
        }

        navController.addOnDestinationChangedListener((controller, dest, args) -> {
            int id = dest.getId();

            // Pill nav solo en los 5 destinos raíz
            boolean esRaiz = (id == R.id.estudianteDashboardFragment
                    || id == R.id.estudianteGruposFragment
                    || id == R.id.estudianteTareasFragment
                    || id == R.id.estudianteChatFragment
                    || id == R.id.estudiantePerfilFragment);
            binding.bottomNavEstudiante.setVisibility(esRaiz ? View.VISIBLE : View.GONE);

            for (int i = 0; i < DESTINATIONS.length; i++) {
                if (DESTINATIONS[i] == id) {
                    selectItem(i);
                    return;
                }
            }

            // Sub-destinos: resaltar la pestaña padre
            if (id == R.id.estudianteGrupoDetalleFragment
                    || id == R.id.estudianteUnirseGrupoFragment
                    || id == R.id.estudianteEscanearQrFragment) {
                selectItem(1); // Grupos
            } else if (id == R.id.estudianteTareaDetalleFragment
                    || id == R.id.estudianteEntregarTareaFragment) {
                selectItem(2); // Tareas
            } else if (id == R.id.estudianteConversacionFragment) {
                selectItem(3); // Chat
            }
        });
    }

    private void selectItem(int activeIndex) {
        if (activeIndex == currentActiveIndex) return;
        currentActiveIndex = activeIndex;

        int activeIcon   = ContextCompat.getColor(this, R.color.nav_icon_active);
        int inactiveIcon = ContextCompat.getColor(this, R.color.nav_icon_inactive);

        for (int i = 0; i < items.length; i++) {
            NavItem item = items[i];
            boolean active = (i == activeIndex);

            // Fondo e icono: instantáneos
            if (active) {
                item.container.setBackgroundResource(R.drawable.bg_nav_item_active);
                item.circle.setBackgroundResource(R.drawable.bg_icon_circle_active);
                ImageViewCompat.setImageTintList(item.icon, ColorStateList.valueOf(activeIcon));
            } else {
                item.container.setBackground(null);
                item.circle.setBackgroundResource(R.drawable.bg_icon_circle_inactive);
                ImageViewCompat.setImageTintList(item.icon, ColorStateList.valueOf(inactiveIcon));
            }

            // Scale del círculo con animación
            item.circle.animate()
                    .scaleX(active ? 0.8f : 1f)
                    .scaleY(active ? 0.8f : 1f)
                    .setDuration(200)
                    .setInterpolator(active ? OVERSHOOT : DECELERATE)
                    .start();

            // Label: fade in/out
            if (active) {
                item.label.animate().cancel();
                item.label.setAlpha(0f);
                item.label.setVisibility(View.VISIBLE);
                item.label.animate()
                        .alpha(1f)
                        .setDuration(150)
                        .setInterpolator(DECELERATE)
                        .start();
            } else {
                item.label.animate()
                        .alpha(0f)
                        .setDuration(100)
                        .withEndAction(() -> item.label.setVisibility(View.GONE))
                        .start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
