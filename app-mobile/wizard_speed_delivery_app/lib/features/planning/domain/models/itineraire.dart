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
