import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';
import '../../domain/models/camion.dart';
import '../../domain/models/model_camion.dart';

class FuelGauge extends StatelessWidget {
  final Camion camion;
  final ModeleCamion modele;

  const FuelGauge({super.key, required this.camion, required this.modele});

  @override
  Widget build(BuildContext context) {
    final percent = camion.quantiteEssence / modele.capaciteReservoir;
    final color = percent > 0.4 ? AppTheme.success : percent > 0.2 ? AppTheme.warning : AppTheme.error;

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(color: AppTheme.card, borderRadius: BorderRadius.circular(16)),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(children: [
          const Icon(Icons.local_gas_station_rounded, color: AppTheme.accent, size: 20),
          const SizedBox(width: 10),
          const Text('Niveau de carburant', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w700, color: AppTheme.textPrimary)),
          const Spacer(),
          Text('${camion.quantiteEssence.toInt()} / ${modele.capaciteReservoir.toInt()} L',
              style: TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: color)),
        ]),
        const SizedBox(height: 16),
        ClipRRect(
          borderRadius: BorderRadius.circular(6),
          child: LinearProgressIndicator(value: percent, minHeight: 10,
              backgroundColor: AppTheme.surfaceLight, valueColor: AlwaysStoppedAnimation<Color>(color)),
        ),
        const SizedBox(height: 8),
        Text('${(percent * 100).toInt()}% · Autonomie estimée : ${(camion.quantiteEssence / modele.consommationEssence * 100).toInt()} km',
            style: const TextStyle(fontSize: 12, color: AppTheme.textMuted)),
      ]),
    );
  }
}