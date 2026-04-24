part of 'planning_bloc.dart';

@immutable
sealed class PlanningEvent {}

class PlanningLoadRequested extends PlanningEvent {
  final int driverId;
  PlanningLoadRequested(this.driverId); // on récupère id chauffeur
}
