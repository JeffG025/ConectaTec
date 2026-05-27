package com.conectatec.ui.estudiante.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.conectatec.R;
import com.conectatec.data.model.ResumenEstudiante;
import com.conectatec.databinding.FragmentEstudianteDashboardBinding;
import com.conectatec.ui.common.EntradaAnimator;
import com.conectatec.ui.common.UiState;
import com.conectatec.ui.estudiante.tareas.adapter.TareaEstudianteAdapter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EstudianteDashboardFragment extends Fragment {

    private FragmentEstudianteDashboardBinding binding;
    private EstudianteDashboardViewModel viewModel;
    private TareaEstudianteAdapter proximasAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstudianteDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EstudianteDashboardViewModel.class);

        // Adapter para la lista de próximas entregas
        proximasAdapter = new TareaEstudianteAdapter(new ArrayList<>(), tarea -> {
            android.os.Bundle args = new android.os.Bundle();
            args.putInt("tareaId", tarea.id);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.estudianteTareaDetalleFragment, args);
        });
        binding.rvProximasTareas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvProximasTareas.setAdapter(proximasAdapter);

        // "Ver todas" → pestaña Tareas
        binding.tvVerTodasTareasEst.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(v);
            NavOptions opts = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .setPopUpTo(nav.getGraph().getStartDestinationId(), false, true)
                    .build();
            nav.navigate(R.id.estudianteTareasFragment, null, opts);
        });

        // Inicializar charts con ajustes visuales
        configurarGraficas();

        // Observar ViewModel
        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            binding.progressBarDashboard.setVisibility(
                    state instanceof UiState.Loading ? View.VISIBLE : View.GONE);

            if (state instanceof UiState.Success) {
                ResumenEstudiante r = ((UiState.Success<ResumenEstudiante>) state).data;
                pintarResumen(r);
                EntradaAnimator.animar(
                        binding.cardBienvenidaEstudiante,
                        binding.layoutKpisEstudiante,
                        binding.cardDonutTareas,
                        binding.cardBarrasTipos,
                        binding.cardProximasTareasEst
                );
            } else if (state instanceof UiState.Error) {
                String msg = ((UiState.Error<?>) state).mensaje;
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
                EntradaAnimator.animar(
                        binding.cardBienvenidaEstudiante,
                        binding.layoutKpisEstudiante,
                        binding.cardDonutTareas,
                        binding.cardBarrasTipos,
                        binding.cardProximasTareasEst
                );
            }
        });

        viewModel.cargarDatos();
    }

    // ── Datos ──────────────────────────────────────────────────────────

    private void pintarResumen(ResumenEstudiante r) {
        // Hero card — mini stats
        binding.tvDashTotalGrupos.setText(String.valueOf(r.totalGrupos));
        binding.tvDashPendientes.setText(String.valueOf(r.tareasPendientes));
        binding.tvDashCalificadas.setText(String.valueOf(r.tareasCalificadas));

        // KPI row
        binding.tvDashEntregadas.setText(String.valueOf(r.tareasEntregadas));
        binding.tvDashPromedio.setText(r.promedioGeneral > 0
                ? String.format(Locale.getDefault(), "%.0f", r.promedioGeneral)
                : "—");

        // Donut con datos reales y animación
        actualizarDonut(r);

        // Próximas entregas
        proximasAdapter.setLista(r.proximasTareas);
        boolean vacio = r.proximasTareas.isEmpty();
        binding.rvProximasTareas.setVisibility(vacio ? View.GONE : View.VISIBLE);
        binding.tvSinProximasTareas.setVisibility(vacio ? View.VISIBLE : View.GONE);
    }

    // ── Charts ─────────────────────────────────────────────────────────

    private void configurarGraficas() {
        inicializarDonut(binding.chartDonutTareas);
        configurarBarrasTipos(binding.chartBarrasTipos);
    }

    /** Configura ajustes visuales del donut; los datos se cargan en actualizarDonut(). */
    private void inicializarDonut(PieChart chart) {
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setTransparentCircleColor(Color.TRANSPARENT);
        chart.setCenterText("—");
        chart.setCenterTextSize(18f);
        chart.setCenterTextColor(
                ContextCompat.getColor(requireContext(), R.color.colorOnSurface));
        chart.setCenterTextTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.setBackgroundColor(Color.TRANSPARENT);
    }

    /** Actualiza el donut con datos reales y lanza animación. */
    private void actualizarDonut(ResumenEstudiante r) {
        PieChart chart = binding.chartDonutTareas;
        int calificadas = r.tareasCalificadas;
        int entregadas  = r.tareasEntregadas;
        int pendientes  = r.tareasPendientes;
        int total       = calificadas + entregadas + pendientes;

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(calificadas > 0 ? calificadas : 0.01f));
        entries.add(new PieEntry(entregadas  > 0 ? entregadas  : 0.01f));
        entries.add(new PieEntry(pendientes  > 0 ? pendientes  : 0.01f));

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(
                ContextCompat.getColor(requireContext(), R.color.colorSuccess),
                ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                ContextCompat.getColor(requireContext(), R.color.colorWarning)
        );
        ds.setValueTextSize(0f);
        ds.setValueTextColor(Color.TRANSPARENT);
        ds.setSliceSpace(2f);

        chart.setData(new PieData(ds));

        int pct = total > 0 ? ((calificadas + entregadas) * 100 / total) : 0;
        chart.setCenterText(pct + "%");
        chart.animateY(800, Easing.EaseInOutQuad);
        chart.invalidate();

        // Leyenda manual
        binding.tvLeyendaCalificadas.setText("Calificadas  " + calificadas);
        binding.tvLeyendaEntregadas.setText("Entregadas  "  + entregadas);
        binding.tvLeyendaPendientes.setText("Pendientes  "  + pendientes);
    }

    /**
     * Barras por tipo de tarea — valores fijos alineados con EstudianteRepositoryImpl.
     * TAREA=5 · PROYECTO=1 · EXAMEN=3 · TRABAJO=1
     */
    private void configurarBarrasTipos(BarChart chart) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 5f));
        entries.add(new BarEntry(1f, 1f));
        entries.add(new BarEntry(2f, 3f));
        entries.add(new BarEntry(3f, 1f));

        BarDataSet ds = new BarDataSet(entries, "");
        ds.setColors(
                ContextCompat.getColor(requireContext(), R.color.colorChipDocente),
                ContextCompat.getColor(requireContext(), R.color.colorChipEstudiante),
                ContextCompat.getColor(requireContext(), R.color.colorChipAdmin),
                ContextCompat.getColor(requireContext(), R.color.colorWarning)
        );
        ds.setValueTextSize(11f);
        ds.setValueTextColor(
                ContextCompat.getColor(requireContext(), R.color.colorOnSurface));

        BarData data = new BarData(ds);
        data.setBarWidth(0.5f);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"Tarea", "Proyecto", "Examen", "Trabajo"}));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant));
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(false);

        chart.getAxisLeft().setTextColor(
                ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant));
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(7f);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setEnabled(false);

        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);
        chart.setTouchEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setFitBars(true);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.animateY(700, Easing.EaseInOutQuad);
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
