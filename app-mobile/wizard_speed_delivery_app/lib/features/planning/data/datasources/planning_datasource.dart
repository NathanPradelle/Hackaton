import '../../../../core/service/api_service.dart';

class PlanningDatasource {
  final _api = ApiService();

  /// GET /api/trips
  Future<List<Map<String, dynamic>>> getAllTrips() async {
    final list = await _api.get('/trips') as List;
    return list.cast<Map<String, dynamic>>();
  }

  /// GET /api/orders
  Future<List<Map<String, dynamic>>> getAllOrders() async {
    final list = await _api.get('/orders') as List;
    return list.cast<Map<String, dynamic>>();
  }

  /// GET /api/itineraries/trip/{tripId}
  Future<Map<String, dynamic>> getItineraryByTripId(int tripId) async {
    final result = await _api.get('/itineraries/trip/$tripId');
    return result as Map<String, dynamic>;
  }
}