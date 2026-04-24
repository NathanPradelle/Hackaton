part of 'navigation_bloc.dart';

@immutable
sealed class NavigationState {}

final class NavigationInitial extends NavigationState {}

class NavigationLoading extends NavigationState {}

class NavigationReady extends NavigationState {
  final Tournee activeTournee;

  NavigationReady(this.activeTournee);
}

class NavigationEmpty extends NavigationState {}

class NavigationError extends NavigationState {
  final String message;

  NavigationError(this.message);
}
