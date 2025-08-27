# trancasDEE - Sistema de Gerenciamento de Crachás

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MariaDB](https://img.shields.io/badge/MariaDB-Compatible-blue.svg)](https://mariadb.org/)

Sistema de microserviço para gerenciamento de crachás e controle de acesso do Departamento de Engenharia Elétrica (DEE).

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [API Endpoints](#api-endpoints)
- [Modelos de Dados](#modelos-de-dados)
- [Docker](#docker)
- [Exemplos de Uso](#exemplos-de-uso)
- [Contribuição](#contribuição)

## 🎯 Visão Geral

O **trancasDEE** é um microserviço desenvolvido em Spring Boot para gerenciar o sistema de controle de acesso por crachás do Departamento de Engenharia Elétrica. O sistema permite:

- Cadastro e gerenciamento de usuários
- Controle de acesso a ambientes/salas
- Registro de eventos de acesso
- Gerenciamento de permissões baseadas em tempo
- Associação de IDs únicos aos crachás dos usuários

## ✨ Funcionalidades

### 👥 Gerenciamento de Usuários
- Cadastro de usuários com CPF, nome e nível de acesso
- Associação de IDs únicos para identificação por crachá
- Controle de permissões de acesso

### 🏢 Gerenciamento de Ambientes
- Cadastro de salas e ambientes
- Organização por tópicos/categorias
- Controle de acesso por ambiente

### 🔐 Controle de Acesso
- Definição de horários permitidos (inicial e final)
- Configuração de datas limite para acesso
- Vinculação usuário-ambiente com restrições temporais

### 📊 Registro de Eventos
- Log de todas as tentativas de acesso
- Registro de data/hora de cada evento
- Rastreamento por ID único e ambiente

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │────│    Services     │────│  Repositories   │
│                 │    │                 │    │                 │
│ • UsuariosCtrl  │    │ • UsuariosServ  │    │ • UsuariosRepo  │
│ • AmbientesCtrl │    │ • AmbientesServ │    │ • AmbientesRepo │
│ • AcessoCtrl    │    │ • AcessoServ    │    │ • AcessoRepo    │
│ • EventosCtrl   │    │ • EventosServ   │    │ • EventosRepo   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │    MariaDB      │
                    │   Schema: sys   │
                    └─────────────────┘
```

### Stack Tecnológica
- **Backend**: Spring Boot 3.4.2
- **Linguagem**: Java 17
- **Banco de Dados**: MariaDB
- **ORM**: Spring Data JPA / Hibernate
- **Build**: Maven
- **Containerização**: Docker

## 🚀 Instalação

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+
- MariaDB 10.3+
- Docker (opcional)

### Instalação Local

1. **Clone o repositório:**
```bash
git clone https://github.com/thsethub/trancasDEE.git
cd trancasDEE
```

2. **Configure o banco de dados MariaDB:**
```sql
CREATE DATABASE sys;
CREATE USER 'trancasdee'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON sys.* TO 'trancasdee'@'localhost';
FLUSH PRIVILEGES;
```

3. **Compile o projeto:**
```bash
./mvnw clean compile
```

4. **Execute o projeto:**
```bash
./mvnw spring-boot:run
```

O serviço estará disponível em `http://localhost:3000`

## ⚙️ Configuração

### application.properties

```properties
# Configuração da aplicação
spring.application.name=trancasDEE
server.port=3000

# Configuração do banco de dados
spring.datasource.url=jdbc:mariadb://localhost:3306/sys
spring.datasource.username=trancasdee
spring.datasource.password=password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# Configurações JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

# Logs
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Serialização JSON
spring.jackson.property-naming-strategy=SNAKE_CASE
```

### Variáveis de Ambiente

```bash
export SPRING_DATASOURCE_URL=jdbc:mariadb://sua-db-host:3306/sys
export SPRING_DATASOURCE_USERNAME=seu-usuario
export SPRING_DATASOURCE_PASSWORD=sua-senha
export SERVER_PORT=3000
```

## 📡 API Endpoints

### 👥 Usuários (`/usuarios`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/usuarios` | Lista todos os usuários |
| GET | `/usuarios/{id}` | Busca usuário por ID (CPF) |
| PUT | `/usuarios/{id}` | Atualiza UniqueID do usuário |

**Exemplo - Atualizar UniqueID:**
```bash
curl -X PUT http://localhost:3000/usuarios/12345678901 \
  -H "Content-Type: application/json" \
  -d '{"UniqueID": 987654321}'
```

### 🏢 Ambientes (`/ambientes`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/ambientes` | Lista todos os ambientes |
| GET | `/ambientes/{id}` | Busca ambiente por ID |
| GET | `/ambientes/sala/{sala}` | Busca ambiente por sala |

### 🔐 Acessos (`/acesso`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/acesso` | Lista todos os acessos |
| GET | `/acesso/{id}` | Busca acesso por ID |
| GET | `/acesso/usuario/{usuario}` | Lista acessos por usuário |
| GET | `/acesso/ambientes/{ambiente}` | Lista acessos por ambiente |
| GET | `/acesso/uniqueID/{uniqueID}` | Lista acessos por UniqueID |
| GET | `/acesso/sala/{sala}/uniqueID/{uniqueID}` | Busca acesso específico |

**Exemplo de Resposta - Lista de Acessos:**
```json
[
  {
    "id": 1,
    "nomeAmbiente": "Laboratório 101",
    "cpfUsuario": 12345678901,
    "uniqueID": 987654321,
    "dataLimite": "2024-12-31T23:59:59Z",
    "horaAcessoInicial": "08:00:00",
    "horaAcessoFinal": "18:00:00"
  }
]
```

### 📊 Eventos (`/eventos`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/eventos` | Lista todos os eventos |

## 📊 Modelos de Dados

### Usuários
```sql
CREATE TABLE Usuarios (
    CPF BIGINT PRIMARY KEY,
    Nome VARCHAR(255) NOT NULL,
    UniqueID BIGINT,
    Acesso INT NOT NULL
);
```

### Ambientes
```sql
CREATE TABLE Ambientes (
    id BIGINT PRIMARY KEY,
    Sala MEDIUMTEXT NOT NULL,
    Topico MEDIUMTEXT NOT NULL
);
```

### Acesso
```sql
CREATE TABLE Acesso (
    id BIGINT PRIMARY KEY,
    Sala BIGINT,
    CPF BIGINT,
    Data_Limite TIMESTAMP NOT NULL,
    Hora_acesso_inicial TIME NOT NULL,
    Hora_acesso_final TIME NOT NULL,
    FOREIGN KEY (Sala) REFERENCES Ambientes(id),
    FOREIGN KEY (CPF) REFERENCES Usuarios(CPF)
);
```

### Eventos
```sql
CREATE TABLE Eventos (
    id BIGINT PRIMARY KEY,
    Evento VARCHAR(255),
    DataHora VARCHAR(255),
    UniqueID BIGINT,
    Ambiente BIGINT
);
```

## 🐳 Docker

### Dockerfile
O projeto inclui um Dockerfile otimizado:

```dockerfile
FROM eclipse-temurin:17-jdk-jammy
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests
ENTRYPOINT ["java", "-jar", "target/trancasDEE-0.0.1-SNAPSHOT.jar"]
```

### Docker Compose
Execute com docker-compose:

```bash
docker-compose up -d
```

O serviço estará disponível em `http://localhost:3000`

### Build Manual
```bash
# Build da imagem
docker build -t trancasdee:latest .

# Execução do container
docker run -p 3000:3000 \
  -e SPRING_DATASOURCE_URL=jdbc:mariadb://host-db:3306/sys \
  -e SPRING_DATASOURCE_USERNAME=usuario \
  -e SPRING_DATASOURCE_PASSWORD=senha \
  trancasdee:latest
```

## 💡 Exemplos de Uso

### Cenário: Cadastro de Novo Acesso

1. **Verificar usuário existente:**
```bash
curl http://localhost:3000/usuarios/12345678901
```

2. **Associar UniqueID ao crachá:**
```bash
curl -X PUT http://localhost:3000/usuarios/12345678901 \
  -H "Content-Type: application/json" \
  -d '{"UniqueID": 987654321}'
```

3. **Verificar ambientes disponíveis:**
```bash
curl http://localhost:3000/ambientes
```

4. **Consultar acessos por UniqueID:**
```bash
curl http://localhost:3000/acesso/uniqueID/987654321
```

### Cenário: Monitoramento de Eventos

```bash
# Listar todos os eventos de acesso
curl http://localhost:3000/eventos

# Verificar acessos de um ambiente específico
curl http://localhost:3000/acesso/ambientes/1
```

## 🔧 Desenvolvimento

### Executar em modo de desenvolvimento:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Executar testes:
```bash
./mvnw test
```

### Build para produção:
```bash
./mvnw clean package -DskipTests
```

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📝 Licença

Este projeto é desenvolvido para uso interno do Departamento de Engenharia Elétrica.

## 📞 Suporte

Para dúvidas ou suporte técnico, entre em contato com a equipe de desenvolvimento do DEE.

---

**Departamento de Engenharia Elétrica**  
Sistema de Controle de Acesso por Crachás