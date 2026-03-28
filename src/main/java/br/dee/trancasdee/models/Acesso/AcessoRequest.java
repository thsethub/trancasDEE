package br.dee.trancasdee.models.Acesso;

public record AcessoRequest(
        Long salaId,
        Long cpf,
        String dataLimite,
        String horaAcessoInicial,
        String horaAcessoFinal
) {
}
