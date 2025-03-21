#!/usr/bin/env bash

echo "📥 Téléchargement du JAR depuis Artifactory..."

# Récupération du dernier fichier jar publié (adapter le chemin Artifactory)
# Ex: snapshot ou release selon ta logique
DATE=$(date +'%y%m%d')
ARTIFACTORY_PATH="kiwi-card-be-generic-local/snapshot/${DATE}"  # ou release/${DATE}
FILENAME="kiwi-card-be-${DATE}-SNAPSHOT.jar" # ou -RELEASE.jar

jfrog rt dl "$ARTIFACTORY_PATH/$FILENAME" app.jar

echo "🐳 Construction de l'image Docker avec le jar récupéré"
docker build -t teamj/kiwicard-spring-backend -f Dockerfile .
