# WizardSpeedDelivery — Contexte projet

## Vision produit

Optimisation logistique de livraison de cartons. L'objectif est de minimiser le coût et le temps de trajet des livreurs via :
- Un algorithme d'optimisation de trajet (estimation coût essence, bouchons, limitations de vitesse, stations-service)
- Prise en compte des caractéristiques techniques du camion (modèle, consommation, type d'essence, capacité réservoir)
- Transmission du tracé GPS optimisé à l'app mobile du chauffeur

## Architecture

| Dossier | Stack | Rôle |
|---------|-------|------|
| `back-end/` | Java Spring Boot + PostgreSQL | API REST centrale |
| `back-office/` | Angular | Panel admin (gestion des camions, commandes, utilisateurs) |
| `front-office/` | Angular | Panel client (pas encore implémenté) |
| `app-mobile/` | Flutter/Dart | App chauffeur (suivi GPS, profil) |

## Source de vérité : BDD.txt

Le fichier `BDD.txt` à la racine définit le modèle de données canonique en français. Toutes les plateformes doivent s'y conformer.

---

## Analyse de cohérence (2026-04-23)

### Problème racine

Le backend Java utilise des noms de champs **anglais camelCase** (`firstName`, `licensePlate`, `fuelType`…) alors que :
- La BDD spec (BDD.txt) est en **français** (`nom`, `prenom`, `plaque d'immatriculation`…)
- L'app mobile Flutter attend du **français camelCase** (`nom`, `prenom`, `plaqueImmatriculation`…)
- Le back-office Angular utilise tantôt l'anglais, tantôt le français de manière incohérente

---

### Incohérences identifiées

#### 1. Nommage des champs — HAUTE SÉVÉRITÉ

| Entité | Backend DTO | Mobile attend | BDD spec |
|--------|------------|---------------|----------|
| Driver | `firstName` | `nom` | `nom` |
| Driver | `lastName` | `prenom` | `prenom` |
| Driver | `licenseNumber` | `numeroPermis` | `numéro de permis` |
| Driver | `status` | `statut` | `statut` |
| Truck | `licensePlate` | `plaqueImmatriculation` | `plaque d'immatriculation` |
| Truck | `currentFuelLevel` | `quantiteEssence` | `quantité d'essence` |
| VehicleModel | `brand` | `marque` | `marque` |
| VehicleModel | `modelName` | `nomModele` | `nom du modèle` |
| VehicleModel | `capacity` | `capacite` | `capacité` |
| VehicleModel | `fuelConsumption` | `consommationEssence` | `consommation d'essence` |
| VehicleModel | `fuelType` | `typeEssence` | `type d'essence` |
| VehicleModel | `tankCapacity` | `capaciteReservoir` | `capacité reservoir` |
| User | `username` | N/A | `identifiant` |

Back-office incohérence interne : `VehicleModel.capacity` → back-office attend `payloadCapacity`.

#### 2. Endpoints manquants — HAUTE SÉVÉRITÉ

Le back-office appelle des endpoints qui n'existent pas côté backend :

| Ressource | Méthode | Endpoint | Back-office appelle | Backend a |
|-----------|---------|----------|---------------------|-----------|
| Trucks | PUT | `/api/trucks/{id}` | `editerCamion()` | NON |
| Trucks | DELETE | `/api/trucks/{id}` | `supprimerCamion()` | NON |
| Orders | PUT | `/api/orders/{id}` | `editerCommande()` | NON |
| Orders | DELETE | `/api/orders/{id}` | `supprimerCommande()` | NON |
| Users | PUT | `/api/users/{id}` | `editerUser()` (component line 83) | NON |

#### 3. Méthode manquante dans UserService Angular — MOYENNE SÉVÉRITÉ

`users.component.ts` (ligne 83) appelle `this.userService.editerUser(user)` mais la méthode n'existe pas dans `user.service.ts`.

#### 4. Enum values — MOYENNE SÉVÉRITÉ

| Enum | Backend | Mobile | Problème |
|------|---------|--------|---------|
| DriverStatus | `AVAILABLE`, `ON_TRIP`, `OFF_DUTY` | `disponible`, `enCours`, `indisponible` | Désérialisation crash |
| TruckStatus | `AVAILABLE`, `IN_MAINTENANCE`, `BUSY` | `disponible`, `maintenance`, `enCours`, `horsService` | Crash + valeur manquante backend |
| FuelType | `DIESEL`, `GASOLINE`, `ELECTRIC`, `GNV` | `diesel`, `essence`, `electrique` | `GNV` manquant mobile |

#### 5. Objet imbriqué manquant — MOYENNE SÉVÉRITÉ

Le mobile attend `camion.modele: ModeleCamion` (objet complet), mais `TruckDto` n'envoie que `modelId: Long`.

#### 6. Itinéraire — MOYENNE SÉVÉRITÉ

- `ItineraryDto` existe en backend mais aucun controller REST
- Le back-office n'a aucun composant itinéraire
- Donnée GPS jamais exposée → bloque l'objectif principal du projet

#### 7. Sécurité

- Backend retourne le mot de passe dans `UserDto` → à supprimer immédiatement

---

### Plan de correction proposé

**Stratégie recommandée :** Utiliser `@JsonProperty` en Java pour sérialiser les champs en français (conformément à BDD.txt) sans renommer les variables Java internes. Adapter les enums en ajoutant des annotations Jackson.

#### Priorité 1 — Backend (correctifs cassants)
1. Ajouter `@JsonProperty("nom")` etc. sur tous les DTOs pour aligner sur BDD.txt
2. Supprimer `password` de `UserDto`
3. Ajouter endpoints PUT/DELETE : trucks, orders, users
4. Ajouter `ItineraryController` avec GET/POST
5. Enrichir `TruckDto` avec l'objet `VehicleModelDto` imbriqué
6. Ajouter `OUT_OF_SERVICE` dans `TruckStatus` (pour `horsService` mobile)
7. Ajouter `GNV` dans l'enum Flutter `TypeEssence`

#### Priorité 2 — Back-office Angular
1. Corriger les interfaces TS pour matcher les nouveaux noms JSON
2. Ajouter `editerUser()` dans `UserService`
3. Mettre à jour `Modele.payloadCapacity` → `capacite`

#### Priorité 3 — App mobile Flutter
1. Mettre à jour les enums pour reconnaître les valeurs backend (ou mapper via `@JsonValue`)
2. Vérifier tous les `.fromJson()` après refactoring backend

---

## Historique des modifications

| Date | Description |
|------|-------------|
| 2026-04-23 | Analyse initiale cohérence backend / back-office / mobile. Rédaction CLAUDE.md. |
| 2026-04-23 | **Correction cohérence complète** — Stratégie `@JsonProperty` pour sérialiser les DTOs en français (BDD.txt comme source de vérité). Détail ci-dessous. |

### Corrections appliquées (2026-04-23)

**Backend — DTOs** (`@JsonProperty` → noms JSON français, variables Java inchangées) :
- `UserDto` : `username` → JSON `identifiant`
- `DriverDto` : `firstName/lastName/licenseNumber/status` → JSON `nom/prenom/numeroPermis/statut`
- `TruckDto` : `modelId/status/licensePlate/currentFuelLevel` → JSON `modeleId/statut/plaqueImmatriculation/quantiteEssence` + nouveau champ `modele` (VehicleModelDto imbriqué)
- `VehicleModelDto` : tous les champs → JSON français (`marque`, `nomModele`, `capacite`, etc.)
- `OrderDto` : `tripId/addressText/requestedDate/timeSlot/price/quantity/status` → JSON `tourneeId/adresseTexte/dateVoulu/plageHoraire/prix/quantite/statut`
- `TripDto` : `driverId/truckId/timeSlot/status` → JSON `chauffeurId/camionId/plageHoraire/statut`
- `ItineraryDto` : `tripId/duration/constraints/gpsData` → JSON `tourneeId/duree/contrainte/infoGps`
- `ClientDto` : `name/siretNumber` → JSON `nom/numeroSiret`

**Backend — Enums** (`@JsonProperty` sur chaque valeur) :
- `DriverStatus` : `AVAILABLE/ON_TRIP/PAUSE/OFF_DUTY` → JSON `disponible/enCours/pause/indisponible` (+ ajout `PAUSE`)
- `TruckStatus` : `AVAILABLE/IN_MAINTENANCE/BUSY/OUT_OF_SERVICE` → JSON `disponible/maintenance/enCours/horsService` (+ ajout `OUT_OF_SERVICE`)
- `FuelType` : `DIESEL/GASOLINE/ELECTRIC/GNV` → JSON `diesel/essence/electrique/gnv`
- `OrderStatus` : `PENDING/ASSIGNED/PICKED_UP/DELIVERED/CANCELLED` → JSON `enAttente/confirmee/enCours/livree/annulee`
- `TripStatus` : `PLANNED/IN_PROGRESS/COMPLETED/CANCELLED` → JSON `planifiee/enCours/terminee/annulee`
- `UserRole` : `ADMIN/DRIVER/CLIENT` → JSON `admin/chauffeur/client`

**Backend — Endpoints manquants créés** :
- `TruckController` : POST, PUT `/{id}`, DELETE `/{id}`
- `OrderController` : PUT `/{id}`, DELETE `/{id}`
- `UserController` : PUT `/users/{id}`
- `ItineraryController` (nouveau) : GET, GET `/trip/{tripId}`, POST, PUT `/{id}`

**Backend — Services** :
- `TruckService` : ajout `updateTruck()`, `deleteTruck()`
- `OrderService` : ajout `updateOrder()`, `deleteOrder()`
- `UserService` : ajout `update()`
- `ItineraryService` (nouveau) : `getAllItineraries()`, `getByTripId()`, `save()`, `update()`

**Backend — TruckMapper** : ajout `uses = VehicleModelMapper.class` + mapping `model → modele` pour inclure l'objet modèle imbriqué

**Backend — ItineraryRepository** : ajout `findByTrip_Id(Long tripId)`

**Back-office Angular** :
- `user.model.ts` : `identifier` → `identifiant`, suppression `password`
- `user.service.ts` : ajout `editerUser()` via PUT `/api/users/{id}`
- `users.component.ts` : suppression interface User dupliquée, import depuis model, correction `u.identifier` → `u.identifiant`, correction `user.id_user` → `user.id`, logique edit/add avec Observable
- `modele.ts` : champs renommés en français + ajout champs carburant (`consommationEssence`, `typeEssence`, `capaciteReservoir`)
- `camions.component.ts` : interface Camion renommée en français (`modeleId`, `statut`, `plaqueImmatriculation`, `quantiteEssence`), références mises à jour
- `commandes.component.ts` : interface Commande renommée en français (`tourneeId`, `adresseTexte`, `dateVoulu`, `plageHoraire`, `prix`, `quantite`, `statut`), références mises à jour

**App mobile Flutter** :
- `enums.dart` : ajout `gnv` dans `TypeEssence`
