package br.dev.allantoledo.psc.dto.regionalendpoint;

import br.dev.allantoledo.psc.entity.RegionalEndpoint;
import lombok.Data;

import java.util.UUID;

@Data
public class RegionalEndpointInformation {
    private UUID id;
    private Long idExtern;
    private String name;
    private Boolean active;

    public static RegionalEndpointInformation fromRegionalEndpoint(RegionalEndpoint re) {
        RegionalEndpointInformation reInformation = new RegionalEndpointInformation();
        reInformation.setId(re.getId());
        reInformation.setIdExtern(re.getIdExtern());
        reInformation.setName(re.getName());
        reInformation.setActive(re.getActive());

        return reInformation;
    }
}
