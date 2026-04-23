import '../../shared/enums/enums.dart';
import '../../features/auth/domain/models/user.dart';
import '../../features/profile/domain/models/chauffeur.dart';
import '../../features/profile/domain/models/camion.dart';
import '../../features/profile/domain/models/model_camion.dart';
import '../../features/planning/domain/models/commande.dart';
import '../../features/planning/domain/models/tournee.dart';
import '../../features/planning/domain/models/itineraire.dart';

class MockData {
  static final User currentUser = User(
    id: 1, identifiant: 'j.martin', password: 'azerty', role: UserRole.chauffeur,
  );

  static final Chauffeur currentChauffeur = Chauffeur(
    id: 1, userId: 1, nom: 'Martin', prenom: 'Julien',
    numeroPermis: 'C-2019-78452', statut: StatutChauffeur.disponible,
  );

  static final ModeleCamion modeleMAN = ModeleCamion(
    id: 1, marque: 'MAN', nomModele: 'TGX 18.510', capacite: 480,
    consommationEssence: 28.5, typeEssence: TypeEssence.diesel, capaciteReservoir: 400,
  );

  static final Camion currentCamion = Camion(
    id: 1, modeleId: 1, statut: StatutCamion.disponible,
    plaqueImmatriculation: 'AB-123-CD', quantiteEssence: 285, modele: modeleMAN,
  );

  static final List<Commande> commandes = [
    Commande(id: 1, clientId: 1, tourneeId: 1, adresseTexte: '12 Rue de Rivoli, 75001 Paris',
        latitude: 48.8606, longitude: 2.3376, dateVoulu: DateTime.now(),
        plageHoraire: PlageHoraire.matin, prix: 85.50, quantite: 24, statut: StatutCommande.confirmee),
    Commande(id: 2, clientId: 2, tourneeId: 1, adresseTexte: '45 Av. des Champs-Élysées, 75008 Paris',
        latitude: 48.8698, longitude: 2.3075, dateVoulu: DateTime.now(),
        plageHoraire: PlageHoraire.matin, prix: 120.00, quantite: 48, statut: StatutCommande.confirmee),
    Commande(id: 3, clientId: 3, tourneeId: 1, adresseTexte: '8 Place de la Bastille, 75011 Paris',
        latitude: 48.8533, longitude: 2.3692, dateVoulu: DateTime.now(),
        plageHoraire: PlageHoraire.matin, prix: 65.00, quantite: 16, statut: StatutCommande.enAttente),
    Commande(id: 4, clientId: 1, tourneeId: 2, adresseTexte: '1 Place du Trocadéro, 75016 Paris',
        latitude: 48.8624, longitude: 2.2885, dateVoulu: DateTime.now().add(const Duration(days: 1)),
        plageHoraire: PlageHoraire.apresMidi, prix: 95.00, quantite: 32, statut: StatutCommande.enAttente),
    Commande(id: 5, clientId: 4, tourneeId: 2, adresseTexte: '20 Rue de la Paix, 75002 Paris',
        latitude: 48.8688, longitude: 2.3307, dateVoulu: DateTime.now().add(const Duration(days: 1)),
        plageHoraire: PlageHoraire.apresMidi, prix: 145.00, quantite: 56, statut: StatutCommande.enAttente),
  ];

  static List<Tournee> get tournees => [
    Tournee(id: 1, chauffeurId: 1, camionId: 1, date: DateTime.now(),
        plageHoraire: PlageHoraire.matin, statut: StatutTournee.enCours,
        commandes: commandes.where((c) => c.tourneeId == 1).toList(),
        itineraire: const Itineraire(tourneeId: 1, duree: 145, contrainte: 'Hauteur max 4.0m, poids max 19T')),
    Tournee(id: 2, chauffeurId: 1, camionId: 1, date: DateTime.now().add(const Duration(days: 1)),
        plageHoraire: PlageHoraire.apresMidi, statut: StatutTournee.planifiee,
        commandes: commandes.where((c) => c.tourneeId == 2).toList(),
        itineraire: const Itineraire(tourneeId: 2, duree: 95, contrainte: 'Hauteur max 4.0m, poids max 19T')),
    Tournee(id: 3, chauffeurId: 1, camionId: 1, date: DateTime.now().subtract(const Duration(days: 1)),
        plageHoraire: PlageHoraire.matin, statut: StatutTournee.terminee,
        commandes: [
          Commande(id: 10, clientId: 1, tourneeId: 3, adresseTexte: '55 Bd Haussmann, 75009 Paris',
              latitude: 48.8738, longitude: 2.3321, dateVoulu: DateTime.now().subtract(const Duration(days: 1)),
              plageHoraire: PlageHoraire.matin, prix: 110.00, quantite: 40, statut: StatutCommande.livree),
          Commande(id: 11, clientId: 2, tourneeId: 3, adresseTexte: '10 Rue du Fg Saint-Honoré, 75008 Paris',
              latitude: 48.8704, longitude: 2.3178, dateVoulu: DateTime.now().subtract(const Duration(days: 1)),
              plageHoraire: PlageHoraire.matin, prix: 78.00, quantite: 20, statut: StatutCommande.livree),
        ],
        itineraire: const Itineraire(tourneeId: 3, duree: 110, contrainte: 'Hauteur max 4.0m')),
    Tournee(id: 4, chauffeurId: 1, camionId: 1, date: DateTime.now().subtract(const Duration(days: 3)),
        plageHoraire: PlageHoraire.journee, statut: StatutTournee.terminee,
        commandes: [
          Commande(id: 20, clientId: 3, tourneeId: 4, adresseTexte: '2 Place de la Nation, 75012 Paris',
              latitude: 48.8487, longitude: 2.3963, dateVoulu: DateTime.now().subtract(const Duration(days: 3)),
              plageHoraire: PlageHoraire.journee, prix: 200.00, quantite: 80, statut: StatutCommande.livree),
        ],
        itineraire: const Itineraire(tourneeId: 4, duree: 180)),
  ];
}