# API Documentation - trancasDEE

Esta documentação descreve em detalhes todos os endpoints da API REST do sistema trancasDEE.

## Base URL
```
http://localhost:3000
```

## Content-Type
Todas as requisições que enviam dados devem usar:
```
Content-Type: application/json
```

## Formato de Resposta
As respostas seguem o padrão JSON com snake_case para nomeação de propriedades.

---

## 👥 Usuários

### GET /usuarios
Lista todos os usuários cadastrados no sistema.

**Resposta:**
```json
[
  {
    "cpf": 12345678901,
    "nome": "João Silva",
    "unique_id": 987654321,
    "acesso": 1
  },
  {
    "cpf": 98765432100,
    "nome": "Maria Santos",
    "unique_id": 123456789,
    "acesso": 2
  }
]
```

**Códigos de Status:**
- `200 OK` - Sucesso
- `500 Internal Server Error` - Erro interno do servidor

---

### GET /usuarios/{id}
Busca um usuário específico pelo CPF.

**Parâmetros:**
- `id` (path) - CPF do usuário (número de 11 dígitos)

**Exemplo:**
```bash
curl http://localhost:3000/usuarios/12345678901
```

**Resposta:**
```json
{
  "cpf": 12345678901,
  "nome": "João Silva",
  "unique_id": 987654321,
  "acesso": 1
}
```

**Códigos de Status:**
- `200 OK` - Usuário encontrado
- `404 Not Found` - Usuário não encontrado
- `400 Bad Request` - CPF inválido

---

### PUT /usuarios/{id}
Atualiza o UniqueID de um usuário (associação com crachá).

**Parâmetros:**
- `id` (path) - CPF do usuário
- `body` - JSON com o novo UniqueID

**Body da Requisição:**
```json
{
  "UniqueID": 987654321
}
```

**Exemplo:**
```bash
curl -X PUT http://localhost:3000/usuarios/12345678901 \
  -H "Content-Type: application/json" \
  -d '{"UniqueID": 987654321}'
```

**Resposta:**
```json
{
  "cpf": 12345678901,
  "nome": "João Silva",
  "unique_id": 987654321,
  "acesso": 1
}
```

**Códigos de Status:**
- `200 OK` - UniqueID atualizado com sucesso
- `404 Not Found` - Usuário não encontrado
- `400 Bad Request` - Dados inválidos

---

## 🏢 Ambientes

### GET /ambientes
Lista todos os ambientes/salas cadastrados.

**Resposta:**
```json
[
  {
    "id": 1,
    "sala": "Laboratório 101",
    "topico": "Eletrônica Analógica"
  },
  {
    "id": 2,
    "sala": "Sala 202",
    "topico": "Sistemas Digitais"
  }
]
```

**Códigos de Status:**
- `200 OK` - Sucesso

---

### GET /ambientes/{id}
Busca um ambiente específico pelo ID.

**Parâmetros:**
- `id` (path) - ID do ambiente

**Exemplo:**
```bash
curl http://localhost:3000/ambientes/1
```

**Resposta:**
```json
{
  "id": 1,
  "sala": "Laboratório 101",
  "topico": "Eletrônica Analógica"
}
```

**Códigos de Status:**
- `200 OK` - Ambiente encontrado
- `404 Not Found` - Ambiente não encontrado

---

### GET /ambientes/sala/{sala}
Busca ambientes por um objeto Ambiente específico.

**Parâmetros:**
- `sala` (path) - Objeto Ambiente para busca

**Códigos de Status:**
- `200 OK` - Sucesso
- `400 Bad Request` - Parâmetro inválido

---

## 🔐 Controle de Acesso

### GET /acesso
Lista todos os registros de acesso.

**Resposta:**
```json
[
  {
    "id": 1,
    "nome_ambiente": "Laboratório 101",
    "cpf_usuario": 12345678901,
    "unique_id": 987654321,
    "data_limite": "2024-12-31T23:59:59Z",
    "hora_acesso_inicial": "08:00:00",
    "hora_acesso_final": "18:00:00"
  }
]
```

**Códigos de Status:**
- `200 OK` - Sucesso

---

### GET /acesso/{id}
Busca um registro de acesso específico pelo ID.

**Parâmetros:**
- `id` (path) - ID do registro de acesso

**Exemplo:**
```bash
curl http://localhost:3000/acesso/1
```

**Resposta:**
```json
{
  "id": 1,
  "ambientes": {
    "id": 1,
    "sala": "Laboratório 101",
    "topico": "Eletrônica Analógica"
  },
  "usuarios": {
    "cpf": 12345678901,
    "nome": "João Silva",
    "unique_id": 987654321,
    "acesso": 1
  },
  "data_limite": "2024-12-31T23:59:59Z",
  "hora_acesso_inicial": "08:00:00",
  "hora_acesso_final": "18:00:00"
}
```

**Códigos de Status:**
- `200 OK` - Registro encontrado
- `404 Not Found` - Registro não encontrado

---

### GET /acesso/usuario/{usuario}
Lista todos os acessos de um usuário específico.

**Parâmetros:**
- `usuario` (path) - Objeto Usuario para busca

**Resposta:**
```json
[
  {
    "id": 1,
    "nome_ambiente": "Laboratório 101",
    "cpf_usuario": 12345678901,
    "unique_id": 987654321,
    "data_limite": "2024-12-31T23:59:59Z",
    "hora_acesso_inicial": "08:00:00",
    "hora_acesso_final": "18:00:00"
  }
]
```

**Códigos de Status:**
- `200 OK` - Sucesso

---

### GET /acesso/ambientes/{ambientes}
Lista todos os acessos para um ambiente específico.

**Parâmetros:**
- `ambientes` (path) - Objeto Ambiente para busca

**Resposta:**
```json
[
  {
    "id": 1,
    "nome_ambiente": "Laboratório 101",
    "cpf_usuario": 12345678901,
    "unique_id": 987654321,
    "data_limite": "2024-12-31T23:59:59Z",
    "hora_acesso_inicial": "08:00:00",
    "hora_acesso_final": "18:00:00"
  }
]
```

**Códigos de Status:**
- `200 OK` - Sucesso

---

### GET /acesso/uniqueID/{uniqueID}
Lista todos os acessos para um UniqueID específico (crachá).

**Parâmetros:**
- `uniqueID` (path) - ID único do crachá

**Exemplo:**
```bash
curl http://localhost:3000/acesso/uniqueID/987654321
```

**Resposta:**
```json
[
  {
    "id": 1,
    "nome_ambiente": "Laboratório 101",
    "cpf_usuario": 12345678901,
    "unique_id": 987654321,
    "data_limite": "2024-12-31T23:59:59Z",
    "hora_acesso_inicial": "08:00:00",
    "hora_acesso_final": "18:00:00"
  }
]
```

**Códigos de Status:**
- `200 OK` - Sucesso
- `404 Not Found` - UniqueID não encontrado

---

### GET /acesso/sala/{sala}/uniqueID/{uniqueID}
Busca acesso específico por sala e UniqueID.

**Parâmetros:**
- `sala` (path) - ID da sala
- `uniqueID` (path) - ID único do crachá

**Exemplo:**
```bash
curl http://localhost:3000/acesso/sala/1/uniqueID/987654321
```

**Resposta:**
```json
[
  {
    "id": 1,
    "nome_ambiente": "Laboratório 101",
    "cpf_usuario": 12345678901,
    "unique_id": 987654321,
    "data_limite": "2024-12-31T23:59:59Z",
    "hora_acesso_inicial": "08:00:00",
    "hora_acesso_final": "18:00:00"
  }
]
```

**Códigos de Status:**
- `200 OK` - Sucesso
- `404 Not Found` - Acesso não encontrado

---

## 📊 Eventos

### GET /eventos
Lista todos os eventos de acesso registrados no sistema.

**Resposta:**
```json
[
  {
    "id": 1,
    "evento": "ACESSO_AUTORIZADO",
    "data_hora": "2024-03-15T14:30:00",
    "unique_id": 987654321,
    "ambiente": 1
  },
  {
    "id": 2,
    "evento": "ACESSO_NEGADO",
    "data_hora": "2024-03-15T14:35:00",
    "unique_id": 123456789,
    "ambiente": 2
  }
]
```

**Códigos de Status:**
- `200 OK` - Sucesso

---

## 🚨 Códigos de Erro

### Estrutura de Erro
```json
{
  "timestamp": "2024-03-15T14:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Usuário não encontrado",
  "path": "/usuarios/12345678901"
}
```

### Códigos Comuns
- `200 OK` - Requisição bem-sucedida
- `400 Bad Request` - Dados da requisição inválidos
- `404 Not Found` - Recurso não encontrado
- `500 Internal Server Error` - Erro interno do servidor

---

## 📝 Exemplos de Integração

### Fluxo Completo: Verificação de Acesso

1. **Verificar se UniqueID existe:**
```bash
curl http://localhost:3000/acesso/uniqueID/987654321
```

2. **Se não existir, verificar usuário e associar UniqueID:**
```bash
curl http://localhost:3000/usuarios/12345678901
curl -X PUT http://localhost:3000/usuarios/12345678901 \
  -H "Content-Type: application/json" \
  -d '{"UniqueID": 987654321}'
```

3. **Verificar permissões para sala específica:**
```bash
curl http://localhost:3000/acesso/sala/1/uniqueID/987654321
```

### Script de Monitoramento
```bash
#!/bin/bash
# Monitor de eventos em tempo real
while true; do
  echo "=== Eventos recentes ==="
  curl -s http://localhost:3000/eventos | jq '.[-5:]'
  sleep 10
done
```

---

## 🔍 Filtros e Consultas

### Consultas por Usuário
```bash
# Todos os acessos de um usuário
curl http://localhost:3000/acesso/usuario/{usuario_obj}

# Verificar se usuário tem UniqueID
curl http://localhost:3000/usuarios/{cpf}
```

### Consultas por Ambiente
```bash
# Todos os acessos para um ambiente
curl http://localhost:3000/acesso/ambientes/{ambiente_obj}

# Detalhes do ambiente
curl http://localhost:3000/ambientes/{id}
```

### Auditoria de Acesso
```bash
# Todos os eventos
curl http://localhost:3000/eventos

# Todos os registros de acesso
curl http://localhost:3000/acesso
```