import 'dart:convert';

class Waypoint {
  final double lat;
  final double lng;
  final String type; // depot | livraison | carburant
  final int? orderId;
  final String? eta;

  const Waypoint({
    required this.lat,
    required this.lng,
    required this.type,
    this.orderId,
    this.eta,
  });

  factory Waypoint.fromJson(Map<String, dynamic> json) => Waypoint(
    lat: (json['lat'] as num).toDouble(),
    lng: (json['lng'] as num).toDouble(),
    type: json['type'] as String,
    orderId: json['orderId'] as int?,
    eta: json['eta'] as String?,
  );
}

class Itineraire {
  final int tourneeId;
  final double duree;
  final String? contrainte;
  final String? infoGps;

  const Itineraire({
    required this.tourneeId,
    required this.duree,
    this.contrainte,
    this.infoGps,
  });

  List<Waypoint>? get parsedWaypoints {
    if (infoGps == null) return null;
    try {
      final decoded = jsonDecode(infoGps!) as Map<String, dynamic>;
      final list = decoded['waypoints'] as List;
      return list.map((w) => Waypoint.fromJson(w as Map<String, dynamic>)).toList();
    } catch (_) {
      return null;
    }
  }

  String get dureeFormatee {
    final totalMin = duree.round();
    final h = totalMin ~/ 60;
    final m = totalMin % 60;
    if (h == 0) return '${m}min';
    return '${h}h${m.toString().padLeft(2, '0')}';
  }

  factory Itineraire.fromJson(Map<String, dynamic> json) => Itineraire(
    tourneeId: json['tourneeId'],
    duree: (json['duree'] as num).toDouble(),
    contrainte: json['contrainte'],
    infoGps: json['infoGps'],
  );


}
