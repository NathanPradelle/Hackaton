class Itineraire {
  final int tourneeId;
  final int duree;
  final String? contrainte;
  final String? infoGps;

  const Itineraire({
    required this.tourneeId,
    required this.duree,
    this.contrainte,
    this.infoGps,
  });

  String get dureeFormatee {
    final h = duree ~/ 60;
    final m = duree % 60;
    if (h == 0) return '${m}min';
    return '${h}h${m.toString().padLeft(2, '0')}';
  }

  factory Itineraire.fromJson(Map<String, dynamic> json) => Itineraire(
    tourneeId: json['tourneeId'],
    duree: json['duree'],
    contrainte: json['contrainte'],
    infoGps: json['infoGps'],
  );

  Map<String, dynamic> toJson() => {
    'tourneeId': tourneeId,
    'duree': duree,
    'contrainte': contrainte,
    'infoGps': infoGps,
  };
}