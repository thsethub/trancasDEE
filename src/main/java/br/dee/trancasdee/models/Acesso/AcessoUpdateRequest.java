package br.dee.trancasdee.models.Acesso;

public record AcessoUpdateRequest(
        String dataLimite,
        String horaAcessoInicial,
        String horaAcessoFinal
) {}
