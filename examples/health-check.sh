#!/bin/bash
# Script para verificar status e testar endpoints da API trancasDEE

BASE_URL="http://localhost:3000"

echo "=== Teste de Health Check - trancasDEE ==="
echo "Data: $(date)"
echo "URL: $BASE_URL"
echo

# Função para testar endpoint
test_endpoint() {
    local endpoint="$1"
    local description="$2"
    
    echo -n "Testando $description... "
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL$endpoint")
    
    if [ "$response" == "200" ]; then
        echo "✅ OK (HTTP $response)"
    else
        echo "❌ FALHA (HTTP $response)"
    fi
}

# Testar conectividade básica
echo "1. Verificando conectividade com a aplicação:"
if curl -s --connect-timeout 5 "$BASE_URL" > /dev/null 2>&1; then
    echo "✅ Aplicação respondendo"
else
    echo "❌ Aplicação não responde"
    echo "Verifique se a aplicação está rodando em $BASE_URL"
    exit 1
fi

echo
echo "2. Testando endpoints principais:"

# Testar endpoints principais
test_endpoint "/usuarios" "Usuários"
test_endpoint "/ambientes" "Ambientes"
test_endpoint "/acesso" "Acessos"
test_endpoint "/eventos" "Eventos"

echo
echo "3. Testando endpoints específicos (se dados existirem):"

# Testar endpoint específico com ID válido
test_endpoint "/usuarios/12345678901" "Usuário específico"
test_endpoint "/ambientes/1" "Ambiente específico"
test_endpoint "/acesso/1" "Acesso específico"

echo
echo "4. Contagem de registros:"

# Contar registros em cada endpoint
for endpoint in "/usuarios" "/ambientes" "/acesso" "/eventos"; do
    count=$(curl -s "$BASE_URL$endpoint" | jq '. | length' 2>/dev/null || echo "erro")
    echo "   $endpoint: $count registros"
done

echo
echo "=== Teste concluído ==="