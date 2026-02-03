package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.dto.regionalendpoint.RegionalEndpointFromGeiaApi;
import br.dev.allantoledo.psc.entity.RegionalEndpoint;
import br.dev.allantoledo.psc.repository.RegionalEndpointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log
@Service
@RequiredArgsConstructor
public class RegionalEndpointService {
    private final RegionalEndpointRepository regionalEndpointRepository;

    @Scheduled(fixedRate = 1000 * 60 * 15) // 15 minutos
    public void updateRegionalEndpoints() {
        log.info("Atualizando endpoints...");
        List<RegionalEndpoint> endpoints = regionalEndpointRepository.findByParams(
                null, null, true);
        Map<Long, RegionalEndpoint> endpointMap = endpoints.stream()
                .collect(Collectors.toMap(RegionalEndpoint::getIdExtern, r -> r));

        List<RegionalEndpointFromGeiaApi> endpointsFromApi = fetchEndpointsFromApi();
        endpointsFromApi.forEach(api -> {
            RegionalEndpoint local = endpointMap.get(api.getId());
            if (!api.equalTo(local)) {
                log.info(String.format("Endpoint modificado: [%s] %s", api.getId(), api.getNome()));
                updateEndpoint(api);
            }
        });

        Map<Long, RegionalEndpointFromGeiaApi> endpointsFromApiMap = endpointsFromApi.stream()
                .collect(Collectors.toMap(RegionalEndpointFromGeiaApi::getId, r -> r));
        endpoints.forEach(local -> {
            if (!endpointsFromApiMap.containsKey(local.getIdExtern())) {
                log.info(String.format("Endpoint Ausente: [%s] %s", local.getIdExtern(), local.getName()));
                regionalEndpointRepository.deactivateAllByIdExtern(local.getIdExtern());
            }
        });
        log.info("Endpoints regionais atualizados!");
    }

    public RegionalEndpoint updateEndpoint(RegionalEndpointFromGeiaApi newEndpoint) {
        regionalEndpointRepository.deactivateAllByIdExtern(newEndpoint.getId());

        RegionalEndpoint updated = new RegionalEndpoint();
        updated.setActive(true);
        updated.setName(newEndpoint.getNome());
        updated.setIdExtern(newEndpoint.getId());

        return regionalEndpointRepository.save(updated);
    }

    public List<RegionalEndpointFromGeiaApi> fetchEndpointsFromApi() {
        RestClient client = RestClient.builder().baseUrl("https://integrador-argus-api.geia.vip").build();
        RegionalEndpointFromGeiaApi[] endpoints = client.get().uri("/v1/regionais")
                .retrieve().body(RegionalEndpointFromGeiaApi[].class);

        assert endpoints != null;
        return Arrays.stream(endpoints).toList();
    }
}
