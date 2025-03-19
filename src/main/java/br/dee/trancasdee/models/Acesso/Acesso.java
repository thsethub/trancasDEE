package br.dee.trancasdee.models.Acesso;

import br.dee.trancasdee.models.Ambientes;
import br.dee.trancasdee.models.Usuarios.Usuarios;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(name = "Acesso", schema = "sys")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Acesso {

    @Id
    @JsonProperty("id")
    private long id;
    @ManyToOne
    @JoinColumn(name = "Sala")
    private Ambientes ambientes;
    @ManyToOne
    @JoinColumn(name = "CPF")
    private Usuarios usuarios;
    @Column(name = "Data_Limite", nullable = false)
    @JoinColumn(name = "Data_Limite")
    private Instant dataLimite;
    @Column(name = "Hora_acesso_inicial", nullable = false)
    @JoinColumn(name = "Hora_acesso_inicial")
    private LocalTime horaAcessoInicial;
    @Column(name = "Hora_acesso_final", nullable = false)
    @JoinColumn(name = "Hora_acesso_final")
    private LocalTime horaAcessoFinal;


    public long getId() {
        return id;
    }

    public Ambientes getAmbientes() {
        return ambientes;
    }

    public Usuarios getUsuarios() {
        return usuarios;
    }

    public Instant getDataLimite() {
        return dataLimite;
    }

    public LocalTime getHoraAcessoInicial() {
        return horaAcessoInicial;
    }

    public LocalTime getHoraAcessoFinal() {
        return horaAcessoFinal;
    }

}
