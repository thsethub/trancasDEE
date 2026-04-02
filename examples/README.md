# Exemplos de Uso - trancasDEE

Esta pasta contém exemplos práticos de como usar o sistema trancasDEE.

## Scripts de Exemplo

### 1. Cadastro Completo de Usuário

```bash
#!/bin/bash
# cadastro-usuario.sh

# Verificar se usuário já existe
CPF="12345678901"
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000/usuarios/$CPF)

if [ "$response" == "200" ]; then
    echo "Usuário já existe com CPF: $CPF"
    # Buscar dados do usuário
    curl -s http://localhost:3000/usuarios/$CPF | jq .
else
    echo "Usuário não encontrado. Verifique se foi cadastrado no banco de dados."
fi

# Associar UniqueID ao crachá
UNIQUE_ID="987654321"
echo "Associando UniqueID $UNIQUE_ID ao usuário $CPF..."

curl -X PUT http://localhost:3000/usuarios/$CPF \
    -H "Content-Type: application/json" \
    -d "{\"UniqueID\": $UNIQUE_ID}" \
    | jq .

# Verificar acessos do usuário
echo "Verificando acessos disponíveis para UniqueID $UNIQUE_ID..."
curl -s http://localhost:3000/acesso/uniqueID/$UNIQUE_ID | jq .
```

### 2. Verificação de Acesso por Crachá

```bash
#!/bin/bash
# verificar-acesso.sh

UNIQUE_ID="987654321"
SALA_ID="1"

echo "=== Verificação de Acesso ==="
echo "UniqueID: $UNIQUE_ID"
echo "Sala: $SALA_ID"
echo

# Verificar se UniqueID tem acessos gerais
echo "1. Verificando acessos gerais para UniqueID $UNIQUE_ID:"
curl -s http://localhost:3000/acesso/uniqueID/$UNIQUE_ID | jq '.[].nome_ambiente'

echo
echo "2. Verificando acesso específico para sala $SALA_ID:"
response=$(curl -s http://localhost:3000/acesso/sala/$SALA_ID/uniqueID/$UNIQUE_ID)

if [ "$(echo $response | jq '. | length')" -gt 0 ]; then
    echo "✅ ACESSO AUTORIZADO"
    echo $response | jq '.[] | {ambiente: .nome_ambiente, data_limite: .data_limite, horario: (.hora_acesso_inicial + " - " + .hora_acesso_final)}'
else
    echo "❌ ACESSO NEGADO"
fi
```

### 3. Monitoramento de Eventos

```bash
#!/bin/bash
# monitor-eventos.sh

echo "=== Monitor de Eventos trancasDEE ==="
echo "Pressione Ctrl+C para parar"
echo

while true; do
    clear
    echo "=== Eventos Recentes ($(date)) ==="
    echo
    
    # Buscar todos os eventos e mostrar os 10 mais recentes
    curl -s http://localhost:3000/eventos | jq -r '.[-10:][] | [.id, .evento, .data_hora, .unique_id, .ambiente] | @tsv' | \
    while IFS=$'\t' read -r id evento data_hora unique_id ambiente; do
        printf "%-5s %-20s %-20s %-12s %-10s\n" "$id" "$evento" "$data_hora" "$unique_id" "$ambiente"
    done
    
    echo
    echo "Atualizando em 10 segundos..."
    sleep 10
done
```

### 4. Relatório de Usuários

```bash
#!/bin/bash
# relatorio-usuarios.sh

echo "=== Relatório de Usuários trancasDEE ==="
echo

# Buscar todos os usuários
usuarios=$(curl -s http://localhost:3000/usuarios)

echo "Total de usuários: $(echo $usuarios | jq '. | length')"
echo
echo "Usuários com crachá associado:"
echo $usuarios | jq -r '.[] | select(.unique_id != null) | [.nome, .cpf, .unique_id] | @tsv' | \
while IFS=$'\t' read -r nome cpf unique_id; do
    printf "%-30s CPF: %-15s UniqueID: %-12s\n" "$nome" "$cpf" "$unique_id"
done

echo
echo "Usuários sem crachá:"
echo $usuarios | jq -r '.[] | select(.unique_id == null) | [.nome, .cpf] | @tsv' | \
while IFS=$'\t' read -r nome cpf; do
    printf "%-30s CPF: %-15s\n" "$nome" "$cpf"
done
```

### 5. Backup de Dados via API

```bash
#!/bin/bash
# backup-api.sh

BACKUP_DIR="backup-$(date +%Y%m%d_%H%M%S)"
mkdir -p $BACKUP_DIR

echo "=== Backup dos dados via API ==="
echo "Diretório: $BACKUP_DIR"

# Backup de usuários
echo "Fazendo backup de usuários..."
curl -s http://localhost:3000/usuarios > $BACKUP_DIR/usuarios.json

# Backup de ambientes
echo "Fazendo backup de ambientes..."
curl -s http://localhost:3000/ambientes > $BACKUP_DIR/ambientes.json

# Backup de acessos
echo "Fazendo backup de acessos..."
curl -s http://localhost:3000/acesso > $BACKUP_DIR/acessos.json

# Backup de eventos
echo "Fazendo backup de eventos..."
curl -s http://localhost:3000/eventos > $BACKUP_DIR/eventos.json

# Compactar backup
tar -czf $BACKUP_DIR.tar.gz $BACKUP_DIR/
rm -rf $BACKUP_DIR/

echo "Backup concluído: $BACKUP_DIR.tar.gz"
```

### 6. Teste de Performance

```bash
#!/bin/bash
# teste-performance.sh

echo "=== Teste de Performance trancasDEE ==="

# Teste de carga simples nos endpoints principais
endpoints=(
    "/usuarios"
    "/ambientes"
    "/acesso"
    "/eventos"
)

for endpoint in "${endpoints[@]}"; do
    echo "Testando endpoint: $endpoint"
    
    # Usar 'time' para medir tempo de resposta
    for i in {1..10}; do
        start_time=$(date +%s%N)
        response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000$endpoint)
        end_time=$(date +%s%N)
        
        duration=$(((end_time - start_time) / 1000000)) # Convert to milliseconds
        
        if [ "$response" == "200" ]; then
            printf "  Requisição %2d: %3d ms ✅\n" $i $duration
        else
            printf "  Requisição %2d: %3d ms ❌ (HTTP %s)\n" $i $duration $response
        fi
    done
    echo
done
```

## Exemplos de Integração

### Python Client

```python
# cliente-python.py
import requests
import json
from datetime import datetime

class TrancasDEEClient:
    def __init__(self, base_url="http://localhost:3000"):
        self.base_url = base_url
    
    def verificar_acesso(self, unique_id, sala_id):
        """Verifica se um UniqueID tem acesso a uma sala específica"""
        url = f"{self.base_url}/acesso/sala/{sala_id}/uniqueID/{unique_id}"
        response = requests.get(url)
        
        if response.status_code == 200:
            acessos = response.json()
            if acessos:
                return {
                    "autorizado": True,
                    "detalhes": acessos[0]
                }
        
        return {"autorizado": False}
    
    def associar_cracha(self, cpf, unique_id):
        """Associa um UniqueID (crachá) a um usuário"""
        url = f"{self.base_url}/usuarios/{cpf}"
        data = {"UniqueID": unique_id}
        
        response = requests.put(url, json=data)
        return response.status_code == 200
    
    def listar_usuarios_sem_cracha(self):
        """Lista usuários que não têm crachá associado"""
        url = f"{self.base_url}/usuarios"
        response = requests.get(url)
        
        if response.status_code == 200:
            usuarios = response.json()
            return [u for u in usuarios if u.get('unique_id') is None]
        
        return []

# Exemplo de uso
if __name__ == "__main__":
    client = TrancasDEEClient()
    
    # Verificar acesso
    resultado = client.verificar_acesso(987654321, 1)
    print(f"Acesso autorizado: {resultado['autorizado']}")
    
    # Listar usuários sem crachá
    usuarios_sem_cracha = client.listar_usuarios_sem_cracha()
    print(f"Usuários sem crachá: {len(usuarios_sem_cracha)}")
```

### Node.js Client

```javascript
// cliente-nodejs.js
const axios = require('axios');

class TrancasDEEClient {
    constructor(baseURL = 'http://localhost:3000') {
        this.api = axios.create({ baseURL });
    }

    async verificarAcesso(uniqueID, salaID) {
        try {
            const response = await this.api.get(`/acesso/sala/${salaID}/uniqueID/${uniqueID}`);
            return {
                autorizado: response.data.length > 0,
                detalhes: response.data[0] || null
            };
        } catch (error) {
            return { autorizado: false, erro: error.message };
        }
    }

    async associarCracha(cpf, uniqueID) {
        try {
            await this.api.put(`/usuarios/${cpf}`, { UniqueID: uniqueID });
            return true;
        } catch (error) {
            console.error('Erro ao associar crachá:', error.message);
            return false;
        }
    }

    async monitorarEventos() {
        try {
            const response = await this.api.get('/eventos');
            return response.data.slice(-10); // Últimos 10 eventos
        } catch (error) {
            console.error('Erro ao buscar eventos:', error.message);
            return [];
        }
    }
}

// Exemplo de uso
async function exemplo() {
    const client = new TrancasDEEClient();
    
    // Verificar acesso
    const acesso = await client.verificarAcesso(987654321, 1);
    console.log('Acesso autorizado:', acesso.autorizado);
    
    // Monitorar eventos
    const eventos = await client.monitorarEventos();
    console.log(`Últimos eventos: ${eventos.length}`);
}

exemplo();
```

## Cenários de Uso Comuns

### Cenário 1: Novo Funcionário

1. Funcionário é cadastrado no banco de dados (fora da API)
2. Funcionário recebe crachá físico com UniqueID
3. Administrador associa UniqueID ao CPF via API
4. Sistema de acessos configura permissões
5. Funcionário pode usar o crachá

### Cenário 2: Controle de Acesso em Tempo Real

1. Crachá é apresentado ao leitor
2. Sistema consulta `/acesso/sala/{id}/uniqueID/{uniqueID}`
3. Verifica horário atual contra horários permitidos
4. Verifica data atual contra data limite
5. Autoriza ou nega acesso
6. Registra evento no sistema

### Cenário 3: Auditoria de Acessos

1. Administrador consulta `/eventos` para ver todos os eventos
2. Filtra eventos por período de interesse
3. Analisa padrões de acesso por usuário/ambiente
4. Gera relatórios de uso do sistema

## Scripts de Manutenção

### Limpeza de Dados Antigos

```bash
#!/bin/bash
# limpeza-dados.sh

# Este script simula a limpeza via API
# Na prática, a limpeza seria feita diretamente no banco

echo "=== Limpeza de Dados Antigos ==="

# Buscar eventos para análise (exemplo)
curl -s http://localhost:3000/eventos | jq '.[] | select(.data_hora < "2024-01-01")' | jq length

echo "Eventos antigos identificados. Execute limpeza no banco de dados."
```

### Validação de Integridade

```bash
#!/bin/bash
# validacao-integridade.sh

echo "=== Validação de Integridade dos Dados ==="

# Verificar usuários órfãos (com acessos mas sem UniqueID)
echo "Verificando usuários sem UniqueID que têm acessos..."

usuarios=$(curl -s http://localhost:3000/usuarios)
acessos=$(curl -s http://localhost:3000/acesso)

# Processar dados com jq para encontrar inconsistências
echo $usuarios | jq -r '.[] | select(.unique_id == null) | .cpf' | while read cpf; do
    has_access=$(echo $acessos | jq --arg cpf "$cpf" '.[] | select(.cpf_usuario == ($cpf | tonumber))')
    if [ -n "$has_access" ]; then
        echo "⚠️  Usuário $cpf tem acessos mas não tem UniqueID"
    fi
done

echo "Validação concluída."
```

## Uso destes Exemplos

1. **Tornar scripts executáveis:**
```bash
chmod +x *.sh
```

2. **Instalar dependências para exemplos Python:**
```bash
pip install requests
```

3. **Instalar dependências para exemplos Node.js:**
```bash
npm install axios
```

4. **Executar exemplos:**
```bash
./cadastro-usuario.sh
./verificar-acesso.sh
python cliente-python.py
node cliente-nodejs.js
```

Estes exemplos fornecem uma base sólida para integração e uso do sistema trancasDEE em diferentes cenários e linguagens de programação.