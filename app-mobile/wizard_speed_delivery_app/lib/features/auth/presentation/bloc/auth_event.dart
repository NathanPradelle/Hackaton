part of 'auth_bloc.dart';

@immutable
sealed class AuthEvent {}

class AuthLoginRequested extends AuthEvent {
  final String identifiant;
  final String password;
  AuthLoginRequested({required this.identifiant, required this.password});
}

class AuthLogoutRequested extends AuthEvent {}