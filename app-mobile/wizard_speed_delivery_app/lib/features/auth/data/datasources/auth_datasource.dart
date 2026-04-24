import '../../../../core/service/api_service.dart';

class AuthDatasource {
  final _api = ApiService();

  // route POST login
  Future<Map<String, dynamic>> login(String identifier, String password) async {
    final result = await _api.post('/auth/login', {
      'identifier': identifier,
      'password': password,
    });
    return result as Map<String, dynamic>;
  }

  Future<int?> resolveDriverId(int userId) async {
    final list = await _api.get('/drivers') as List;
    for (final driver in list) {
      if (driver['userId'] == userId) {
        return driver['id'] as int;
      }
    }
    return null;
  }
}
