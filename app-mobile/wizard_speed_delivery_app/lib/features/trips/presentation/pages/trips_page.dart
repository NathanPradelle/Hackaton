import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../shared/enums/enums.dart';
import '../../../../local/mock/mock_data.dart';
import '../../../planning/domain/models/tournee.dart';
import '../widgets/trip_card.dart';
import '../widgets/trip_details_sheet.dart';

class TripsPage extends StatelessWidget {
  const TripsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final past =
        MockData.tournees
            .where((t) => t.statut == StatutTournee.terminee)
            .toList()
          ..sort((a, b) => b.date.compareTo(a.date));

    return Scaffold(
      appBar: AppBar(title: const Text('Mes trajets')),
      body: past.isEmpty
          ? Center(
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
            )
          : ListView.builder(
              padding: const EdgeInsets.all(20),
              itemCount: past.length,
              itemBuilder: (_, i) => TripCard(
                tournee: past[i],
                onTap: () => showTripDetailSheet(context, past[i]),
              ),
            ),
    );
  }
}
