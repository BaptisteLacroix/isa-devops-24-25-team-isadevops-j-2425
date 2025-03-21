#!/usr/bin/env bash

echo "📥 Téléchargement du JAR depuis Artifactory..."

# Récupération du dernier fichier jar publié (adapter le chemin Artifactory)
# Ex: snapshot ou release selon ta logique
DATE=$(date +'%y%m%d')
ARTIFACTORY_URL="http://vmpx10.polytech.unice.fr:8011/artifactory/kiwi-card-be-generic-local/kiwi-card-be-generic-local/snapshot/${DATE}-${TIME}"
FILENAME="kiwi-card-be-${DATE}-${TIME}-SNAPSHOT.jar"

curl -uadmin:512Bank! -L -o app.jar "${ARTIFACTORY_URL}/${FILENAME}"

echo "🐳 Construction de l'image Docker avec le jar récupéré"
docker build -t teamj/kiwicard-spring-backend -f Dockerfile .
