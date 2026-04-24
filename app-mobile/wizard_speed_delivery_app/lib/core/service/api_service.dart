import 'dart:convert';
import 'package:http/http.dart' as http;
import '../constants/app_constants.dart';

class ApiService {
  static final ApiService _instance = ApiService._();
  factory ApiService() => _instance;
  ApiService._();

  // On stockera le user connecté ici après login
  Map<String, dynamic>? currentUser;
  int? driverId;

  final _baseUrl = AppConstants.apiBaseUrl;

  Map<String, String> get _headers => {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };

  // GET
  Future<dynamic> get(String endpoint) async {
    final response = await http.get(
      Uri.parse('$_baseUrl$endpoint'),
      headers: _headers,
    );
    return _handleResponse(response);
  }

  // POST
  Future<dynamic> post(String endpoint, Map<String, dynamic> body) async {
    final response = await http.post(
      Uri.parse('$_baseUrl$endpoint'),
      headers: _headers,
      body: jsonEncode(body),
    );
    return _handleResponse(response);
  }

  // PUT
  Future<dynamic> put(String endpoint, Map<String, dynamic> body) async {
    final response = await http.put(
      Uri.parse('$_baseUrl$endpoint'),
      headers: _headers,
      body: jsonEncode(body),
    );
    return _handleResponse(response);
  }

  dynamic _handleResponse(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (response.body.isEmpty) return null;
      return jsonDecode(response.body);
    } else if (response.statusCode == 401) {
      throw ApiException('Identifiant ou mot de passe incorrect');
    } else {
      throw ApiException('Erreur serveur (${response.statusCode})');
    }
  }
}

class ApiException implements Exception {
  final String message;
  ApiException(this.message);

  @override
  String toString() => message;
}