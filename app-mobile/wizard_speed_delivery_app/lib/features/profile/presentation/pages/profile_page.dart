import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../../core/service/api_service.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../shared/enums/enums.dart';
import '../bloc/profile_bloc.dart';
import '../widgets/info_section.dart';
import '../widgets/fuel_gauge.dart';

class ProfilePage extends StatelessWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) {
        final userId = ApiService().currentUser?['id'] ?? 0;
        return ProfileBloc()..add(ProfileLoadRequested(userId));
      },
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Mon profil'),
          actions: [
            IconButton(
              icon: const Icon(Icons.logout_rounded, size: 22),
              onPressed: () => _showLogoutDialog(context),
            ),
          ],
        ),
        body: BlocBuilder<ProfileBloc, ProfileState>(
          builder: (context, state) {
            if (state is ProfileLoading) {
              return const Center(child: CircularProgressIndicator(color: AppTheme.accent));
            }
            if (state is ProfileError) {
              return Center(child: Text(state.message, style: const TextStyle(color: AppTheme.textMuted)));
            }
            if (state is ProfileLoaded) {
              final chauffeur = state.chauffeur;
              final camion = state.camion;
              final modele = camion?.modele;

              return SingleChildScrollView(
                padding: const EdgeInsets.all(20),
                child: Column(children: [
                  Container(
                    width: 88, height: 88,
                    decoration: BoxDecoration(color: AppTheme.primaryLight, borderRadius: BorderRadius.circular(22)),
                    child: Center(child: Text(
                      '${chauffeur.prenom[0]}${chauffeur.nom[0]}',
                      style: const TextStyle(fontSize: 32, fontWeight: FontWeight.w800, color: Colors.white, letterSpacing: 2),
                    )),
                  ),
                  const SizedBox(height: 16),
                  Text(chauffeur.nomComplet, style: const TextStyle(fontSize: 24, fontWeight: FontWeight.w800, color: AppTheme.textPrimary)),
                  const SizedBox(height: 4),
                  Text('Chauffeur Poids Lourd', style: TextStyle(fontSize: 14, color: AppTheme.textMuted, letterSpacing: 0.5)),
                  const SizedBox(height: 16),
                  _statutBadge(chauffeur.statut),
                  const SizedBox(height: 28),

                  InfoSection(title: 'Informations personnelles', icon: Icons.person_outline_rounded, rows: {
                    'Nom': chauffeur.nom,
                    'Prénom': chauffeur.prenom,
                    'Identifiant': ApiService().currentUser?['identifiant'] ?? '-',
                    'N° Permis': chauffeur.numeroPermis,
                  }),

                  if (camion != null && modele != null) ...[
                    const SizedBox(height: 20),
                    InfoSection(title: 'Véhicule assigné', icon: Icons.local_shipping_outlined, rows: {
                      'Plaque': camion.plaqueImmatriculation,
                      'Marque': modele.marque,
                      'Modèle': modele.nomModele,
                      'Capacité': '${modele.capacite.toInt()} cartons',
                      'Carburant': _fuelLabel(modele.typeEssence),
                      'Réservoir': '${modele.capaciteReservoir.toInt()} L',
                      'Consommation': '${modele.consommationEssence} L/100km',
                    }),
                    const SizedBox(height: 20),
                    FuelGauge(camion: camion, modele: modele),
                  ],
                  const SizedBox(height: 40),
                ]),
              );
            }
            return const SizedBox.shrink();
          },
        ),
      ),
    );
  }

  Widget _statutBadge(StatutChauffeur statut) {
    final (Color color, String label, IconData icon) = switch (statut) {
      StatutChauffeur.disponible => (AppTheme.success, 'Disponible', Icons.check_circle_outline_rounded),
      StatutChauffeur.enCours => (AppTheme.accent, 'En livraison', Icons.local_shipping_rounded),
      StatutChauffeur.pause => (AppTheme.warning, 'En pause', Icons.pause_circle_outline_rounded),
      StatutChauffeur.indisponible => (AppTheme.error, 'Indisponible', Icons.cancel_outlined),
    };
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
      decoration: BoxDecoration(
        color: color.withOpacity(0.12), borderRadius: BorderRadius.circular(30),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Row(mainAxisSize: MainAxisSize.min, children: [
        Icon(icon, color: color, size: 18), const SizedBox(width: 8),
        Text(label, style: TextStyle(color: color, fontWeight: FontWeight.w600, fontSize: 14)),
      ]),
    );
  }

  String _fuelLabel(TypeEssence t) => switch (t) {
    TypeEssence.diesel => 'Diesel',
    TypeEssence.essence => 'Essence',
    TypeEssence.electrique => 'Électrique',
    TypeEssence.gnv => 'GNV',
  };

  void _showLogoutDialog(BuildContext context) {
    showDialog(context: context, builder: (ctx) => AlertDialog(
      backgroundColor: AppTheme.card,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      title: const Text('Se déconnecter ?', style: TextStyle(color: AppTheme.textPrimary)),
      content: const Text('Vous serez redirigé vers la page de connexion.', style: TextStyle(color: AppTheme.textSecondary)),
      actions: [
        TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Annuler', style: TextStyle(color: AppTheme.textMuted))),
        TextButton(onPressed: () { Navigator.pop(ctx); Navigator.of(context).pushReplacementNamed('/login'); },
            child: const Text('Déconnexion', style: TextStyle(color: AppTheme.error))),
      ],
    ));
  }
}