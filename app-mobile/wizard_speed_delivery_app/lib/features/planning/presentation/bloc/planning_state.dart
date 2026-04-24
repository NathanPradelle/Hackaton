part of 'planning_bloc.dart';

@immutable
sealed class PlanningState {}

final class PlanningInitial extends PlanningState {}
class PlanningLoading extends PlanningState {}

class PlanningLoaded extends PlanningState {
  final List<Tournee> tournees;
  PlanningLoaded(this.tournees);
}

class PlanningError extends PlanningState {
  final String message;
  PlanningError(this.message);
}