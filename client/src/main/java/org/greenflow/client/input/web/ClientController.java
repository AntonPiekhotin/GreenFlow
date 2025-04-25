package org.greenflow.client.input.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.greenflow.client.model.dto.ClientDto;
import org.greenflow.client.model.entity.Client;
import org.greenflow.client.service.ClientService;
import org.greenflow.common.model.constant.CustomHeaders;
import org.greenflow.common.model.dto.UserCreationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> clientInfoById(@RequestHeader(CustomHeaders.X_USER_ID) String id) {
        ClientDto client = clientService.getClientById(id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(client);
    }

    @PostMapping("/save")
    public boolean saveClient(@RequestBody UserCreationDto clientDto) {
        clientService.saveClient(clientDto);
        return true;
    }

    @PutMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> updateClient(@RequestHeader(CustomHeaders.X_USER_ID) String id,
                                          @RequestBody @Valid ClientDto clientDto) {
        ClientDto client = clientService.updateClient(id, clientDto);
        return ResponseEntity.ok(client);
    }

}
