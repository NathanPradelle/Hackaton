import 'package:bloc/bloc.dart';
import 'package:flutter/cupertino.dart';
import 'package:meta/meta.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../../core/service/api_service.dart';
import '../../../../shared/enums/enums.dart';
import '../../data/datasources/auth_datasource.dart';
import '../../domain/models/user.dart';

part 'auth_event.dart';

part 'auth_state.dart';

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final _datasource = AuthDatasource();

  AuthBloc() : super(AuthInitial()) {
    on<AuthLoginRequested>(_onLogin);
    on<AuthLogoutRequested>(_onLogout);
  }

  Future<void> _onLogin(
    AuthLoginRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    try {
      final json = await _datasource.login(event.identifiant, event.password);
      debugPrint('=== LOGIN RAW JSON ===');
      debugPrint('$json');

      // Le user est imbriqué dans la clé "user"
      final userJson = json['user'] as Map<String, dynamic>?;
      if (userJson == null) {
        emit(AuthFailure('Réponse serveur invalide'));
        return;
      }

      // Le token est à la racine (c'est le userId dans ton cas)
      final token = json['token'];
      debugPrint('token=$token, user=$userJson');

      final user = User.fromJson(userJson);

      // Stocker le user
      ApiService().currentUser = userJson;

      // Résoudre le driverId
      final driverId = await _datasource.resolveDriverId(user.id);
      debugPrint('driverId résolu: $driverId');

      if (driverId == null) {
        emit(AuthFailure('Profil chauffeur introuvable'));
        return;
      }
      ApiService().driverId = driverId;

      emit(AuthSuccess(user, driverId));
    } on ApiException catch (e) {
      debugPrint('LOGIN ApiException: ${e.message}');
      emit(AuthFailure(e.message));
    } catch (e) {
      debugPrint('LOGIN Exception: $e');
      emit(AuthFailure('Impossible de contacter le serveur'));
    }
  }

  Future<void> _onLogout(
    AuthLogoutRequested event,
    Emitter<AuthState> emit,
  ) async {
    ApiService().currentUser = null;
    emit(AuthInitial());
  }
}
