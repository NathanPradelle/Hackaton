import '../../../../shared/enums/enums.dart';
import 'model_camion.dart';
class Camion {
  final int id;
  final int modeleId;
  final StatutCamion statut;
  final String plaqueImmatriculation;
  final double quantiteEssence;
  final ModeleCamion? modele;

  const Camion({
    required this.id,
    required this.modeleId,
    required this.statut,
    required this.plaqueImmatriculation,
    required this.quantiteEssence,
    this.modele,
  });

  factory Camion.fromJson(Map<String, dynamic> json) => Camion(
    id: json['id'],
    modeleId: json['modeleId'],
    statut: StatutCamion.values.byName(json['statut']),
    plaqueImmatriculation: json['plaqueImmatriculation'],
    quantiteEssence: (json['quantiteEssence'] as num).toDouble(),
    modele: json['modele'] != null
        ? ModeleCamion.fromJson(json['modele'])
        : null,
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'modeleId': modeleId,
    'statut': statut.name,
    'plaqueImmatriculation': plaqueImmatriculation,
    'quantiteEssence': quantiteEssence,
  };
}