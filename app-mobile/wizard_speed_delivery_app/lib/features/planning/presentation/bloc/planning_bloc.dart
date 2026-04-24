import 'package:bloc/bloc.dart';
import 'package:flutter/cupertino.dart';
import 'package:meta/meta.dart';

import '../../data/datasources/planning_datasource.dart';
import '../../domain/models/commande.dart';
import '../../domain/models/itineraire.dart';
import '../../domain/models/tournee.dart';

part 'planning_event.dart';
part 'planning_state.dart';

class PlanningBloc extends Bloc<PlanningEvent, PlanningState> {
  final _datasource = PlanningDatasource();

  PlanningBloc() : super(PlanningInitial()) {
    on<PlanningLoadRequested>(_onLoad);
  }

  Future<void> _onLoad(PlanningLoadRequested event, Emitter<PlanningState> emit) async {
    emit(PlanningLoading());
    try {
      final tripsJson = await _datasource.getAllTrips();
      
      final driverTrips = tripsJson
          .where((t) => t['chauffeurId'] == event.driverId)
          .map((t) => Tournee.fromJson(t))
          .toList();


      final ordersJson = await _datasource.getAllOrders();
      final allOrders = ordersJson.map((o) => Commande.fromJson(o)).toList();

      for (final tournee in driverTrips) {
        tournee.commandes = allOrders.where((o) => o.tourneeId == tournee.id).toList();
        try {
          final itiJson = await _datasource.getItineraryByTripId(tournee.id);
          tournee.itineraire = Itineraire.fromJson(itiJson);
        } catch (_) {}
      }

      driverTrips.sort((a, b) => b.date.compareTo(a.date));
      emit(PlanningLoaded(driverTrips));
    } catch (e) {
      print('PLANNING ERROR: $e');
      emit(PlanningError('Erreur chargement planning : $e'));
    }
  }
}