package org.greenflow.client.service;

import lombok.RequiredArgsConstructor;
import org.greenflow.client.model.entity.Client;
import org.greenflow.client.output.persistent.ClientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client getClientById(String id) {
        return clientRepository.findById(id).orElse(null);
    }
}
