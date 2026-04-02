# Database Documentation - trancasDEE

Esta documentação descreve a estrutura do banco de dados do sistema trancasDEE, incluindo esquemas, relacionamentos e scripts de configuração.

## Visão Geral

O sistema utiliza **MariaDB** como banco de dados principal, organizando os dados em quatro tabelas principais no schema `sys`.

## Diagrama de Relacionamentos

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    Usuarios     │    │    Ambientes    │    │     Eventos     │
│                 │    │                 │    │                 │
│ CPF (PK)        │    │ id (PK)         │    │ id (PK)         │
│ Nome            │    │ Sala            │    │ Evento          │
│ UniqueID        │    │ Topico          │    │ DataHora        │
│ Acesso          │    │                 │    │ UniqueID        │
└─────────┬───────┘    └─────────┬───────┘    │ Ambiente        │
          │                      │            └─────────────────┘
          │                      │                      
          │                      │            
          │    ┌─────────────────┐│            
          │    │     Acesso      ││            
          │    │                 ││            
          │    │ id (PK)         ││            
          └────┤ CPF (FK)        ││            
               │ Sala (FK)       │┘            
               │ Data_Limite     │             
               │ Hora_acesso_ini │             
               │ Hora_acesso_fin │             
               └─────────────────┘             
```

## Estrutura das Tabelas

### Tabela: Usuarios

Armazena informações dos usuários do sistema.

```sql
CREATE TABLE Usuarios (
    CPF BIGINT PRIMARY KEY COMMENT 'CPF do usuário (11 dígitos)',
    Nome VARCHAR(255) NOT NULL COMMENT 'Nome completo do usuário',
    UniqueID BIGINT NULL COMMENT 'ID único associado ao crachá',
    Acesso INT NOT NULL COMMENT 'Nível de acesso do usuário'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Colunas:**
- `CPF`: Chave primária, identificador único do usuário
- `Nome`: Nome completo do usuário
- `UniqueID`: Identificador único do crachá (pode ser NULL até ser associado)
- `Acesso`: Nível de acesso (1=básico, 2=intermediário, 3=avançado, etc.)

**Índices:**
```sql
CREATE INDEX idx_usuarios_unique_id ON Usuarios(UniqueID);
CREATE INDEX idx_usuarios_acesso ON Usuarios(Acesso);
```

---

### Tabela: Ambientes

Armazena informações sobre salas e ambientes físicos.

```sql
CREATE TABLE Ambientes (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único do ambiente',
    Sala MEDIUMTEXT NOT NULL COMMENT 'Nome/identificação da sala',
    Topico MEDIUMTEXT NOT NULL COMMENT 'Descrição ou categoria do ambiente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Colunas:**
- `id`: Chave primária auto-incrementada
- `Sala`: Nome ou identificação da sala/ambiente
- `Topico`: Categoria ou descrição do tipo de ambiente

**Índices:**
```sql
CREATE INDEX idx_ambientes_sala ON Ambientes(Sala(100));
CREATE INDEX idx_ambientes_topico ON Ambientes(Topico(100));
```

---

### Tabela: Acesso

Controla as permissões de acesso dos usuários aos ambientes.

```sql
CREATE TABLE Acesso (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único do registro de acesso',
    Sala BIGINT COMMENT 'Referência ao ambiente',
    CPF BIGINT COMMENT 'Referência ao usuário',
    Data_Limite TIMESTAMP NOT NULL COMMENT 'Data limite para o acesso',
    Hora_acesso_inicial TIME NOT NULL COMMENT 'Horário inicial permitido',
    Hora_acesso_final TIME NOT NULL COMMENT 'Horário final permitido',
    
    FOREIGN KEY (Sala) REFERENCES Ambientes(id) ON DELETE CASCADE,
    FOREIGN KEY (CPF) REFERENCES Usuarios(CPF) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Colunas:**
- `id`: Chave primária auto-incrementada
- `Sala`: Chave estrangeira referenciando Ambientes.id
- `CPF`: Chave estrangeira referenciando Usuarios.CPF
- `Data_Limite`: Data até quando o acesso é válido
- `Hora_acesso_inicial`: Horário a partir do qual o acesso é permitido
- `Hora_acesso_final`: Horário até quando o acesso é permitido

**Índices:**
```sql
CREATE INDEX idx_acesso_cpf ON Acesso(CPF);
CREATE INDEX idx_acesso_sala ON Acesso(Sala);
CREATE INDEX idx_acesso_data_limite ON Acesso(Data_Limite);
CREATE INDEX idx_acesso_composto ON Acesso(CPF, Sala, Data_Limite);
```

---

### Tabela: Eventos

Registra todos os eventos de acesso tentados no sistema.

```sql
CREATE TABLE Eventos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único do evento',
    Evento VARCHAR(255) COMMENT 'Tipo de evento (ACESSO_AUTORIZADO, ACESSO_NEGADO, etc.)',
    DataHora VARCHAR(255) COMMENT 'Data e hora do evento (formato string)',
    UniqueID BIGINT COMMENT 'ID único do crachá utilizado',
    Ambiente BIGINT COMMENT 'ID do ambiente onde ocorreu o evento'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Colunas:**
- `id`: Chave primária auto-incrementada
- `Evento`: Tipo de evento registrado
- `DataHora`: Timestamp do evento (armazenado como string)
- `UniqueID`: ID único do crachá utilizado na tentativa
- `Ambiente`: ID do ambiente onde ocorreu o evento

**Índices:**
```sql
CREATE INDEX idx_eventos_unique_id ON Eventos(UniqueID);
CREATE INDEX idx_eventos_ambiente ON Eventos(Ambiente);
CREATE INDEX idx_eventos_data_hora ON Eventos(DataHora);
CREATE INDEX idx_eventos_tipo ON Eventos(Evento);
```

---

## Scripts de Configuração

### Script de Criação do Banco

```sql
-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS sys 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE sys;

-- Criar tabela Usuarios
CREATE TABLE Usuarios (
    CPF BIGINT PRIMARY KEY COMMENT 'CPF do usuário (11 dígitos)',
    Nome VARCHAR(255) NOT NULL COMMENT 'Nome completo do usuário',
    UniqueID BIGINT NULL COMMENT 'ID único associado ao crachá',
    Acesso INT NOT NULL COMMENT 'Nível de acesso do usuário'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Criar tabela Ambientes
CREATE TABLE Ambientes (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único do ambiente',
    Sala MEDIUMTEXT NOT NULL COMMENT 'Nome/identificação da sala',
    Topico MEDIUMTEXT NOT NULL COMMENT 'Descrição ou categoria do ambiente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Criar tabela Acesso
CREATE TABLE Acesso (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único do registro de acesso',
    Sala BIGINT COMMENT 'Referência ao ambiente',
    CPF BIGINT COMMENT 'Referência ao usuário',
    Data_Limite TIMESTAMP NOT NULL COMMENT 'Data limite para o acesso',
    Hora_acesso_inicial TIME NOT NULL COMMENT 'Horário inicial permitido',
    Hora_acesso_final TIME NOT NULL COMMENT 'Horário final permitido',
    
    FOREIGN KEY (Sala) REFERENCES Ambientes(id) ON DELETE CASCADE,
    FOREIGN KEY (CPF) REFERENCES Usuarios(CPF) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Criar tabela Eventos
CREATE TABLE Eventos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID único do evento',
    Evento VARCHAR(255) COMMENT 'Tipo de evento',
    DataHora VARCHAR(255) COMMENT 'Data e hora do evento',
    UniqueID BIGINT COMMENT 'ID único do crachá utilizado',
    Ambiente BIGINT COMMENT 'ID do ambiente onde ocorreu o evento'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### Script de Índices

```sql
-- Índices para Usuarios
CREATE INDEX idx_usuarios_unique_id ON Usuarios(UniqueID);
CREATE INDEX idx_usuarios_acesso ON Usuarios(Acesso);

-- Índices para Ambientes
CREATE INDEX idx_ambientes_sala ON Ambientes(Sala(100));
CREATE INDEX idx_ambientes_topico ON Ambientes(Topico(100));

-- Índices para Acesso
CREATE INDEX idx_acesso_cpf ON Acesso(CPF);
CREATE INDEX idx_acesso_sala ON Acesso(Sala);
CREATE INDEX idx_acesso_data_limite ON Acesso(Data_Limite);
CREATE INDEX idx_acesso_composto ON Acesso(CPF, Sala, Data_Limite);

-- Índices para Eventos
CREATE INDEX idx_eventos_unique_id ON Eventos(UniqueID);
CREATE INDEX idx_eventos_ambiente ON Eventos(Ambiente);
CREATE INDEX idx_eventos_data_hora ON Eventos(DataHora);
CREATE INDEX idx_eventos_tipo ON Eventos(Evento);
```

### Script de Dados de Exemplo

```sql
-- Inserir usuários de exemplo
INSERT INTO Usuarios (CPF, Nome, UniqueID, Acesso) VALUES
(12345678901, 'João Silva', 987654321, 1),
(98765432100, 'Maria Santos', 123456789, 2),
(11122233344, 'Pedro Oliveira', NULL, 1),
(55566677788, 'Ana Costa', 555666777, 3);

-- Inserir ambientes de exemplo
INSERT INTO Ambientes (Sala, Topico) VALUES
('Laboratório 101', 'Eletrônica Analógica'),
('Sala 202', 'Sistemas Digitais'),
('Laboratório 303', 'Microcontroladores'),
('Sala 404', 'Processamento de Sinais');

-- Inserir registros de acesso de exemplo
INSERT INTO Acesso (Sala, CPF, Data_Limite, Hora_acesso_inicial, Hora_acesso_final) VALUES
(1, 12345678901, '2024-12-31 23:59:59', '08:00:00', '18:00:00'),
(2, 98765432100, '2024-12-31 23:59:59', '09:00:00', '17:00:00'),
(1, 55566677788, '2024-06-30 23:59:59', '08:00:00', '20:00:00'),
(3, 12345678901, '2024-12-31 23:59:59', '14:00:00', '18:00:00');

-- Inserir eventos de exemplo
INSERT INTO Eventos (Evento, DataHora, UniqueID, Ambiente) VALUES
('ACESSO_AUTORIZADO', '2024-03-15T08:30:00', 987654321, 1),
('ACESSO_NEGADO', '2024-03-15T14:35:00', 123456789, 3),
('ACESSO_AUTORIZADO', '2024-03-15T09:15:00', 555666777, 1),
('ACESSO_NEGADO', '2024-03-15T19:00:00', 987654321, 2);
```

---

## Configuração de Usuário

### Criar Usuário da Aplicação

```sql
-- Criar usuário específico para a aplicação
CREATE USER 'trancasdee'@'localhost' IDENTIFIED BY 'senha_segura_aqui';

-- Conceder permissões necessárias
GRANT SELECT, INSERT, UPDATE, DELETE ON sys.* TO 'trancasdee'@'localhost';

-- Para acesso remoto (se necessário)
CREATE USER 'trancasdee'@'%' IDENTIFIED BY 'senha_segura_aqui';
GRANT SELECT, INSERT, UPDATE, DELETE ON sys.* TO 'trancasdee'@'%';

-- Aplicar mudanças
FLUSH PRIVILEGES;
```

---

## Consultas Úteis

### Consultas de Auditoria

```sql
-- Usuários sem UniqueID associado
SELECT CPF, Nome FROM Usuarios WHERE UniqueID IS NULL;

-- Acessos expirados
SELECT a.*, u.Nome, am.Sala 
FROM Acesso a
JOIN Usuarios u ON a.CPF = u.CPF
JOIN Ambientes am ON a.Sala = am.id
WHERE a.Data_Limite < NOW();

-- Eventos por período
SELECT * FROM Eventos 
WHERE DataHora BETWEEN '2024-03-01' AND '2024-03-31'
ORDER BY DataHora DESC;
```

### Consultas de Performance

```sql
-- Acessos mais utilizados
SELECT am.Sala, COUNT(*) as tentativas
FROM Eventos e
JOIN Ambientes am ON e.Ambiente = am.id
GROUP BY am.Sala
ORDER BY tentativas DESC;

-- Usuários mais ativos
SELECT u.Nome, COUNT(*) as acessos
FROM Eventos e
JOIN Usuarios u ON e.UniqueID = u.UniqueID
GROUP BY u.Nome
ORDER BY acessos DESC;
```

---

## Backup e Manutenção

### Script de Backup

```bash
#!/bin/bash
# Backup do banco trancasDEE
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/trancasdee"
BACKUP_FILE="$BACKUP_DIR/trancasdee_$DATE.sql"

mkdir -p $BACKUP_DIR

mysqldump -u root -p sys > $BACKUP_FILE

# Compactar backup
gzip $BACKUP_FILE

echo "Backup realizado: $BACKUP_FILE.gz"
```

### Limpeza de Logs Antigos

```sql
-- Limpar eventos antigos (mais de 1 ano)
DELETE FROM Eventos 
WHERE STR_TO_DATE(DataHora, '%Y-%m-%dT%H:%i:%s') < DATE_SUB(NOW(), INTERVAL 1 YEAR);

-- Limpar acessos expirados há mais de 6 meses
DELETE FROM Acesso 
WHERE Data_Limite < DATE_SUB(NOW(), INTERVAL 6 MONTH);
```

---

## Monitoramento

### Verificação de Integridade

```sql
-- Verificar integridade referencial
SELECT 'Acessos órfãos - CPF' as problema, COUNT(*) as quantidade
FROM Acesso a LEFT JOIN Usuarios u ON a.CPF = u.CPF
WHERE u.CPF IS NULL

UNION ALL

SELECT 'Acessos órfãos - Sala', COUNT(*)
FROM Acesso a LEFT JOIN Ambientes am ON a.Sala = am.id
WHERE am.id IS NULL;
```

### Estatísticas de Uso

```sql
-- Estatísticas gerais
SELECT 
    'Total Usuários' as metrica, COUNT(*) as valor FROM Usuarios
UNION ALL
SELECT 'Usuários com Crachá', COUNT(*) FROM Usuarios WHERE UniqueID IS NOT NULL
UNION ALL
SELECT 'Total Ambientes', COUNT(*) FROM Ambientes
UNION ALL
SELECT 'Total Acessos Ativos', COUNT(*) FROM Acesso WHERE Data_Limite > NOW()
UNION ALL
SELECT 'Total Eventos Hoje', COUNT(*) FROM Eventos 
WHERE DataHora LIKE CONCAT(CURDATE(), '%');
```