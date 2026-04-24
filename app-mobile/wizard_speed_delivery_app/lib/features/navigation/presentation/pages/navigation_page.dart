import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../../core/service/api_service.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../shared/enums/enums.dart';
import '../../../planning/domain/models/tournee.dart';
import '../bloc/navigation_bloc.dart';

class NavigationPage extends StatelessWidget {
  const NavigationPage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) {
        final driverId = ApiService().driverId ?? 0;
        return NavigationBloc()..add(NavigationLoadActive(driverId));
      },
      child: Scaffold(
        body: BlocBuilder<NavigationBloc, NavigationState>(
          builder: (context, state) {
            if (state is NavigationLoading) {
              return const Center(
                child: CircularProgressIndicator(color: AppTheme.accent),
              );
            }
            if (state is NavigationError) {
              return Center(
                child: Text(
                  state.message,
                  style: const TextStyle(color: AppTheme.textMuted),
                ),
              );
            }
            if (state is NavigationEmpty) {
              return Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.navigation_rounded,
                      size: 48,
                      color: AppTheme.textMuted.withOpacity(0.4),
                    ),
                    const SizedBox(height: 12),
                    Text(
                      'Aucune tournée active',
                      style: TextStyle(
                        fontSize: 15,
                        color: AppTheme.textMuted.withOpacity(0.6),
                      ),
                    ),
                  ],
                ),
              );
            }
            if (state is NavigationReady) {
              return _buildNavigation(context, state.activeTournee);
            }
            return const SizedBox.shrink();
          },
        ),
      ),
    );
  }

  Widget _buildNavigation(BuildContext context, Tournee active) {
    return Stack(
      children: [
        // Map placeholder
        Container(
          width: double.infinity,
          height: double.infinity,
          color: AppTheme.surfaceLight,
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.map_rounded,
                  size: 64,
                  color: AppTheme.textMuted.withOpacity(0.3),
                ),
                const SizedBox(height: 16),
                Text(
                  'Mapbox Navigation',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w600,
                    color: AppTheme.textMuted.withOpacity(0.5),
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  'Intégrer mapbox_maps_flutter ici',
                  style: TextStyle(
                    fontSize: 13,
                    color: AppTheme.textMuted.withOpacity(0.35),
                  ),
                ),
              ],
            ),
          ),
        ),
        // Top bar
        Positioned(
          top: 0,
          left: 0,
          right: 0,
          child: Container(
            padding: EdgeInsets.fromLTRB(
              20,
              MediaQuery.of(context).padding.top + 12,
              20,
              16,
            ),
            decoration: BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: [AppTheme.surface, AppTheme.surface.withOpacity(0.0)],
              ),
            ),
            child: Row(
              children: [
                Container(
                  padding: const EdgeInsets.all(10),
                  decoration: BoxDecoration(
                    color: AppTheme.card,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: const Icon(
                    Icons.arrow_back_rounded,
                    color: AppTheme.textPrimary,
                    size: 20,
                  ),
                ),
                const Spacer(),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 14,
                    vertical: 8,
                  ),
                  decoration: BoxDecoration(
                    color: active.statut == StatutTournee.enCours
                        ? AppTheme.accent.withOpacity(0.2)
                        : AppTheme.card,
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Row(
                    children: [
                      Icon(
                        active.statut == StatutTournee.enCours
                            ? Icons.navigation_rounded
                            : Icons.schedule_rounded,
                        size: 16,
                        color: active.statut == StatutTournee.enCours
                            ? AppTheme.accent
                            : AppTheme.textSecondary,
                      ),
                      const SizedBox(width: 6),
                      Text(
                        active.statut == StatutTournee.enCours
                            ? 'Navigation active'
                            : 'En attente',
                        style: TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.w600,
                          color: active.statut == StatutTournee.enCours
                              ? AppTheme.accent
                              : AppTheme.textSecondary,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
        // Bottom panel
        Positioned(
          bottom: 0,
          left: 0,
          right: 0,
          child: Container(
            padding: EdgeInsets.fromLTRB(
              20,
              20,
              20,
              MediaQuery.of(context).padding.bottom + 20,
            ),
            decoration: BoxDecoration(
              color: AppTheme.card,
              borderRadius: const BorderRadius.vertical(
                top: Radius.circular(24),
              ),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.3),
                  blurRadius: 20,
                  offset: const Offset(0, -4),
                ),
              ],
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Center(
                  child: Container(
                    width: 40,
                    height: 4,
                    decoration: BoxDecoration(
                      color: AppTheme.divider,
                      borderRadius: BorderRadius.circular(2),
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text(
                            'Prochaine livraison',
                            style: TextStyle(
                              fontSize: 12,
                              color: AppTheme.textMuted,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            active.commandes?.isNotEmpty == true
                                ? active.commandes!.first.adresseTexte
                                : 'Aucune commande',
                            style: const TextStyle(
                              fontSize: 15,
                              fontWeight: FontWeight.w700,
                              color: AppTheme.textPrimary,
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ],
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 14,
                        vertical: 8,
                      ),
                      decoration: BoxDecoration(
                        color: AppTheme.surfaceLight,
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Text(
                        '${active.nbStops} stops',
                        style: const TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.w600,
                          color: AppTheme.textSecondary,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                if (active.commandes != null)
                  ...active.commandes!
                      .take(3)
                      .map(
                        (c) => Padding(
                          padding: const EdgeInsets.only(bottom: 8),
                          child: Row(
                            children: [
                              Container(
                                width: 8,
                                height: 8,
                                decoration: BoxDecoration(
                                  color: c.statut == StatutCommande.livree
                                      ? AppTheme.success
                                      : AppTheme.accent,
                                  shape: BoxShape.circle,
                                ),
                              ),
                              const SizedBox(width: 10),
                              Expanded(
                                child: Text(
                                  c.adresseTexte,
                                  style: const TextStyle(
                                    fontSize: 13,
                                    color: AppTheme.textSecondary,
                                  ),
                                  maxLines: 1,
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ),
                              Text(
                                '${c.quantite} crt',
                                style: const TextStyle(
                                  fontSize: 12,
                                  color: AppTheme.textMuted,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                const SizedBox(height: 14),
                SizedBox(
                  width: double.infinity,
                  height: 52,
                  child: ElevatedButton.icon(
                    onPressed: () => ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: const Text(
                          'Navigation Mapbox — prochaine étape',
                        ),
                        backgroundColor: AppTheme.primaryLight,
                        behavior: SnackBarBehavior.floating,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10),
                        ),
                      ),
                    ),
                    icon: const Icon(Icons.navigation_rounded, size: 20),
                    label: const Text('Démarrer la navigation'),
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
