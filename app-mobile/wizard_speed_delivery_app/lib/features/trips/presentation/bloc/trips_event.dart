part of 'trips_bloc.dart';

@immutable
sealed class TripsEvent {}
class TripsLoadRequested extends TripsEvent {
  final int driverId;
  TripsLoadRequested(this.driverId);
}