import 'package:flutter/material.dart';
import '../../../../core/service/api_service.dart';
import '../../../../core/theme/app_theme.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../bloc/trips_bloc.dart';
import '../widgets/trip_card.dart';
import '../widgets/trip_details_sheet.dart';


class TripsPage extends StatelessWidget {
  const TripsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) {
        final driverId = ApiService().driverId ?? 0;
        return TripsBloc()..add(TripsLoadRequested(driverId));
      },
      child: Scaffold(
        appBar: AppBar(title: const Text('Mes trajets')),
        body: BlocBuilder<TripsBloc, TripsState>(
          builder: (context, state) {
            if (state is TripsLoading) {
              return const Center(
                child: CircularProgressIndicator(color: AppTheme.accent),
              );
            }
            if (state is TripsError) {
              return Center(
                child: Text(
                  state.message,
                  style: const TextStyle(color: AppTheme.textMuted),
                ),
              );
            }
            if (state is TripsLoaded) {
              if (state.pastTrips.isEmpty) {
                return Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.route_rounded,
                        size: 48,
                        color: AppTheme.textMuted.withOpacity(0.4),
                      ),
                      const SizedBox(height: 12),
                      Text(
                        'Aucun trajet effectué',
                        style: TextStyle(
                          fontSize: 15,
                          color: AppTheme.textMuted.withOpacity(0.6),
                        ),
                      ),
                    ],
                  ),
                );
              }
              return ListView.builder(
                padding: const EdgeInsets.all(20),
                itemCount: state.pastTrips.length,
                itemBuilder: (_, i) => TripCard(
                  tournee: state.pastTrips[i],
                  onTap: () => showTripDetailSheet(context, state.pastTrips[i]),
                ),
              );
            }
            return const SizedBox.shrink();
          },
        ),
      ),
    );
  }
}
