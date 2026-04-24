import '../../../../core/service/api_service.dart';

class ProfileDatasource {
  final _api = ApiService();

  /// GET /api/drivers — on filtre par userId côté Flutter
  Future<Map<String, dynamic>?> getDriverByUserId(int userId) async {
    final list = await _api.get('/drivers') as List;
    try {
      return list.firstWhere((d) => d['userId'] == userId) as Map<String, dynamic>;
    } catch (_) {
      return null;
    }
  }

  /// GET /api/trucks — on récupère tous les camions
  /// Le back ne donne pas d'endpoint "truck par chauffeur"
  /// donc on récupère le camion via la tournée active
  Future<List<Map<String, dynamic>>> getAllTrucks() async {
    final list = await _api.get('/trucks') as List;
    return list.cast<Map<String, dynamic>>();
  }

  /// GET /api/trips — pour trouver la tournée active du chauffeur
  /// et ainsi connaître son camion assigné
  Future<List<Map<String, dynamic>>> getAllTrips() async {
    final list = await _api.get('/trips') as List;
    return list.cast<Map<String, dynamic>>();
  }
}