package br.dee.trancasdee.models.Acesso;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalTime;

public record AcessoResponse (
//        @JsonProperty("id")
        long id,
//        @JsonProperty("id")
        String nomeAmbiente,
//        @JsonProperty("CPF")
        long cpfUsuario,
        long uniqueID,
//        @JsonProperty("Data_Limite")
        Instant dataLimite,
//        @JsonProperty("Hora_acesso_inicial")
        LocalTime horaAcessoInicial,
//        @JsonProperty("Hora_acesso_final")
        LocalTime horaAcessoFinal
){
    public AcessoResponse(Acesso acesso){
        this(
                acesso.getId(),
                acesso.getAmbientes().getSala(),
                acesso.getUsuarios().getCpf(),
                acesso.getUsuarios().getUniqueID(),
                acesso.getDataLimite(),
                acesso.getHoraAcessoInicial(),
                acesso.getHoraAcessoFinal()
        );
    }


}
