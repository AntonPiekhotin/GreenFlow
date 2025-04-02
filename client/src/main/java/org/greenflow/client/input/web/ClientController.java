package org.greenflow.client.input.web;

import lombok.RequiredArgsConstructor;
import org.greenflow.client.service.ClientService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

//    @GetMapping("/info/{id}")
//    public ResponseEntity<?> clientInfoById(@PathVariable String id) {
//        Client client = clientService.getClientById(id);
//        if (client == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(ClientDto.fromEntity(client));
//    }


    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/info")
    public String info() {
        return "Info";
    }


}
