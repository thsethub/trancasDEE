docker-compose down

docker build -t trancas:latest ./

docker-compose up --build --force-recreate --remove-orphans