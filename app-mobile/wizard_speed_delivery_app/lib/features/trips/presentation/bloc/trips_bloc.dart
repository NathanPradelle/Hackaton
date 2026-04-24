import 'package:bloc/bloc.dart';
import 'package:meta/meta.dart';

import '../../../../shared/enums/enums.dart';
import '../../../planning/data/datasources/planning_datasource.dart';
import '../../../planning/domain/models/commande.dart';
import '../../../planning/domain/models/itineraire.dart';
import '../../../planning/domain/models/tournee.dart';

part 'trips_event.dart';

part 'trips_state.dart';

class TripsBloc extends Bloc<TripsEvent, TripsState> {
  final _datasource = PlanningDatasource();

  TripsBloc() : super(TripsInitial()) {
    on<TripsLoadRequested>(_onLoad);
  }

  Future<void> _onLoad(TripsLoadRequested event, Emitter<TripsState> emit) async {
    emit(TripsLoading());
    try {
      final tripsJson = await _datasource.getAllTrips();
      final allTrips = tripsJson
          .where((t) => t['chauffeurId'] == event.driverId)
          .map((t) => Tournee.fromJson(t))
          .toList();

      final ordersJson = await _datasource.getAllOrders();
      final allOrders = ordersJson.map((o) => Commande.fromJson(o)).toList();

      for (final trip in allTrips) {
        trip.commandes = allOrders.where((o) => o.tourneeId == trip.id).toList();
        try {
          final itiJson = await _datasource.getItineraryByTripId(trip.id);
          trip.itineraire = Itineraire.fromJson(itiJson);
        } catch (_) {}
      }

      // Tri : en cours d'abord, puis planifiées, puis terminées, puis annulées
      // Dans chaque groupe, tri par date décroissante
      allTrips.sort((a, b) {
        final priorityA = _statutPriority(a.statut);
        final priorityB = _statutPriority(b.statut);
        if (priorityA != priorityB) return priorityA.compareTo(priorityB);
        return b.date.compareTo(a.date);
      });

      emit(TripsLoaded(allTrips));
    } catch (e) {
      emit(TripsError('Erreur chargement trajets : $e'));
    }
  }

  int _statutPriority(StatutTournee statut) => switch (statut) {
    StatutTournee.enCours => 0,
    StatutTournee.planifiee => 1,
    StatutTournee.terminee => 2,
    StatutTournee.annulee => 3,
  };
}