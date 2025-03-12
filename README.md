# ISA/Devops KIWICARD

### Authors :

- [Antoine FADDA - RODRIGUEZ](https://github.com/Antoine-FdRg)
- [Antoine MAISTRE - RICE](https://github.com/Antoine-MR)
- [Baptiste LACROIX](https://github.com/BaptisteLacroix)
- [Clément LEFEVRE](https://github.com/Firelods)
- [Roxane BACON](https://github.com/RoxaneBacon)

Cette étude de cas illustre les différentes technologies mises en œuvre dans le projet **KIWICARD**.
Ce projet innovant s’inscrit dans une démarche de dynamisation des commerces de proximité en proposant une solution de
fidélisation basée sur une carte multi-fidélités.
Au lieu de concentrer les avantages sur un seul magasin, cette approche permet aux clients de cumuler des points et de
bénéficier d’avantages dans l’ensemble d’une zone commerciale.
Ainsi, les commerçants locaux, en collaborant avec les collectivités, peuvent rivaliser avec les grands centres
commerciaux en attirant et en fidélisant une clientèle diverse et engagée.

---

## Technologies Requises

Pour faire fonctionner ce code de démonstration, vous devez disposer des logiciels suivants:

- **Configuration d’environnement et build :** Maven ≥3.9.9 (avec wrapper Maven fourni)
- **Langage d’implémentation :** Java ≥21 avec Spring Boot 3.4.1
- **Base de données :** PostgreSQL 17.2 (en image Docker)
- **Containerisation :** Docker Engine (avec Compose) ≥27.x

---

## Vision du Produit

Le système **KIWICARD** est une solution de fidélisation destinée à renforcer le lien entre les consommateurs et les
commerces de leur quartier.
Ce système innovant permet aux clients de débloquer des avantages tels que des réductions, des offres spéciales ou des
services gratuits (ex. : parking gratuit, garderie à tarif réduit) lors de leurs achats chez différents partenaires
d’une même zone géographique.
L’objectif est de créer une synergie entre les commerçants locaux et de dynamiser l’économie de proximité.


---

## Comment Utiliser ce Repository

La documentation de build et d’exécution est divisée en deux parties: une version «tout containerisé» et une version
«build et exécution manuels». La première méthode utilise Docker Compose pour orchestrer l’ensemble des services, tandis
que la seconde décrit en détail chaque étape de compilation et d’exécution.

### Tout Containerisé

Le système se compose de plusieurs sous-systèmes: le backend Spring Boot, une interface en ligne de commande (CLI) et
des services externes, le tout containerisé. Le fichier Docker Compose orchestre le déploiement en incluant, entre
autres, l’image officielle de PostgreSQL qui est configurée à l’aide de variables d’environnement (voir le
chapitre [Persistance](chapters/Persistence.md)).

Pour construire toutes les images requises, lancez le script suivant:

```bash
./build-all.sh 
```

Ce script se rend dans chaque répertoire de sous-système, exécute le build correspondant (compilation, création d’image,
etc.) et crée les images Docker.

Une fois les images construites, déployez l’ensemble du système depuis le répertoire racine avec la commande:

```bash
docker compose up -d
```

Lorsque tous les containers sont démarrés et que leurs vérifications de santé sont validées, vous pouvez vous connecter
au container CLI avec:

(TODO)

```bash
docker attach cli
```
