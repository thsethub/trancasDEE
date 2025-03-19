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
@Table(name = "Ambientes", schema = "sys")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Ambientes {

    @Id
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @JsonProperty("id")
    private Long id;

    @Column(name = "Sala", columnDefinition = "MEDIUMTEXT", nullable = false)
    @JsonProperty("Sala")
    private String sala;

    @Column(name = "Topico", columnDefinition = "MEDIUMTEXT", nullable = false)
    @JsonProperty("Topico")
    private String topico;

    public Long getId() {
        return id;
    }

    public String getSala() {
        return sala;
    }

}
