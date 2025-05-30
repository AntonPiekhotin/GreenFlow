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

/**
 * Service responsible for managing client-related operations in the GreenFlow system.
 * Handles client creation, retrieval, update, and balance queries by interacting with the client repository
 * and external billing service.
 */
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

    /**
     * Retrieves a client by their unique identifier.
     *
     * @param id the unique identifier of the client
     * @return the ClientDto representation of the client, or null if not found
     */
    public ClientDto getClientById(String id) {
        return ClientMapper.INSTANCE.toDto(clientRepository.findById(id).orElse(null));
    }

    /**
     * Saves a new client if the email is not already in use.
     *
     * @param clientDto the data transfer object containing client information
     * @return true if the client was saved successfully, false if the email is already in use
     */
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

    /**
     * Updates an existing client with new information.
     *
     * @param id the unique identifier of the client to update
     * @param clientDto the new client data
     * @return the updated ClientDto
     * @throws GreenFlowException if the client is not found
     */
    public ClientDto updateClient(@NotNull String id, @NotNull @Valid ClientDto clientDto) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            log.error("Client not found in client service: {}", id);
            throw new GreenFlowException(400, "Client not found for id: " + id);
        }
        client = ClientMapper.INSTANCE.toEntity(clientDto);
        client.setId(id);
        clientRepository.save(client);

        log.info("Client {} updated", client.getEmail());
        return ClientMapper.INSTANCE.toDto(client);
    }

    /**
     * Retrieves the balance for a client by querying the billing service.
     *
     * @param userId the unique identifier of the client
     * @return the balance as a BigDecimal
     * @throws GreenFlowException if there is an error communicating with the billing service
     */
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
