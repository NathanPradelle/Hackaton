import '../../../../shared/enums/enums.dart';
import 'commande.dart';
import 'itineraire.dart';

class Tournee {
  final int id;
  final int chauffeurId;
  final int camionId;
  final DateTime date;
  final String plageHoraire;
  final StatutTournee statut;

  // Chargés séparément — pas dans le TripDto du back
  List<Commande>? commandes;
  Itineraire? itineraire;

  Tournee({
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
    plageHoraire:json['plageHoraire'] ?? '',
    statut: StatutTournee.values.byName(json['statut']),
  );
}