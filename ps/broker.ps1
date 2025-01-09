$CURRENT_DIR = "Brocker"
$NETWORK_NAME = "mqtt-network"

# Vérifier si le réseau existe
$networkExists = docker network ls | Select-String -Pattern $NETWORK_NAME

if (-not $networkExists) {
    Write-Host "Creating network ..."
    docker network create $NETWORK_NAME
}

# Exécuter le conteneur Docker
docker run -d --rm -it `
    --name "$($CURRENT_DIR)-broker" `
    -p 15672:15672 -p 5672:5672 `
    --net $NETWORK_NAME `
    rabbitmq:3.12.4-management-alpine
