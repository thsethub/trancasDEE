# Guia de Deploy e Configuração - trancasDEE

Este guia abrange todas as formas de deploy e configuração do sistema trancasDEE.

## 📋 Índice

- [Configuração do Ambiente](#configuração-do-ambiente)
- [Deploy Local](#deploy-local)
- [Deploy com Docker](#deploy-com-docker)
- [Deploy em Produção](#deploy-em-produção)
- [Configuração de Banco de Dados](#configuração-de-banco-de-dados)
- [Monitoramento](#monitoramento)
- [Troubleshooting](#troubleshooting)

---

## 🔧 Configuração do Ambiente

### Pré-requisitos

**Para desenvolvimento local:**
- Java 17 ou superior
- Maven 3.6+
- MariaDB 10.3+
- Git

**Para deploy com Docker:**
- Docker 20.10+
- Docker Compose 2.0+

**Para deploy em produção:**
- Servidor Linux (Ubuntu 20.04+ ou CentOS 8+)
- Nginx (opcional, para proxy reverso)
- Certificado SSL (opcional, para HTTPS)

---

## 🏠 Deploy Local

### 1. Preparação do Ambiente

```bash
# Instalar Java 17 (Ubuntu)
sudo apt update
sudo apt install openjdk-17-jdk

# Verificar instalação
java -version
```

### 2. Configuração do Banco de Dados

```bash
# Instalar MariaDB
sudo apt install mariadb-server mariadb-client

# Configurar MariaDB
sudo mysql_secure_installation

# Acessar MariaDB
sudo mysql -u root -p
```

```sql
-- Criar banco e usuário
CREATE DATABASE sys CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'trancasdee'@'localhost' IDENTIFIED BY 'senha_segura';
GRANT ALL PRIVILEGES ON sys.* TO 'trancasdee'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configuração da Aplicação

```bash
# Clonar repositório
git clone https://github.com/thsethub/trancasDEE.git
cd trancasDEE

# Configurar application.properties
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Editar `src/main/resources/application.properties`:

```properties
spring.application.name=trancasDEE
server.port=3000

# Configuração do banco local
spring.datasource.url=jdbc:mariadb://localhost:3306/sys
spring.datasource.username=trancasdee
spring.datasource.password=senha_segura
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# Configurações JPA
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

# Logs em produção
logging.level.org.hibernate.SQL=WARN
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN
logging.level.br.dee.trancasdee=INFO

spring.jackson.property-naming-strategy=SNAKE_CASE
```

### 4. Build e Execução

```bash
# Build da aplicação
./mvnw clean package -DskipTests

# Executar aplicação
java -jar target/trancasDEE-0.0.1-SNAPSHOT.jar

# Ou executar em modo de desenvolvimento
./mvnw spring-boot:run
```

### 5. Verificação

```bash
# Testar API
curl http://localhost:3000/usuarios
curl http://localhost:3000/ambientes
```

---

## 🐳 Deploy com Docker

### 1. Build da Imagem

```bash
# Build usando Dockerfile incluído
docker build -t trancasdee:latest .

# Ou build multi-stage para produção
docker build -f Dockerfile.prod -t trancasdee:prod .
```

### 2. Configuração com Docker Compose

Criar `docker-compose.yml`:

```yaml
version: '3.9'

services:
  mariadb:
    image: mariadb:10.11
    container_name: trancasdee-db
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: sys
      MYSQL_USER: trancasdee
      MYSQL_PASSWORD: app_password
    volumes:
      - db_data:/var/lib/mysql
      - ./docs/init.sql:/docker-entrypoint-initdb.d/01-init.sql
    ports:
      - "3306:3306"
    networks:
      - trancas-network
    restart: unless-stopped

  spring-app:
    build: .
    container_name: trancasdee-app
    ports:
      - "3000:3000"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://mariadb:3306/sys
      SPRING_DATASOURCE_USERNAME: trancasdee
      SPRING_DATASOURCE_PASSWORD: app_password
      SPRING_PROFILES_ACTIVE: docker
    depends_on:
      - mariadb
    networks:
      - trancas-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3000/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  db_data:

networks:
  trancas-network:
    driver: bridge
```

### 3. Execução

```bash
# Subir todos os serviços
docker-compose up -d

# Verificar logs
docker-compose logs -f

# Verificar status
docker-compose ps
```

### 4. Scripts de Inicialização

Criar `docs/init.sql`:

```sql
-- Script executado na inicialização do MariaDB
USE sys;

-- Criar as tabelas (ver DATABASE.md para script completo)
-- Inserir dados iniciais se necessário
```

---

## 🚀 Deploy em Produção

### 1. Configuração do Servidor

```bash
# Preparar servidor Ubuntu 20.04+
sudo apt update && sudo apt upgrade -y

# Instalar dependências
sudo apt install -y docker.io docker-compose nginx certbot python3-certbot-nginx

# Configurar Docker para usuário
sudo usermod -aG docker $USER
newgrp docker

# Habilitar serviços
sudo systemctl enable docker
sudo systemctl start docker
```

### 2. Configuração de Produção

Criar `docker-compose.prod.yml`:

```yaml
version: '3.9'

services:
  mariadb:
    image: mariadb:10.11
    container_name: trancasdee-db-prod
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MYSQL_DATABASE: sys
      MYSQL_USER: ${DB_USER}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    volumes:
      - /var/lib/trancasdee/data:/var/lib/mysql
      - /var/log/trancasdee/mysql:/var/log/mysql
    networks:
      - trancas-network
    restart: unless-stopped
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  spring-app:
    build:
      context: .
      dockerfile: Dockerfile.prod
    image: trancasdee:prod
    container_name: trancasdee-app-prod
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://mariadb:3306/sys
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_PROFILES_ACTIVE: production
      SERVER_PORT: 3000
    depends_on:
      - mariadb
    networks:
      - trancas-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3000/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 40s
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "5"

networks:
  trancas-network:
    driver: bridge
```

### 3. Variáveis de Ambiente

Criar `.env`:

```bash
# Configurações de banco
DB_ROOT_PASSWORD=senha_root_muito_segura
DB_USER=trancasdee_prod
DB_PASSWORD=senha_app_muito_segura

# Configurações da aplicação
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=3000
```

### 4. Dockerfile para Produção

Criar `Dockerfile.prod`:

```dockerfile
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Criar usuário não-root
RUN groupadd -r trancasdee && useradd -r -g trancasdee trancasdee

# Copiar apenas o JAR da aplicação
COPY --from=builder /app/target/trancasDEE-*.jar app.jar
COPY --chown=trancasdee:trancasdee --from=builder /app/target/trancasDEE-*.jar app.jar

# Configurar usuário
USER trancasdee

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:3000/actuator/health || exit 1

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "-Xmx512m", "-Xms256m", "app.jar"]
```

### 5. Configuração do Nginx

Criar `/etc/nginx/sites-available/trancasdee`:

```nginx
server {
    listen 80;
    server_name trancasdee.exemplo.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name trancasdee.exemplo.com;

    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/trancasdee.exemplo.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/trancasdee.exemplo.com/privkey.pem;
    
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    # Security headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=63072000; includeSubDomains; preload";

    # Logs
    access_log /var/log/nginx/trancasdee.access.log;
    error_log /var/log/nginx/trancasdee.error.log;

    # Proxy to Spring Boot app
    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Health check endpoint
    location /health {
        proxy_pass http://localhost:3000/actuator/health;
        access_log off;
    }
}
```

Habilitar site:

```bash
sudo ln -s /etc/nginx/sites-available/trancasdee /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 6. SSL com Let's Encrypt

```bash
# Obter certificado SSL
sudo certbot --nginx -d trancasdee.exemplo.com

# Configurar renovação automática
sudo crontab -e
# Adicionar linha:
# 0 12 * * * /usr/bin/certbot renew --quiet
```

### 7. Deploy Final

```bash
# Criar diretórios
sudo mkdir -p /var/lib/trancasdee/{data,logs}
sudo mkdir -p /var/log/trancasdee/mysql

# Deploy da aplicação
docker-compose -f docker-compose.prod.yml up -d

# Verificar logs
docker-compose -f docker-compose.prod.yml logs -f

# Testar endpoints
curl https://trancasdee.exemplo.com/usuarios
```

---

## 🔍 Monitoramento

### 1. Configuração de Logs

Criar `/etc/logrotate.d/trancasdee`:

```
/var/log/trancasdee/*.log {
    daily
    missingok
    rotate 52
    compress
    delaycompress
    notifempty
    create 644 root root
    postrotate
        docker-compose -f /opt/trancasdee/docker-compose.prod.yml restart spring-app
    endscript
}
```

### 2. Health Checks

Script de monitoramento (`/opt/scripts/monitor-trancasdee.sh`):

```bash
#!/bin/bash

ENDPOINT="http://localhost:3000/actuator/health"
LOG_FILE="/var/log/trancasdee/monitor.log"

# Verificar saúde da aplicação
if curl -f -s $ENDPOINT > /dev/null; then
    echo "$(date): OK - Aplicação respondendo" >> $LOG_FILE
else
    echo "$(date): ERROR - Aplicação não responde" >> $LOG_FILE
    # Reiniciar se necessário
    docker-compose -f /opt/trancasdee/docker-compose.prod.yml restart spring-app
fi
```

Adicionar ao crontab:

```bash
# Verificar a cada 5 minutos
*/5 * * * * /opt/scripts/monitor-trancasdee.sh
```

### 3. Métricas com Prometheus (opcional)

Adicionar ao `docker-compose.prod.yml`:

```yaml
  prometheus:
    image: prom/prometheus:latest
    container_name: trancasdee-prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    networks:
      - trancas-network

  grafana:
    image: grafana/grafana:latest
    container_name: trancasdee-grafana
    ports:
      - "3001:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    networks:
      - trancas-network
```

---

## 🔧 Troubleshooting

### Problemas Comuns

#### 1. Erro de Conexão com Banco

```bash
# Verificar se MariaDB está rodando
docker-compose ps mariadb

# Verificar logs do banco
docker-compose logs mariadb

# Testar conexão
docker exec -it trancasdee-db-prod mysql -u trancasdee -p sys
```

#### 2. Aplicação Não Inicia

```bash
# Verificar logs da aplicação
docker-compose logs spring-app

# Verificar uso de memória
docker stats

# Verificar configurações
docker exec trancasdee-app-prod env | grep SPRING
```

#### 3. Performance Issues

```bash
# Verificar uso de recursos
docker stats
htop

# Logs de performance
docker-compose logs spring-app | grep -E "(slow|timeout|error)"

# Verificar queries lentas no MariaDB
docker exec trancasdee-db-prod mysql -u root -p -e "SHOW PROCESSLIST;"
```

### Scripts de Diagnóstico

Criar `/opt/scripts/diagnose-trancasdee.sh`:

```bash
#!/bin/bash

echo "=== Diagnóstico trancasDEE ==="
echo "Data: $(date)"
echo

echo "=== Status dos Containers ==="
docker-compose -f /opt/trancasdee/docker-compose.prod.yml ps
echo

echo "=== Uso de Recursos ==="
docker stats --no-stream
echo

echo "=== Health Check ==="
curl -s http://localhost:3000/actuator/health | jq .
echo

echo "=== Logs Recentes ==="
docker-compose -f /opt/trancasdee/docker-compose.prod.yml logs --tail=20 spring-app
echo

echo "=== Conexões de Banco ==="
docker exec trancasdee-db-prod mysql -u root -p${DB_ROOT_PASSWORD} -e "SHOW PROCESSLIST;"
```

### Backup e Recuperação

Script de backup (`/opt/scripts/backup-trancasdee.sh`):

```bash
#!/bin/bash

BACKUP_DIR="/var/backups/trancasdee"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Backup do banco
docker exec trancasdee-db-prod mysqldump -u root -p${DB_ROOT_PASSWORD} sys > $BACKUP_DIR/db_$DATE.sql

# Backup dos volumes
tar -czf $BACKUP_DIR/volumes_$DATE.tar.gz /var/lib/trancasdee/

# Limpar backups antigos (manter últimos 7 dias)
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete

echo "Backup realizado: $DATE"
```

Script de recuperação:

```bash
#!/bin/bash

BACKUP_FILE=$1

if [ -z "$BACKUP_FILE" ]; then
    echo "Uso: $0 <arquivo_backup.sql>"
    exit 1
fi

# Parar aplicação
docker-compose -f /opt/trancasdee/docker-compose.prod.yml stop spring-app

# Restaurar banco
docker exec -i trancasdee-db-prod mysql -u root -p${DB_ROOT_PASSWORD} sys < $BACKUP_FILE

# Reiniciar aplicação
docker-compose -f /opt/trancasdee/docker-compose.prod.yml start spring-app

echo "Restauração concluída"
```

---

## 📞 Suporte

Para problemas ou dúvidas sobre deploy:

1. Verificar logs da aplicação
2. Consultar documentação da API
3. Verificar configurações de banco
4. Contatar equipe de desenvolvimento do DEE

---

**Departamento de Engenharia Elétrica**  
Guia de Deploy - Sistema trancasDEE