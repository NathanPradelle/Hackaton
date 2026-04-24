part of 'auth_bloc.dart';

@immutable
sealed class AuthState {}

class AuthInitial extends AuthState {}

class AuthLoading extends AuthState {}

class AuthSuccess extends AuthState {
  final User user;
  final int driverId;
  AuthSuccess(this.user, this.driverId);
}

class AuthFailure extends AuthState {
  final String message;

  AuthFailure(this.message);
}
