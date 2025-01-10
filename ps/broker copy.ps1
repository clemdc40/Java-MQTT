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
    --name "bdd" `
    -p 3306:3306 `
    --net $NETWORK_NAME `
    -e MYSQL_ROOT_PASSWORD=root `
    -v C:\Users\Utilisateur\Documents\clement\Cours\annee3\infrastructure\mysql:/var/lib/mysql `
    mysql:5.7.44-oraclelinux7
