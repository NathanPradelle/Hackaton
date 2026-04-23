import '../../../../shared/enums/enums.dart';

class Chauffeur {
  final int id;
  final int userId;
  final String nom;
  final String prenom;
  final String numeroPermis;
  final StatutChauffeur statut;

  const Chauffeur({
    required this.id,
    required this.userId,
    required this.nom,
    required this.prenom,
    required this.numeroPermis,
    required this.statut,
  });

  String get nomComplet => '$prenom $nom';

  factory Chauffeur.fromJson(Map<String, dynamic> json) => Chauffeur(
    id: json['id'],
    userId: json['userId'],
    nom: json['nom'],
    prenom: json['prenom'],
    numeroPermis: json['numeroPermis'],
    statut: StatutChauffeur.values.byName(json['statut']),
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'userId': userId,
    'nom': nom,
    'prenom': prenom,
    'numeroPermis': numeroPermis,
    'statut': statut.name,
  };
}