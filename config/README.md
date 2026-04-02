# Configurações do trancasDEE

Este diretório contém arquivos de configuração para diferentes ambientes e cenários de deploy.

## Arquivos de Configuração

### application.properties.example
Template principal de configuração da aplicação Spring Boot.

### docker/
Configurações específicas para deploy com Docker.

### production/
Configurações otimizadas para ambiente de produção.

### development/
Configurações para ambiente de desenvolvimento local.

## Como Usar

1. **Para desenvolvimento local:**
```bash
cp application.properties.example src/main/resources/application.properties
# Editar as configurações conforme necessário
```

2. **Para Docker:**
```bash
cp docker/application-docker.properties src/main/resources/
# Usar profile 'docker' ao executar
```

3. **Para produção:**
```bash
cp production/application-production.properties src/main/resources/
# Configurar variáveis de ambiente apropriadas
```

## Variáveis de Ambiente

As principais variáveis que podem ser configuradas:

- `SPRING_DATASOURCE_URL` - URL do banco de dados
- `SPRING_DATASOURCE_USERNAME` - Usuário do banco
- `SPRING_DATASOURCE_PASSWORD` - Senha do banco
- `SERVER_PORT` - Porta do servidor (padrão: 3000)
- `SPRING_PROFILES_ACTIVE` - Profile ativo (development, docker, production)

## Perfis de Configuração

### development
- Logs verbosos
- DDL automático habilitado
- Configurações de debug

### docker
- Configurações para containers
- URLs internas do Docker
- Logs moderados

### production
- Logs mínimos
- Configurações de segurança
- Otimizações de performance