package org.greenflow.client.input.web;

import lombok.RequiredArgsConstructor;
import org.greenflow.client.model.dto.ClientDto;
import org.greenflow.client.model.entity.Client;
import org.greenflow.client.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/info")
    public ResponseEntity<?> clientInfoById(@RequestHeader("X-User-Id") String id) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ClientDto.fromEntity(client));
    }

}
