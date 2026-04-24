import 'package:bloc/bloc.dart';
import 'package:meta/meta.dart';

import '../../../planning/data/datasources/planning_datasource.dart';
import '../../../planning/domain/models/commande.dart';
import '../../../planning/domain/models/itineraire.dart';
import '../../../planning/domain/models/tournee.dart';

part 'navigation_event.dart';

part 'navigation_state.dart';

class NavigationBloc extends Bloc<NavigationEvent, NavigationState> {
  final _datasource = PlanningDatasource();

  NavigationBloc() : super(NavigationInitial()) {
    on<NavigationLoadActive>(_onLoad);
  }

  Future<void> _onLoad(NavigationLoadActive event, Emitter<NavigationState> emit) async {
    emit(NavigationLoading());
    try {
      final tripsJson = await _datasource.getAllTrips();

      // Chercher la tournée en cours, sinon la prochaine planifiée
      final driverTrips = tripsJson
          .where((t) => t['chauffeurId'] == event.driverId)
          .toList();

      final activeJson = driverTrips.where((t) => t['statut'] == 'enCours').toList();
      final plannedJson = driverTrips.where((t) => t['statut'] == 'planifiee').toList();

      Map<String, dynamic>? tripJson;
      if (activeJson.isNotEmpty) {
        tripJson = activeJson.first;
      } else if (plannedJson.isNotEmpty) {
        // Prendre la plus proche en date
        plannedJson.sort((a, b) => a['date'].compareTo(b['date']));
        tripJson = plannedJson.first;
      }

      if (tripJson == null) {
        emit(NavigationEmpty());
        return;
      }

      final tournee = Tournee.fromJson(tripJson);

      // Charger les commandes
      final ordersJson = await _datasource.getAllOrders();
      tournee.commandes = ordersJson
          .map((o) => Commande.fromJson(o))
          .where((o) => o.tourneeId == tournee.id)
          .toList();

      // Charger l'itinéraire
      try {
        final itiJson = await _datasource.getItineraryByTripId(tournee.id);
        tournee.itineraire = Itineraire.fromJson(itiJson);
      } catch (_) {}

      emit(NavigationReady(tournee));
    } catch (e) {
      emit(NavigationError('Erreur chargement navigation : $e'));
    }
  }
}