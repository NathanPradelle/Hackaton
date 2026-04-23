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

---

## Algorithme d'optimisation d'itinéraire

*Brainstorming & plan de réalisation — 2026-04-23*

---

### 1. Nature du problème

Ce qu'on résout est un **VRPTW** (Vehicle Routing Problem with Time Windows) enrichi de contraintes carburant et de tarification dynamique. On n'essaie pas de le résoudre exactement (NP-difficile) : on délègue le cœur du routage à un solveur éprouvé et on code la valeur métier autour.

---

### 2. Dépôt

- **Adresse fixe, unique, hardcodée** dans la configuration Spring (pas en BDD — ça va plus vite).
- Tous les camions **partent** du dépôt et **y reviennent** après chaque tournée.
- Le retour au dépôt est géré nativement par VROOM (paramètre `vehicle.end = depot`).
- Un chauffeur n'est **disponible pour une nouvelle tournée** qu'une fois revenu au dépôt (ETA de retour calculé dans l'itinéraire).

```properties
# application.properties
depot.latitude=48.8566
depot.longitude=2.3522
depot.address=Paris, France
```

---

### 3. Double cycle : simulation → officialisation

C'est le point le plus structurant de l'architecture.

```
Client crée une commande
        │
        ▼
 [SIMULATION LÉGÈRE]
 Chercher commandes compatibles à grouper
 Simuler l'itinéraire (ORS)
 Calculer coût total → proratiser
        │
        ▼
 Prix estimé affiché au client
 Client confirme → commande enAttente (prix verrouillé)
        │
        (plus tard)
        ▼
 [OFFICIALISATION] déclenchée par l'admin
 Regrouper toutes les commandes enAttente du jour
 Générer itinéraires définitifs (VROOM + fuel)
 Créer Trip + Itinerary en BDD
 Mettre à jour statut commandes → confirmee
```

**Décision clé — verrouillage du prix :**
Le prix est **verrouillé au moment de la confirmation client** (simulation). Si l'officialisation regroupe différemment (ex. nouvelle commande arrivée entre-temps), le prix n'est **pas recalculé** pour le client. C'est plus simple et prévisible. L'écart de coût réel vs prix vendu est le risque/marge de l'entreprise.

---

### 4. Simulation à la création de commande

**Déclencheur :** `POST /api/orders` (création d'une commande par le client ou l'admin)

**Étapes :**

1. **Geocoder** l'adresse de la nouvelle commande (ORS)
2. **Chercher des commandes compatibles** à grouper (en BDD) :
   - Même date de livraison
   - Plage horaire compatible (chevauchement ou écart ≤ 1h)
   - Distance entre adresses ≤ seuil (ex. 30 km à vol d'oiseau, calcul rapide sans API)
   - Quantité totale ≤ capacité d'un camion
3. **Simuler la tournée** (appel ORS routing, pas VROOM complet) :
   - Si commandes compatibles trouvées → simuler le trajet groupé dépôt → stops → dépôt
   - Sinon → simuler solo dépôt → adresse → dépôt
4. **Calculer le coût total de la tournée simulée :**
   ```
   coût_carburant = (distance_totale / 100) × conso_L_per_100 × prix_moyen_essence
   coût_total = coût_carburant + marge_fixe_chauffeur (forfait ou au km)
   ```
5. **Proratiser le coût entre les commandes groupées :**
   ```
   part_commande = coût_total × (quantite_commande / quantite_totale_tournee)
   ```
   Ou alternative : part égale + supplément distance (selon choix business)
6. Retourner le **prix estimé** dans la réponse à la création de commande
7. Persister la commande avec `prix = prix_estimé`, `statut = enAttente`, coordonnées GPS stockées

> **Optimisation :** pour la simulation, on n'a pas besoin de VROOM ni de l'algo carburant complet. Un simple appel ORS `/v2/directions/driving-hgv` avec les stops dans un ordre naïf suffit pour l'estimation de prix. La précision n'est pas critique ici (c'est une simulation).

---

### 5. Officialisation — génération des tournées définitives

**Déclencheur :** `POST /api/itineraries/generate?date=2026-05-01` (action admin)

#### Phase 1 — Groupage et routage optimal (VROOM)

Construire la requête ORS `/optimization` avec :

```json
{
  "vehicles": [
    {
      "id": truckId,
      "profile": "driving-hgv",
      "start": [depot_lng, depot_lat],
      "end":   [depot_lng, depot_lat],
      "capacity": [truck.capacity],
      "time_window": [shift_start_unix, shift_end_unix]
    }
  ],
  "jobs": [
    {
      "id": orderId,
      "location": [order.longitude, order.latitude],
      "amount": [order.quantite],
      "time_windows": [[slot_start_unix, slot_end_unix]]
    }
  ],
  "options": { "g": true }
}
```

VROOM retourne pour chaque camion : liste ordonnée de livraisons + ETAs.

Le retour au dépôt est **automatiquement intégré** grâce à `vehicle.end = depot`.

#### Phase 2 — Planification carburant

Pour chaque tournée retournée :

1. Initialiser `niveauEssence = truck.currentFuelLevel`
2. Parcourir les segments dans l'ordre :
   - `consommé = (distance_segment_km / 100) × conso_L_per_100`
   - `niveauEssence -= consommé`
   - Si `niveauEssence < capaciteReservoir × 0.15` → déclencher recherche station
3. **Recherche de station :**
   - API prix-carburant : stations dans un rayon de 5 km autour du point courant, filtrées par type carburant
   - Calculer le détour pour chaque station : ORS routing depuis point courant → station → prochain waypoint vs direct
   - Choisir la station minimisant `distance_détour_km × conso × prix_station`
   - Insérer le waypoint station dans l'itinéraire, remettre `niveauEssence = capaciteReservoir`
4. Répéter jusqu'à la fin (retour dépôt inclus)

#### Phase 3 — Persistance

```
Pour chaque tournée :
  → Créer Trip (chauffeur, camion, statut=planifiee)
  → Créer Itinerary (gpsData JSON, durée, distance, coût carburant réel)
  → Mettre à jour Orders liées → statut=confirmee
  → Mettre à jour Truck.currentFuelLevel (niveau estimé au retour)
```

**Structure `gpsData` (JSON dans Itinerary.gpsData) :**

```json
{
  "waypoints": [
    { "lat": 48.856, "lng": 2.352, "type": "depot",     "eta": "2026-05-01T08:00:00" },
    { "lat": 48.923, "lng": 2.411, "type": "livraison", "orderId": 12, "eta": "2026-05-01T09:15:00" },
    { "lat": 48.881, "lng": 2.383, "type": "carburant", "station": "Total Bercy", "eta": "2026-05-01T10:00:00" },
    { "lat": 48.753, "lng": 2.301, "type": "livraison", "orderId": 15, "eta": "2026-05-01T11:00:00" },
    { "lat": 48.856, "lng": 2.352, "type": "depot",     "eta": "2026-05-01T12:30:00" }
  ],
  "distanceTotale": 87.4,
  "dureeEstimee": 270,
  "coutCarburantEstime": 32.50,
  "nbLivraisons": 2
}
```

---

### 6. APIs externes

| Besoin | API | Gratuit ? | Clé |
|--------|-----|-----------|-----|
| Geocoding (adresse → GPS) | ORS `/geocode/search` | Oui (2000/j) | Oui |
| Routage simulation (solo ou naïf groupé) | ORS `/v2/directions/driving-hgv` | Oui | Oui |
| Solveur VRPTW (groupage + ordre optimal) | ORS `/optimization` (VROOM) | Oui | Oui |
| Prix carburant temps réel + coordonnées stations | data.economie.gouv.fr prix-carburants | Oui | Non |

Clé ORS : s'inscrire sur openrouteservice.org, mettre la clé en variable d'environnement (`ORS_API_KEY`), ne jamais committer.

---

### 7. Composants backend Java à créer

| Classe | Rôle |
|--------|------|
| `PriceSimulationService` | Simulation légère à la création commande (geocoding + routing naïf + calcul prix) |
| `OrderGroupingService` | Trouver commandes compatibles à grouper (requête BDD + distance haversine) |
| `ItineraryGenerationService` | Orchestrateur officialisation (phases 1→3) |
| `GeocodingService` | ORS geocoding — résultat mis en cache dans les champs lat/lng de Order |
| `VroomService` | Appel ORS `/optimization`, parsing réponse VROOM |
| `RoutingService` | Appel ORS `/v2/directions/driving-hgv` (segment par segment) |
| `FuelPlanningService` | Détection seuil carburant + choix station optimale |
| `FuelStationApiService` | Appel API prix-carburant.gouv.fr, filtre par type + rayon |

Nouveaux endpoints :
- `POST /api/orders` → déclenche `PriceSimulationService`, retourne `prix_estime` dans la réponse
- `POST /api/itineraries/generate?date=` → déclenche `ItineraryGenerationService`

---

### 8. Questions ouvertes avant de coder

| # | Question | Recommandation |
|---|----------|----------------|
| 1 | Seuil de groupage distance | 30 km à vol d'oiseau (haversine, sans API) pour la simulation |
| 2 | Seuil de groupage temps | Plages horaires doivent se chevaucher ou être à ≤ 1h d'écart |
| 3 | Proratisation du prix | Par quantité (cartons) — le plus simple et le plus juste |
| 4 | Prix verrouillé ? | Oui — verrouillé à la confirmation client, pas recalculé à l'officialisation |
| 5 | Camions électriques | Exclure de l'algo pour l'instant (pas de logique borne recharge) |
| 6 | Horaires chauffeur | Intégrer début/fin de shift dans `vehicle.time_window` VROOM (champ à ajouter sur Driver) |
| 7 | Recalcul temps réel | Hors scope pour l'instant — itinéraire statique une fois officiel |
| 8 | Marge/forfait chauffeur | Décider si le prix intègre un coût fixe par livraison ou seulement le carburant |
