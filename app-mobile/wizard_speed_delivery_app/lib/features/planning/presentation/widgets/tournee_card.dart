import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../shared/enums/enums.dart';
import '../../domain/models/tournee.dart';

class TourneeCard extends StatelessWidget {
  final Tournee tournee;
  const TourneeCard({super.key, required this.tournee});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 14),
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(color: AppTheme.card, borderRadius: BorderRadius.circular(16)),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(children: [_statutPill(tournee.statut), const Spacer(),
          Text(_plageLabel(tournee.plageHoraire), style: const TextStyle(fontSize: 13, color: AppTheme.textSecondary, fontWeight: FontWeight.w500))]),
        const SizedBox(height: 14),
        Row(children: [
          _stat(Icons.place_outlined, '${tournee.nbStops} stops'),
          const SizedBox(width: 20),
          _stat(Icons.timer_outlined, tournee.itineraire?.dureeFormatee ?? '-'),
          const SizedBox(width: 20),
          _stat(Icons.inventory_2_outlined, '${tournee.commandes?.fold<int>(0, (s, c) => s + c.quantite) ?? 0} cartons'),
        ]),
        if (tournee.commandes != null && tournee.commandes!.isNotEmpty) ...[
          const SizedBox(height: 14), const Divider(color: AppTheme.divider, height: 1), const SizedBox(height: 12),
          ...tournee.commandes!.map((c) => Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: Row(children: [
              Container(width: 6, height: 6, decoration: BoxDecoration(
                  color: c.statut == StatutCommande.livree ? AppTheme.success : c.statut == StatutCommande.confirmee ? AppTheme.primaryLight : AppTheme.warning,
                  shape: BoxShape.circle)),
              const SizedBox(width: 10),
              Expanded(child: Text(c.adresseTexte, style: const TextStyle(fontSize: 13, color: AppTheme.textSecondary), maxLines: 1, overflow: TextOverflow.ellipsis)),
              Text('${c.quantite} crt', style: const TextStyle(fontSize: 12, color: AppTheme.textMuted, fontWeight: FontWeight.w500)),
            ]),
          )),
        ],
      ]),
    );
  }

  Widget _stat(IconData icon, String label) => Row(children: [
    Icon(icon, size: 16, color: AppTheme.textMuted), const SizedBox(width: 5),
    Text(label, style: const TextStyle(fontSize: 13, color: AppTheme.textSecondary)),
  ]);

  Widget _statutPill(StatutTournee statut) {
    final (Color c, String l) = switch (statut) {
      StatutTournee.planifiee => (AppTheme.primaryLight, 'Planifiée'),
      StatutTournee.enCours => (AppTheme.accent, 'En cours'),
      StatutTournee.terminee => (AppTheme.success, 'Terminée'),
      StatutTournee.annulee => (AppTheme.error, 'Annulée'),
    };
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 5),
      decoration: BoxDecoration(color: c.withOpacity(0.15), borderRadius: BorderRadius.circular(20)),
      child: Text(l, style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: c)),
    );
  }

  String _plageLabel(PlageHoraire p) => switch (p) {
    PlageHoraire.matin => '6h – 12h',
    PlageHoraire.apresMidi => '12h – 18h',
    PlageHoraire.journee => 'Journée',
  };
}