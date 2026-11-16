# Evaluation Service

Microservice de gestion des évaluations scolaires du projet **Microservice École**.

## 1. Présentation

`evaluation-service` permet de gérer les évaluations organisées par les établissements scolaires : devoirs, compositions et examens.

Il assure la création, la consultation, la modification, la suppression, la publication et la clôture des évaluations.

## 2. Technologies utilisées

- Java 21
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Jakarta Validation
- Lombok
- JUnit et Mockito

## 3. Architecture du projet

```text
evaluation-service/
├── src/
│   ├── main/
│   │   ├── java/com/quamtech/evaluation/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── enums/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── EvaluationServiceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/quamtech/evaluation/
├── .gitignore
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## 4. Fonctionnalités

- Création d'une évaluation.
- Consultation de toutes les évaluations d'un établissement.
- Consultation d'une évaluation par son identifiant.
- Modification d'une évaluation en brouillon.
- Suppression d'une évaluation en brouillon.
- Publication d'une évaluation.
- Clôture d'une évaluation publiée.
- Isolation des données par établissement avec `school_id`.

## 5. Cycle de vie d'une évaluation

Une évaluation suit le cycle suivant :

`DRAFT → PUBLISHED → CLOSED`

Règles métier :

- Une évaluation nouvellement créée possède le statut `DRAFT`.
- Seules les évaluations en brouillon peuvent être modifiées ou supprimées.
- Seules les évaluations en brouillon peuvent être publiées.
- Seules les évaluations publiées peuvent être clôturées.
- Une évaluation clôturée ne peut plus être modifiée ou supprimée.

## 6. Types d'évaluation

Les types autorisés sont :

- `DEVOIR`
- `COMPOSITION`
- `EXAMEN`

Les statuts autorisés sont :

- `DRAFT`
- `PUBLISHED`
- `CLOSED`

## 7. Configuration

### Prérequis

- Java 21
- PostgreSQL
- Maven (ou Maven Wrapper)

### Base de données

Créer une base PostgreSQL nommée :

`evaluation_db`

Configurer les variables d'environnement suivantes :

| Variable | Description |
|---|---|
| `DB_URL` | URL JDBC PostgreSQL (facultative, valeur par défaut dans la configuration) |
| `DB_USERNAME` | Utilisateur PostgreSQL |
| `DB_PASSWORD` | Mot de passe PostgreSQL |

Valeurs par défaut de l'application :

- URL : `jdbc:postgresql://127.0.0.1:5432/evaluation_db`
- Utilisateur : `evaluation_user`

Exemple sous Linux :

```bash
export DB_URL="jdbc:postgresql://127.0.0.1:5432/evaluation_db"
export DB_USERNAME="evaluation_user"
export DB_PASSWORD="votre_mot_de_passe"
```

Ne jamais publier les véritables identifiants de la base de données.

### Démarrage

À la racine du projet :

```bash
./mvnw spring-boot:run
```

Le service écoute sur le port `8081`.

## 8. API REST

Préfixe :

`/api/v1/evaluations`

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/evaluations` | Créer une évaluation |
| GET | `/api/v1/evaluations` | Lister les évaluations de l'établissement |
| GET | `/api/v1/evaluations/{evaluationId}` | Consulter une évaluation |
| PUT | `/api/v1/evaluations/{evaluationId}` | Modifier une évaluation |
| DELETE | `/api/v1/evaluations/{evaluationId}` | Supprimer une évaluation |
| PATCH | `/api/v1/evaluations/{evaluationId}/publish` | Publier une évaluation |
| PATCH | `/api/v1/evaluations/{evaluationId}/close` | Clôturer une évaluation |

### En-têtes HTTP

Le service utilise le contexte transmis par la passerelle API :

- `X-School-Id` : identifiant UUID de l'établissement.
- `X-User-Id` : identifiant UUID de l'utilisateur créateur.

`X-School-Id` est requis pour toutes les opérations. `X-User-Id` est requis pour la création.

Ces en-têtes doivent être injectés par une passerelle de confiance après authentification. Le microservice ne valide pas lui-même les JWT ni les rôles.

## 9. Validation et gestion des erreurs

Les données reçues sont validées avec Jakarta Validation.

Principales réponses HTTP :

- `201 Created` : évaluation créée.
- `200 OK` : opération réussie.
- `204 No Content` : évaluation supprimée.
- `400 Bad Request` : données invalides ou en-tête obligatoire absent.
- `404 Not Found` : évaluation introuvable dans l'établissement.
- `409 Conflict` : opération incompatible avec le statut de l'évaluation.

## 10. Tests

Exécuter l'ensemble des vérifications :

```bash
./mvnw clean verify
```

Les tests couvrent les opérations métier, les règles de transition de statut, l'isolation par établissement et les endpoints REST.

## 11. Architecture microservices

Ce service possède ses propres données et identifiants.

Les identifiants d'année scolaire, de période, de classe, de matière et d'enseignant sont conservés comme références UUID. Aucune clé étrangère vers les bases des autres microservices n'est créée.

## 12. État du développement

Le développement fonctionnel du microservice est réalisé et la compilation ainsi que les tests automatisés ont été validés.

Les mécanismes d'authentification, d'autorisation et de communication événementielle restent à intégrer selon les contrats et responsabilités définis pour l'architecture globale.

---

**Projet :** Microservice École
**Service :** evaluation-service
**Port :** 8081
**Package principal :** `com.quamtech.evaluation`
