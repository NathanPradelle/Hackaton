import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../shared/enums/enums.dart';
import '../../../planning/domain/models/tournee.dart';

class TripCard extends StatelessWidget {
  final Tournee tournee;
  final VoidCallback onTap;

  const TripCard({super.key, required this.tournee, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final totalCartons = tournee.commandes?.fold<int>(
        0, (s, c) => s + c.quantite) ?? 0;
    final totalPrix = tournee.commandes?.fold<double>(
        0, (s, c) => s + c.prix) ?? 0;
    final color = _statutColor(tournee.statut);

    return IntrinsicHeight(
      child: Container(
        margin: const EdgeInsets.only(bottom: 14),
        decoration: BoxDecoration(
            color: AppTheme.card, borderRadius: BorderRadius.circular(16)),
        child: Row(children: [
          // Barre statut
          Container(
            width: 4,
            decoration: BoxDecoration(
              color: color,
              borderRadius: const BorderRadius.only(
                  topLeft: Radius.circular(16),
                  bottomLeft: Radius.circular(16)),
            ),
          ),
          Expanded(
            child: Material(
              color: Colors.transparent,
              borderRadius: BorderRadius.circular(16),
              child: InkWell(
                borderRadius: BorderRadius.circular(16), onTap: onTap,
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Row(children: [
                      Container(padding: const EdgeInsets.all(10),
                          decoration: BoxDecoration(
                              color: color.withOpacity(0.12),
                              borderRadius: BorderRadius.circular(12)),
                          child: Icon(_statutIcon(tournee.statut), color: color,
                              size: 20)),
                      const SizedBox(width: 14),
                      Expanded(child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(_formatDate(tournee.date),
                                style: const TextStyle(fontSize: 15,
                                    fontWeight: FontWeight.w700,
                                    color: AppTheme.textPrimary)),
                            const SizedBox(height: 2),
                            Row(children: [
                              Text(tournee.plageHoraire, style: const TextStyle(
                                  fontSize: 13, color: AppTheme.textMuted)),
                              const SizedBox(width: 8),
                              _statutPill(tournee.statut),
                            ]),
                          ])),
                      const Icon(Icons.chevron_right_rounded,
                          color: AppTheme.textMuted, size: 22),
                    ]),
                    const SizedBox(height: 14),
                    const Divider(color: AppTheme.divider, height: 1),
                    const SizedBox(height: 14),
                    Row(children: [
                      _mini(Icons.place_outlined, '${tournee.nbStops} stops'),
                      _mini(Icons.timer_outlined,
                          tournee.itineraire?.dureeFormatee ?? '-'),
                      _mini(Icons.inventory_2_outlined, '$totalCartons crt'),
                      _mini(Icons.euro_rounded,
                          '${totalPrix.toStringAsFixed(0)} €'),
                    ]),
                  ]),
                ),
              ),
            ),
          ),
        ]),
      ),
    );
  }

  Widget _mini(IconData icon, String label) =>
      Expanded(child: Row(children: [
        Icon(icon, size: 14, color: AppTheme.textMuted),
        const SizedBox(width: 4),
        Flexible(child: Text(label,
            style: const TextStyle(fontSize: 12, color: AppTheme.textSecondary),
            overflow: TextOverflow.ellipsis)),
      ]));

  Widget _statutPill(StatutTournee statut) {
    final color = _statutColor(statut);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(color: color.withOpacity(0.15),
          borderRadius: BorderRadius.circular(10)),
      child: Text(_statutLabel(statut), style: TextStyle(
          fontSize: 10, fontWeight: FontWeight.w600, color: color)),
    );
  }

  Color _statutColor(StatutTournee statut) =>
      switch (statut) {
        StatutTournee.enCours => AppTheme.accent,
        StatutTournee.planifiee => AppTheme.primaryLight,
        StatutTournee.terminee => AppTheme.success,
        StatutTournee.annulee => AppTheme.error,
      };

  String _statutLabel(StatutTournee statut) =>
      switch (statut) {
        StatutTournee.enCours => 'En cours',
        StatutTournee.planifiee => 'Planifiée',
        StatutTournee.terminee => 'Terminée',
        StatutTournee.annulee => 'Annulée',
      };

  IconData _statutIcon(StatutTournee statut) =>
      switch (statut) {
        StatutTournee.enCours => Icons.local_shipping_rounded,
        StatutTournee.planifiee => Icons.schedule_rounded,
        StatutTournee.terminee => Icons.check_rounded,
        StatutTournee.annulee => Icons.cancel_rounded,
      };

  String _formatDate(DateTime d) {
    const j = ['Lun','Mar','Mer','Jeu','Ven','Sam','Dim'];
    const m = ['janv.','fév.','mars','avr.','mai','juin','juil.','août','sept.','oct.','nov.','déc.'];
    return '${j[d.weekday - 1]} ${d.day} ${m[d.month - 1]} ${d.year}';
  }
}