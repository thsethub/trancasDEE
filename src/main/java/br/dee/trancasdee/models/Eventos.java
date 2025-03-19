package br.dee.trancasdee.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "Eventos", schema = "sys")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Eventos {

    @Id
    @Column(name = "id")
    @JsonProperty("id")
    private Long id;

    @Column(name = "Evento")
    @JsonProperty("Evento")
    private String evento;

    @Column(name = "DataHora")
    @JsonProperty("DataHora")
    private String dataHora;

    @Column(name = "UniqueID", columnDefinition = "BIGINT")
    @JsonProperty("UniqueID")
    private Long uniqueID;

    @Column(name = "Ambiente", columnDefinition = "BIGINT")
    @JsonProperty("Ambiente")
    private Long ambiente;
}
