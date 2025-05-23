package org.greenflow.client.service;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.client.model.dto.ClientDto;
import org.greenflow.client.model.entity.Client;
import org.greenflow.client.output.persistent.ClientRepository;
import org.greenflow.client.service.mapper.ClientMapper;
import org.greenflow.common.model.dto.UserCreationDto;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final RestTemplate restTemplate;

    @Value("${api.host.billing}")
    private String BILLING_SERVICE_HOST;

    private String BILLING_SERVICE_URL;

    @PostConstruct
    public void init() {
        BILLING_SERVICE_URL = "http://" + BILLING_SERVICE_HOST + "/api/v1/billing";
    }

    public ClientDto getClientById(String id) {
        return ClientMapper.INSTANCE.toDto(clientRepository.findById(id).orElse(null));
    }

    public boolean saveClient(UserCreationDto clientDto) {
        if (clientRepository.existsByEmail(clientDto.email())) {
            log.error("Email already in use in client service: {}", clientDto.email());
            return false;
        }
        Client client = Client.builder()
                .id(clientDto.id())
                .email(clientDto.email())
                .build();
        log.info("Client {} saved", client.getEmail());
        clientRepository.save(client);
        return true;
    }

    public ClientDto updateClient(@NotNull String id, @NotNull @Valid ClientDto clientDto) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            log.error("Client not found in client service: {}", id);
            throw new GreenFlowException(404, "Client not found for id: " + id);
        }
        client = ClientMapper.INSTANCE.toEntity(clientDto);
        client.setId(id);
        clientRepository.save(client);

        log.info("Client {} updated", client.getEmail());
        return ClientMapper.INSTANCE.toDto(client);
    }

    public BigDecimal getClientBalance(String userId) {
        try {
            BigDecimal response = restTemplate.getForObject(BILLING_SERVICE_URL + "/balance?userId=" + userId,
                    BigDecimal.class);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while getting client balance", e);
            throw new GreenFlowException(503, "Error occurred while getting client balance");
        }
    }
}
