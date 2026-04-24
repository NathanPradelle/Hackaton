part of 'navigation_bloc.dart';

@immutable
sealed class NavigationEvent {}

class NavigationLoadActive extends NavigationEvent {
  final int driverId;

  NavigationLoadActive(this.driverId);
}
