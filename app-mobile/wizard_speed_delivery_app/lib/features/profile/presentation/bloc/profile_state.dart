part of 'profile_bloc.dart';

@immutable
sealed class ProfileState {}

final class ProfileInitial extends ProfileState {}
class ProfileLoading extends ProfileState {}

class ProfileLoaded extends ProfileState {
  final Chauffeur chauffeur;
  final Camion? camion;
  ProfileLoaded({required this.chauffeur, this.camion});
}

class ProfileError extends ProfileState {
  final String message;
  ProfileError(this.message);
}