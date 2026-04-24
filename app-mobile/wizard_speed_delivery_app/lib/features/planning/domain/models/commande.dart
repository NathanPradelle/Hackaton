import '../../../../shared/enums/enums.dart';

class Commande {
  final int id;
  final int clientId;
  final int? tourneeId;
  final String adresseTexte;
  final double latitude;
  final double longitude;
  final DateTime dateVoulu;
  final String plageHoraire;
  final double prix;
  final int quantite;
  final StatutCommande statut;

  const Commande({
    required this.id,
    required this.clientId,
    this.tourneeId,
    required this.adresseTexte,
    required this.latitude,
    required this.longitude,
    required this.dateVoulu,
    required this.plageHoraire,
    required this.prix,
    required this.quantite,
    required this.statut,
  });

  factory Commande.fromJson(Map<String, dynamic> json) => Commande(
    id: json['id'],
    clientId: json['clientId'],
    tourneeId: json['tourneeId'],
    adresseTexte: json['adresseTexte'],
    latitude: (json['latitude'] as num).toDouble(),
    longitude: (json['longitude'] as num).toDouble(),
    dateVoulu: DateTime.parse(json['dateVoulu']),
    plageHoraire:json['plageHoraire'] ?? '',
    prix: (json['prix'] as num).toDouble(),
    quantite: json['quantite'],
    statut: StatutCommande.values.byName(json['statut']),
  );
}
