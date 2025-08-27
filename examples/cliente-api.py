#!/usr/bin/env python3
"""
Cliente Python para a API trancasDEE
Demonstra como interagir com o sistema de controle de acesso
"""

import requests
import json
import sys
from datetime import datetime
from typing import Dict, List, Optional

class TrancasDEEClient:
    """Cliente para interagir com a API trancasDEE"""
    
    def __init__(self, base_url: str = "http://localhost:3000"):
        self.base_url = base_url.rstrip('/')
        
    def _get(self, endpoint: str) -> requests.Response:
        """Faz requisição GET para um endpoint"""
        url = f"{self.base_url}{endpoint}"
        return requests.get(url)
    
    def _put(self, endpoint: str, data: dict) -> requests.Response:
        """Faz requisição PUT para um endpoint"""
        url = f"{self.base_url}{endpoint}"
        headers = {'Content-Type': 'application/json'}
        return requests.put(url, json=data, headers=headers)
    
    def listar_usuarios(self) -> List[Dict]:
        """Lista todos os usuários"""
        response = self._get('/usuarios')
        if response.status_code == 200:
            return response.json()
        return []
    
    def buscar_usuario(self, cpf: str) -> Optional[Dict]:
        """Busca um usuário específico pelo CPF"""
        response = self._get(f'/usuarios/{cpf}')
        if response.status_code == 200:
            return response.json()
        return None
    
    def associar_cracha(self, cpf: str, unique_id: int) -> bool:
        """Associa um UniqueID (crachá) a um usuário"""
        data = {"UniqueID": unique_id}
        response = self._put(f'/usuarios/{cpf}', data)
        return response.status_code == 200
    
    def listar_ambientes(self) -> List[Dict]:
        """Lista todos os ambientes"""
        response = self._get('/ambientes')
        if response.status_code == 200:
            return response.json()
        return []
    
    def listar_acessos(self) -> List[Dict]:
        """Lista todos os registros de acesso"""
        response = self._get('/acesso')
        if response.status_code == 200:
            return response.json()
        return []
    
    def verificar_acesso_cracha(self, unique_id: int) -> List[Dict]:
        """Verifica todos os acessos de um crachá"""
        response = self._get(f'/acesso/uniqueID/{unique_id}')
        if response.status_code == 200:
            return response.json()
        return []
    
    def verificar_acesso_sala(self, unique_id: int, sala_id: int) -> List[Dict]:
        """Verifica acesso específico de um crachá a uma sala"""
        response = self._get(f'/acesso/sala/{sala_id}/uniqueID/{unique_id}')
        if response.status_code == 200:
            return response.json()
        return []
    
    def listar_eventos(self) -> List[Dict]:
        """Lista todos os eventos registrados"""
        response = self._get('/eventos')
        if response.status_code == 200:
            return response.json()
        return []
    
    def usuarios_sem_cracha(self) -> List[Dict]:
        """Lista usuários que não têm crachá associado"""
        usuarios = self.listar_usuarios()
        return [u for u in usuarios if u.get('unique_id') is None]
    
    def relatorio_uso_sistema(self) -> Dict:
        """Gera relatório de uso do sistema"""
        usuarios = self.listar_usuarios()
        ambientes = self.listar_ambientes()
        acessos = self.listar_acessos()
        eventos = self.listar_eventos()
        
        usuarios_com_cracha = [u for u in usuarios if u.get('unique_id') is not None]
        usuarios_sem_cracha = [u for u in usuarios if u.get('unique_id') is None]
        
        return {
            'total_usuarios': len(usuarios),
            'usuarios_com_cracha': len(usuarios_com_cracha),
            'usuarios_sem_cracha': len(usuarios_sem_cracha),
            'total_ambientes': len(ambientes),
            'total_acessos': len(acessos),
            'total_eventos': len(eventos),
            'timestamp': datetime.now().isoformat()
        }

def main():
    """Função principal com exemplos de uso"""
    
    # Criar cliente
    client = TrancasDEEClient()
    
    print("=== Cliente Python - trancasDEE ===")
    print(f"Conectando em: {client.base_url}")
    print()
    
    try:
        # Teste de conectividade
        usuarios = client.listar_usuarios()
        print(f"✅ Conexão estabelecida. {len(usuarios)} usuários encontrados.")
        
    except requests.exceptions.ConnectionError:
        print("❌ Erro: Não foi possível conectar à API")
        print("Verifique se a aplicação está rodando")
        return 1
    
    except Exception as e:
        print(f"❌ Erro inesperado: {e}")
        return 1
    
    # Relatório do sistema
    print("\n1. Relatório do Sistema:")
    relatorio = client.relatorio_uso_sistema()
    for chave, valor in relatorio.items():
        if chave != 'timestamp':
            print(f"   {chave.replace('_', ' ').title()}: {valor}")
    
    # Usuários sem crachá
    print("\n2. Usuários sem crachá associado:")
    usuarios_sem_cracha = client.usuarios_sem_cracha()
    if usuarios_sem_cracha:
        for usuario in usuarios_sem_cracha[:5]:  # Mostrar apenas os primeiros 5
            print(f"   - {usuario['nome']} (CPF: {usuario['cpf']})")
        if len(usuarios_sem_cracha) > 5:
            print(f"   ... e mais {len(usuarios_sem_cracha) - 5} usuários")
    else:
        print("   Todos os usuários têm crachá associado")
    
    # Ambientes disponíveis
    print("\n3. Ambientes disponíveis:")
    ambientes = client.listar_ambientes()
    for ambiente in ambientes[:5]:  # Mostrar apenas os primeiros 5
        print(f"   - {ambiente['sala']} (ID: {ambiente['id']}) - {ambiente['topico']}")
    if len(ambientes) > 5:
        print(f"   ... e mais {len(ambientes) - 5} ambientes")
    
    # Eventos recentes
    print("\n4. Eventos recentes:")
    eventos = client.listar_eventos()
    eventos_recentes = eventos[-5:] if eventos else []  # Últimos 5 eventos
    
    if eventos_recentes:
        for evento in eventos_recentes:
            print(f"   - {evento.get('evento', 'N/A')} em {evento.get('data_hora', 'N/A')} "
                  f"(UniqueID: {evento.get('unique_id', 'N/A')}, Ambiente: {evento.get('ambiente', 'N/A')})")
    else:
        print("   Nenhum evento registrado")
    
    # Exemplo de verificação de acesso
    if len(sys.argv) >= 3:
        unique_id = int(sys.argv[1])
        sala_id = int(sys.argv[2])
        
        print(f"\n5. Verificação de acesso (UniqueID: {unique_id}, Sala: {sala_id}):")
        acesso = client.verificar_acesso_sala(unique_id, sala_id)
        
        if acesso:
            print("   ✅ ACESSO AUTORIZADO")
            for a in acesso:
                print(f"   - Ambiente: {a['nome_ambiente']}")
                print(f"   - Horário: {a['hora_acesso_inicial']} - {a['hora_acesso_final']}")
                print(f"   - Válido até: {a['data_limite']}")
        else:
            print("   ❌ ACESSO NEGADO")
            # Verificar se o crachá tem outros acessos
            outros_acessos = client.verificar_acesso_cracha(unique_id)
            if outros_acessos:
                print(f"   Este crachá tem acesso a {len(outros_acessos)} outros ambientes")
    
    print("\n=== Execução concluída ===")
    return 0

if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] in ['-h', '--help']:
        print("Uso: python cliente-api.py [unique_id] [sala_id]")
        print("  unique_id: ID único do crachá para verificar acesso")
        print("  sala_id: ID da sala para verificar acesso")
        print("\nExemplo: python cliente-api.py 987654321 1")
        sys.exit(0)
    
    sys.exit(main())