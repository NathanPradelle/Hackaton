import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../shared/enums/enums.dart';
import '../../domain/models/tournee.dart';

class TourneeCard extends StatelessWidget {
  final Tournee tournee;
  const TourneeCard({super.key, required this.tournee});

  @override
  Widget build(BuildContext context) {
    final statusColor = _statutColor(tournee.statut);
    final totalCartons = tournee.commandes?.fold<int>(0, (s, c) => s + c.quantite) ?? 0;

    return Container(
      margin: const EdgeInsets.only(bottom: 14),
      decoration: BoxDecoration(
        color: AppTheme.card,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(children: [
        // Barre de statut colorée à gauche
        Container(
          width: 4,
          height: 140,
          decoration: BoxDecoration(
            color: statusColor,
            borderRadius: const BorderRadius.only(
              topLeft: Radius.circular(16),
              bottomLeft: Radius.circular(16),
            ),
          ),
        ),
        // Contenu
        Expanded(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              // Header : statut + plage horaire
              Row(children: [
                _statutPill(tournee.statut),
                const Spacer(),
                Icon(_statutIcon(tournee.statut), size: 14, color: statusColor),
                const SizedBox(width: 6),
                Text(tournee.plageHoraire,
                    style: TextStyle(fontSize: 13, color: statusColor, fontWeight: FontWeight.w600)),
              ]),
              const SizedBox(height: 12),

              // Stats row
              Row(children: [
                _stat(Icons.place_outlined, '${tournee.nbStops} stops'),
                const SizedBox(width: 16),
                _stat(Icons.timer_outlined, tournee.itineraire?.dureeFormatee ?? '-'),
                const SizedBox(width: 16),
                _stat(Icons.inventory_2_outlined, '$totalCartons crt'),
              ]),

              // Commandes
              if (tournee.commandes != null && tournee.commandes!.isNotEmpty) ...[
                const SizedBox(height: 12),
                const Divider(color: AppTheme.divider, height: 1),
                const SizedBox(height: 10),
                ...tournee.commandes!.take(4).map((c) => Padding(
                  padding: const EdgeInsets.only(bottom: 6),
                  child: Row(children: [
                    Container(width: 8, height: 8, decoration: BoxDecoration(
                      color: _commandeColor(c.statut),
                      shape: BoxShape.circle,
                      boxShadow: [BoxShadow(color: _commandeColor(c.statut).withOpacity(0.4), blurRadius: 4)],
                    )),
                    const SizedBox(width: 10),
                    Expanded(child: Text(c.adresseTexte,
                        style: const TextStyle(fontSize: 13, color: AppTheme.textSecondary),
                        maxLines: 1, overflow: TextOverflow.ellipsis)),
                    const SizedBox(width: 8),
                    Text('${c.quantite} crt',
                        style: const TextStyle(fontSize: 12, color: AppTheme.textMuted, fontWeight: FontWeight.w500)),
                  ]),
                )),
                if (tournee.commandes!.length > 4)
                  Padding(
                    padding: const EdgeInsets.only(top: 4),
                    child: Text('+${tournee.commandes!.length - 4} autres',
                        style: TextStyle(fontSize: 12, color: AppTheme.textMuted.withOpacity(0.7))),
                  ),
              ],
            ]),
          ),
        ),
      ]),
    );
  }

  Widget _stat(IconData icon, String label) => Row(children: [
    Icon(icon, size: 15, color: AppTheme.textMuted), const SizedBox(width: 4),
    Text(label, style: const TextStyle(fontSize: 13, color: AppTheme.textSecondary)),
  ]);

  Widget _statutPill(StatutTournee statut) {
    final color = _statutColor(statut);
    final label = _statutLabel(statut);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 5),
      decoration: BoxDecoration(
        color: color.withOpacity(0.15),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: color.withOpacity(0.3), width: 0.5),
      ),
      child: Text(label, style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: color)),
    );
  }

  Color _statutColor(StatutTournee statut) => switch (statut) {
    StatutTournee.planifiee => AppTheme.primaryLight,
    StatutTournee.enCours => AppTheme.accent,
    StatutTournee.terminee => AppTheme.success,
    StatutTournee.annulee => AppTheme.error,
  };

  String _statutLabel(StatutTournee statut) => switch (statut) {
    StatutTournee.planifiee => 'Planifiée',
    StatutTournee.enCours => 'En cours',
    StatutTournee.terminee => 'Terminée',
    StatutTournee.annulee => 'Annulée',
  };

  IconData _statutIcon(StatutTournee statut) => switch (statut) {
    StatutTournee.planifiee => Icons.schedule_rounded,
    StatutTournee.enCours => Icons.local_shipping_rounded,
    StatutTournee.terminee => Icons.check_circle_rounded,
    StatutTournee.annulee => Icons.cancel_rounded,
  };

  Color _commandeColor(StatutCommande statut) => switch (statut) {
    StatutCommande.enAttente => AppTheme.warning,
    StatutCommande.confirmee => AppTheme.primaryLight,
    StatutCommande.enCours => AppTheme.accent,
    StatutCommande.livree => AppTheme.success,
    StatutCommande.annulee => AppTheme.error,
  };
}