import '../../../../shared/enums/enums.dart';

class ModeleCamion {
  final int id;
  final String marque;
  final String nomModele;
  final int capacite;
  final double consommationEssence;
  final TypeEssence typeEssence;
  final double capaciteReservoir;

  const ModeleCamion({
    required this.id,
    required this.marque,
    required this.nomModele,
    required this.capacite,
    required this.consommationEssence,
    required this.typeEssence,
    required this.capaciteReservoir,
  });

  factory ModeleCamion.fromJson(Map<String, dynamic> json) => ModeleCamion(
    id: json['id'],
    marque: json['marque'],
    nomModele: json['nomModele'],
    capacite: json['capacite'],
    consommationEssence: (json['consommationEssence'] as num).toDouble(),
    typeEssence: TypeEssence.values.byName(json['typeEssence']),
    capaciteReservoir: (json['capaciteReservoir'] as num).toDouble(),
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'marque': marque,
    'nomModele': nomModele,
    'capacite': capacite,
    'consommationEssence': consommationEssence,
    'typeEssence': typeEssence.name,
    'capaciteReservoir': capaciteReservoir,
  };
}