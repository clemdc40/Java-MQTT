$PROJECT_DIR = Get-Location

docker run -it --rm `
    --name maven-3.8.5-openjdk-17-slim `
    -v ${PROJECT_DIR}:/opt/maven/ `
    -v $PROJECT_DIR\repository:/root/.m2/repository `
    -w /opt/maven `
    maven:3.8.5-openjdk-17-slim `
    mvn clean install
