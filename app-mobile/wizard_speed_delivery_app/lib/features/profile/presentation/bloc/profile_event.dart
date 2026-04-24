part of 'profile_bloc.dart';

@immutable
sealed class ProfileEvent {}
class ProfileLoadRequested extends ProfileEvent {
  final int userId;
  ProfileLoadRequested(this.userId);
}