package org.greenflow.client.input.web;

import lombok.RequiredArgsConstructor;
import org.greenflow.client.model.dto.ClientCreationDto;
import org.greenflow.client.model.dto.ClientDto;
import org.greenflow.client.model.entity.Client;
import org.greenflow.client.service.ClientService;
import org.greenflow.common.model.constant.CustomHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/info")
    public ResponseEntity<?> clientInfoById(@RequestHeader(CustomHeaders.X_USER_ID) String id) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ClientDto.fromEntity(client));
    }

    @PostMapping("/save")
    public boolean saveClient(@RequestBody ClientCreationDto clientCreationDto) {
        clientService.saveClient(clientCreationDto);
        return true;
    }

}
