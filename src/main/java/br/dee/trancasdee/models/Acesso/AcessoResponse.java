package br.dee.trancasdee.models.Acesso;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalTime;

public record AcessoResponse (
        Long id,
        String nomeAmbiente,
        long cpfUsuario,
        String nomeUsuario,
        long uniqueID,
        Instant dataLimite,
        LocalTime horaAcessoInicial,
        LocalTime horaAcessoFinal
){
    public AcessoResponse(Acesso acesso){
        this(
                acesso.getId(),
                acesso.getAmbientes() != null ? acesso.getAmbientes().getSala() : "Desconhecido",
                acesso.getUsuarios() != null ? acesso.getUsuarios().getCpf() : 0L,
                acesso.getUsuarios() != null ? acesso.getUsuarios().getNome() : "Desconhecido",
                acesso.getUsuarios() != null && acesso.getUsuarios().getUniqueID() != null
                        ? acesso.getUsuarios().getUniqueID() : 0L,
                acesso.getDataLimite(),
                acesso.getHoraAcessoInicial(),
                acesso.getHoraAcessoFinal()
        );
    }
}
