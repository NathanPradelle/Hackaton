import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../shared/enums/enums.dart';
import '../../../planning/domain/models/tournee.dart';

void showTripDetailSheet(BuildContext context, Tournee tournee) {
  const j = ['Lun','Mar','Mer','Jeu','Ven','Sam','Dim'];
  const m = ['janv.','fév.','mars','avr.','mai','juin','juil.','août','sept.','oct.','nov.','déc.'];
  final d = tournee.date;
  final dateStr = '${j[d.weekday - 1]} ${d.day} ${m[d.month - 1]} ${d.year}';
  final plage = switch (tournee.plageHoraire) { PlageHoraire.matin => '6h – 12h', PlageHoraire.apresMidi => '12h – 18h', PlageHoraire.journee => 'Journée' };

  showModalBottomSheet(
    context: context, backgroundColor: AppTheme.card, isScrollControlled: true,
    shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(20))),
    builder: (ctx) => DraggableScrollableSheet(
      expand: false, initialChildSize: 0.6, maxChildSize: 0.85,
      builder: (_, scroll) => SingleChildScrollView(
        controller: scroll, padding: const EdgeInsets.all(24),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Center(child: Container(width: 40, height: 4, decoration: BoxDecoration(color: AppTheme.divider, borderRadius: BorderRadius.circular(2)))),
          const SizedBox(height: 20),
          Text('Tournée du $dateStr', style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w800, color: AppTheme.textPrimary)),
          const SizedBox(height: 6),
          Text('$plage · ${tournee.itineraire?.dureeFormatee ?? '-'} · ${tournee.nbStops} arrêts',
              style: const TextStyle(fontSize: 14, color: AppTheme.textSecondary)),
          const SizedBox(height: 24),
          if (tournee.commandes != null)
            ...tournee.commandes!.asMap().entries.map((entry) {
              final i = entry.key;
              final c = entry.value;
              final isLast = i == tournee.commandes!.length - 1;
              return Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Column(children: [
                  Container(width: 28, height: 28,
                      decoration: BoxDecoration(color: AppTheme.success.withOpacity(0.15), shape: BoxShape.circle),
                      child: Center(child: Text('${i + 1}', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppTheme.success)))),
                  if (!isLast) Container(width: 2, height: 50, color: AppTheme.divider),
                ]),
                const SizedBox(width: 14),
                Expanded(child: Padding(padding: const EdgeInsets.only(bottom: 20), child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text(c.adresseTexte, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w600, color: AppTheme.textPrimary)),
                  const SizedBox(height: 4),
                  Text('${c.quantite} cartons · ${c.prix.toStringAsFixed(2)} €', style: const TextStyle(fontSize: 13, color: AppTheme.textMuted)),
                ],
                ))),
              ]);
            }),
        ]),
      ),
    ),
  );
}