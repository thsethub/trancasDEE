package br.dee.trancasdee.models.Usuarios;

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
@Table(name = "Usuarios", schema = "sys")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuarios {

    @Id
    @Column(name = "CPF", nullable = false)
    @JsonProperty("CPF")
    private Long cpf;

    @Column(name = "Nome", nullable = false)
    @JsonProperty("Nome")
    private String nome;

    @Setter
    @Column(name = "UniqueID", nullable = true)
    @JsonProperty("UniqueID")
    private Long uniqueID;

    @Column(name = "Acesso", nullable = false)
    @JsonProperty("Acesso")
    private Integer acesso;

    public void setUniqueID(Long uniqueID) {
        this.uniqueID = uniqueID;
    }

    public Long getCpf() {
        return cpf;
    }


}
