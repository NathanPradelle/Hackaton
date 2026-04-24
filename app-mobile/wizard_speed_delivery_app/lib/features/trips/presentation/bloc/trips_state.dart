part of 'trips_bloc.dart';

@immutable
sealed class TripsState {}

final class TripsInitial extends TripsState {}
class TripsLoading extends TripsState {}

class TripsLoaded extends TripsState {
  final List<Tournee> pastTrips;
  TripsLoaded(this.pastTrips);
}

class TripsError extends TripsState {
  final String message;
  TripsError(this.message);
}