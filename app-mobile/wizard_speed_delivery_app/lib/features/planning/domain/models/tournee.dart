import '../../../../shared/enums/enums.dart';
import 'commande.dart';
import 'itineraire.dart';

class Tournee {
  final int id;
  final int chauffeurId;
  final int camionId;
  final DateTime date;
  final PlageHoraire plageHoraire;
  final StatutTournee statut;
  final List<Commande>? commandes;
  final Itineraire? itineraire;

  const Tournee({
    required this.id,
    required this.chauffeurId,
    required this.camionId,
    required this.date,
    required this.plageHoraire,
    required this.statut,
    this.commandes,
    this.itineraire,
  });

  int get nbStops => commandes?.length ?? 0;

  factory Tournee.fromJson(Map<String, dynamic> json) => Tournee(
    id: json['id'],
    chauffeurId: json['chauffeurId'],
    camionId: json['camionId'],
    date: DateTime.parse(json['date']),
    plageHoraire: PlageHoraire.values.byName(json['plageHoraire']),
    statut: StatutTournee.values.byName(json['statut']),
    commandes: json['commandes'] != null
        ? (json['commandes'] as List).map((c) => Commande.fromJson(c)).toList()
        : null,
    itineraire: json['itineraire'] != null
        ? Itineraire.fromJson(json['itineraire'])
        : null,
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'chauffeurId': chauffeurId,
    'camionId': camionId,
    'date': date.toIso8601String(),
    'plageHoraire': plageHoraire.name,
    'statut': statut.name,
  };
}
