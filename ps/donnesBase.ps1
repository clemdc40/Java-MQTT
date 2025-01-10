# Obtenir le chemin complet du répertoire de travail
$WORKING_DIR = (Get-Location).Path

# Obtenir le nom du répertoire actuel
$CURRENT_DIR = Split-Path -Leaf $WORKING_DIR

# Nom du conteneur pour le runtime
$CONTAINER_RUNTIME_NAME = "$CURRENT_DIR-openjdk-17-pubsub-tesla"

# Nom du réseau Docker
$NETWORK_NAME = "mqtt-network"

# Vérifier si le réseau existe
$networkExists = docker network ls | Select-String -Pattern $NETWORK_NAME

if (-not $networkExists) {
    Write-Host "Creating network '$NETWORK_NAME'..."
    docker network create $NETWORK_NAME
}

# Exécuter le conteneur Docker
Write-Host "Starting Docker container '$CONTAINER_RUNTIME_NAME'..."
docker run -d -it --rm `
    -v "$WORKING_DIR/app:/app" `
    --net $NETWORK_NAME `
    -e "MYSQL_USERNAME=root" `
    -e "MYSQL_PASSWORD=root" `
    -e "broker_host=Brocker-broker" `
    --name $CONTAINER_RUNTIME_NAME `
    openjdk:17

# Exécuter la commande Java dans le conteneur
try {
    docker exec -it `
        -w /app `
        $CONTAINER_RUNTIME_NAME `
        /bin/bash -c "java -cp /app/app-jar-with-dependencies.jar DonneesBase"
} catch {
    Write-Host "Error encountered, stopping container '$CONTAINER_RUNTIME_NAME'..." -ForegroundColor Red
    docker stop $CONTAINER_RUNTIME_NAME
    exit 1
}

# Arrêter le conteneur après l'exécution
Write-Host "Stopping Docker container '$CONTAINER_RUNTIME_NAME'..."
docker stop $CONTAINER_RUNTIME_NAME