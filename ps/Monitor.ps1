# Obtenir le chemin complet du répertoire de travail
$WORKING_DIR = (Get-Location).Path

# Obtenir le nom du répertoire actuel
$CURRENT_DIR = Split-Path -Leaf $WORKING_DIR

# Nom du conteneur pour le runtime
$CONTAINER_RUNTIME_NAME = "$CURRENT_DIR-openjdk-17-pubsub-monitor"

# Nom du réseau Docker
$NETWORK_NAME = "mqtt-network"

# Vérifier si le réseau existe
$networkExists = docker network ls | Select-String -Pattern $NETWORK_NAME

if (-not $networkExists) {
    Write-Host "Creating network ..."
    docker network create $NETWORK_NAME
}

# Exécuter le conteneur Docker
docker run -d -it --rm `
    -v "$WORKING_DIR/app:/app" `
    --net $NETWORK_NAME `
    -e "broker_host=Brocker-broker" `
    --name $CONTAINER_RUNTIME_NAME `
    openjdk:17

# Exécuter la commande Java dans le conteneur
try {
    docker exec -it `
        -w /app `
        $CONTAINER_RUNTIME_NAME `
        /bin/bash -c "java -cp /app/app-jar-with-dependencies.jar Monitor"
} catch {
    Write-Host "Error encountered, stopping container..."
    docker stop $CONTAINER_RUNTIME_NAME
    exit 1
}

# Arrêter le conteneur après l'exécution
docker stop $CONTAINER_RUNTIME_NAME