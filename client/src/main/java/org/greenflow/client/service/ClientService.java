package org.greenflow.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.client.model.dto.ClientCreationDto;
import org.greenflow.client.model.entity.Client;
import org.greenflow.client.output.persistent.ClientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;

    public Client getClientById(String id) {
        return clientRepository.findById(id).orElse(null);
    }

    public boolean saveClient(ClientCreationDto clientCreationDto) {
        if (clientRepository.existsByEmail(clientCreationDto.email())) {
            log.error("Email already in use in client service: {}", clientCreationDto.email());
            return false;
        }
        Client client = Client.builder()
                .id(clientCreationDto.id())
                .email(clientCreationDto.email())
                .build();
        log.info("Client {} saved", client.getEmail());
        clientRepository.save(client);
        return true;
    }
}
