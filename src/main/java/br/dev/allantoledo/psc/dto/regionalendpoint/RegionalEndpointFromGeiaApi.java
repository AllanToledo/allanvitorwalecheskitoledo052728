package br.dev.allantoledo.psc.dto.regionalendpoint;

import br.dev.allantoledo.psc.entity.RegionalEndpoint;
import lombok.Data;

import java.util.Objects;

@Data
public class RegionalEndpointFromGeiaApi {
    private Long id;
    private String nome;

    public boolean equalTo(RegionalEndpoint endpoint) {
        if(endpoint == null) return false;
        if(!Objects.equals(this.id, endpoint.getIdExtern())) return false;
        if(!Objects.equals(this.nome, endpoint.getName()))   return false;

        //eu sei, dava para simplificar, mas acredito que a lógica é mais explícita assim.
        return true;
    }
}
