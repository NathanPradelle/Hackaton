import 'package:bloc/bloc.dart';
import 'package:meta/meta.dart';

import '../../data/datasources/profile_datasource.dart';
import '../../domain/models/camion.dart';
import '../../domain/models/chauffeur.dart';

part 'profile_event.dart';
part 'profile_state.dart';

class ProfileBloc extends Bloc<ProfileEvent, ProfileState> {
  final _datasource = ProfileDatasource();

  ProfileBloc() : super(ProfileInitial()) {
    on<ProfileLoadRequested>(_onLoad);
  }

  Future<void> _onLoad(ProfileLoadRequested event, Emitter<ProfileState> emit) async {
    emit(ProfileLoading());
    try {
      // 1. Récupérer le chauffeur par userId
      final driverJson = await _datasource.getDriverByUserId(event.userId);
      if (driverJson == null) {
        emit(ProfileError('Profil chauffeur introuvable'));
        return;
      }
      final chauffeur = Chauffeur.fromJson(driverJson);

      // 2. Trouver la tournée active/planifiée pour connaître le camion assigné
      Camion? camion;
      final trips = await _datasource.getAllTrips();
      final activeTrip = trips.cast<Map<String, dynamic>>().where((t) =>
      t['chauffeurId'] == chauffeur.id &&
          (t['statut'] == 'enCours' || t['statut'] == 'planifiee')).toList();

      if (activeTrip.isNotEmpty) {
        final truckId = activeTrip.first['camionId'];
        final trucks = await _datasource.getAllTrucks();
        final truckJson = trucks.firstWhere((t) => t['id'] == truckId, orElse: () => <String, dynamic>{});
        if (truckJson.isNotEmpty) {
          camion = Camion.fromJson(truckJson);
        }
      }

      if (camion == null) {
        final trucks = await _datasource.getAllTrucks();
        if (trucks.isNotEmpty) {
          camion = Camion.fromJson(trucks.first);
        }
      }

      emit(ProfileLoaded(chauffeur: chauffeur, camion: camion));
    } catch (e) {
      emit(ProfileError('Erreur chargement profil : $e'));
    }
  }
}