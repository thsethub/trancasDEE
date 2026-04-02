#!/bin/bash
# Script para verificar acesso de um crachá a uma sala específica

BASE_URL="http://localhost:3000"

# Verificar parâmetros
if [ $# -ne 2 ]; then
    echo "Uso: $0 <unique_id> <sala_id>"
    echo "Exemplo: $0 987654321 1"
    exit 1
fi

UNIQUE_ID="$1"
SALA_ID="$2"

echo "=== Verificação de Acesso - trancasDEE ==="
echo "UniqueID do crachá: $UNIQUE_ID"
echo "ID da sala: $SALA_ID"
echo "Data/Hora atual: $(date)"
echo

# Verificar se o crachá existe no sistema
echo "1. Verificando se o crachá está registrado no sistema..."
acessos_gerais=$(curl -s "$BASE_URL/acesso/uniqueID/$UNIQUE_ID")

if [ "$(echo "$acessos_gerais" | jq '. | length')" -eq 0 ]; then
    echo "❌ Crachá não encontrado no sistema"
    echo "   Verifique se o UniqueID $UNIQUE_ID está correto"
    exit 1
else
    count=$(echo "$acessos_gerais" | jq '. | length')
    echo "✅ Crachá encontrado com $count permissão(ões) de acesso"
fi

echo
echo "2. Verificando acesso específico para a sala $SALA_ID..."

# Verificar acesso específico à sala
acesso_especifico=$(curl -s "$BASE_URL/acesso/sala/$SALA_ID/uniqueID/$UNIQUE_ID")

if [ "$(echo "$acesso_especifico" | jq '. | length')" -eq 0 ]; then
    echo "❌ ACESSO NEGADO"
    echo "   O crachá $UNIQUE_ID não tem permissão para acessar a sala $SALA_ID"
    
    echo
    echo "3. Permissões disponíveis para este crachá:"
    echo "$acessos_gerais" | jq -r '.[] | "   - Sala: \(.nome_ambiente) (ID: \(.id))"'
    
else
    echo "✅ ACESSO AUTORIZADO"
    
    echo
    echo "3. Detalhes da permissão:"
    echo "$acesso_especifico" | jq -r '.[] | "   Ambiente: \(.nome_ambiente)
   Data limite: \(.data_limite)
   Horário permitido: \(.hora_acesso_inicial) - \(.hora_acesso_final)
   CPF do usuário: \(.cpf_usuario)"'
    
    # Verificar se está dentro do horário
    echo
    echo "4. Verificação de horário:"
    
    hora_atual=$(date +%H:%M:%S)
    data_atual=$(date -u +%Y-%m-%dT%H:%M:%SZ)
    
    hora_inicio=$(echo "$acesso_especifico" | jq -r '.[0].hora_acesso_inicial')
    hora_fim=$(echo "$acesso_especifico" | jq -r '.[0].hora_acesso_final')
    data_limite=$(echo "$acesso_especifico" | jq -r '.[0].data_limite')
    
    echo "   Horário atual: $hora_atual"
    echo "   Horário permitido: $hora_inicio - $hora_fim"
    echo "   Data limite: $data_limite"
    
    # Verificar data (comparação simplificada)
    if [[ "$data_atual" < "$data_limite" ]]; then
        echo "   ✅ Dentro do prazo de validade"
    else
        echo "   ❌ Acesso expirado"
    fi
    
    # Verificar horário (comparação simplificada)
    if [[ "$hora_atual" > "$hora_inicio" && "$hora_atual" < "$hora_fim" ]]; then
        echo "   ✅ Dentro do horário permitido"
    else
        echo "   ⚠️  Fora do horário permitido"
    fi
fi

echo
echo "=== Verificação concluída ===" 