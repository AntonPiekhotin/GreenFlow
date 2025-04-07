package org.greenflow.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.client.model.entity.Client;
import org.greenflow.client.output.persistent.ClientRepository;
import org.greenflow.common.model.dto.UserCreationDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;

    public Client getClientById(String id) {
        return clientRepository.findById(id).orElse(null);
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
}
