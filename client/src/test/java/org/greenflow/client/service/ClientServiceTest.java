package org.greenflow.client.service;

import org.greenflow.client.model.dto.ClientDto;
import org.greenflow.client.model.entity.Client;
import org.greenflow.client.output.persistent.ClientRepository;
import org.greenflow.common.model.dto.UserCreationDto;
import org.greenflow.common.model.exception.GreenFlowException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientService = new ClientService(clientRepository, restTemplate);
    }

    @Test
    void getClientById_shouldReturnClientDto_whenClientExists() {
        Client client = Client.builder().id("1").email("test@example.com").build();
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));

        ClientDto result = clientService.getClientById("1");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getClientById_shouldReturnNull_whenClientDoesNotExist() {
        when(clientRepository.findById("1")).thenReturn(Optional.empty());

        ClientDto result = clientService.getClientById("1");

        assertNull(result);
    }

    @Test
    void saveClient_shouldReturnTrue_whenEmailIsNotInUse() {
        UserCreationDto userCreationDto = UserCreationDto.builder().id("1").email("test@example.com").build();
        when(clientRepository.existsByEmail("test@example.com")).thenReturn(false);

        boolean result = clientService.saveClient(userCreationDto);

        assertTrue(result);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void saveClient_shouldReturnFalse_whenEmailIsAlreadyInUse() {
        UserCreationDto userCreationDto = UserCreationDto.builder().id("1").email("test@example.com").build();
        when(clientRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = clientService.saveClient(userCreationDto);

        assertFalse(result);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void updateClient_shouldUpdateAndReturnClientDto_whenClientExists() {
        Client client = Client.builder().id("1").email("old@example.com").build();
        ClientDto clientDto = ClientDto.builder().email("new@example.com").build();
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));

        ClientDto result = clientService.updateClient("1", clientDto);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void updateClient_shouldThrowException_whenClientDoesNotExist() {
        ClientDto clientDto = ClientDto.builder().email("new@example.com").build();
        when(clientRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(GreenFlowException.class, () -> clientService.updateClient("1", clientDto));
    }

    @Test
    void getClientBalance_shouldReturnBalance_whenBillingServiceResponds() {
        when(restTemplate.getForObject(anyString(), eq(BigDecimal.class))).thenReturn(BigDecimal.TEN);

        BigDecimal result = clientService.getClientBalance("1");

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result);
    }

    @Test
    void getClientBalance_shouldThrowException_whenBillingServiceFails() {
        when(restTemplate.getForObject(anyString(), eq(BigDecimal.class))).thenThrow(new RuntimeException());

        assertThrows(GreenFlowException.class, () -> clientService.getClientBalance("1"));
    }
}
