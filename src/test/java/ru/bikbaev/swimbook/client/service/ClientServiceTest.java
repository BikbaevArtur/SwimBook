package ru.bikbaev.swimbook.client.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bikbaev.swimbook.client.dto.ClientDto;
import ru.bikbaev.swimbook.client.dto.ClientRequest;
import ru.bikbaev.swimbook.client.exception.ClientAlreadyExistException;
import ru.bikbaev.swimbook.client.exception.ClientNotFoundException;
import ru.bikbaev.swimbook.client.exception.EmailAlreadyExistException;
import ru.bikbaev.swimbook.client.exception.PhoneAlreadyExistException;
import ru.bikbaev.swimbook.client.mapper.ClientMapper;
import ru.bikbaev.swimbook.client.model.Client;
import ru.bikbaev.swimbook.client.repository.JpaClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    private JpaClient jpa;
    @Mock
    private ClientMapper mapper;
    @InjectMocks
    private ClientService service;

    @Test
    void getAllClientTest() {
        Client client1 = Client.builder().id(1L).name("Ivan").phone("+79871234567").email("ivan@mail.ru").build();
        Client client2 = Client.builder().id(2L).name("Timur").phone("+79371111111").email("oleg@mail.ru").build();
        List<Client> clients = List.of(client1, client2);

        ClientDto dto1 = ClientDto.builder().id(1L).name("Ivan").phone("+79871234567").email("ivan@mail.ru").build();
        ClientDto dto2 = ClientDto.builder().id(2L).name("Timur").phone("+79371111111").email("oleg@mail.ru").build();
        List<ClientDto> dtos = List.of(dto1, dto2);

        when(jpa.findAll()).thenReturn(clients);
        when(mapper.convertClientListToDto(clients)).thenReturn(dtos);

        List<ClientDto> result = service.getAllClient();

        assertNotNull(result);

        verify(jpa, times(1)).findAll();
        verify(mapper, times(1)).convertClientListToDto(clients);
    }


    @Test
    void getClientByIdTest() {
        Client client = createTestClient();
        ClientDto clientDto = createTestClientDto();

        when(jpa.findById(1L)).thenReturn(Optional.of(client));
        when(mapper.convertClientToDto(client)).thenReturn(clientDto);

        ClientDto result = service.getClientById(1L);

        assertNotNull(result);
        assertEquals(clientDto.getId(), result.getId());
        assertEquals(clientDto.getName(), result.getName());
        assertEquals(clientDto.getPhone(), result.getPhone());
        assertEquals(clientDto.getEmail(), result.getEmail());

        verify(jpa, times(1)).findById(1L);
        verify(mapper, times(1)).convertClientToDto(client);

    }

    @Test
    void getClientByIdThrowClientNotFoundExceptionTest() {
        when(jpa.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> service.getClientById(1L));

        verify(jpa, times(1)).findById(1L);
    }

    @Test
    void createNewClientTest() {
        ClientRequest request = new ClientRequest();
        request.setName("Ivan");
        request.setPhone("+70000000000");
        request.setEmail("ivan@mail.ru");

        Client client = createTestClient();


        when(jpa.existsByEmailOrPhone(request.getEmail(), request.getPhone())).thenReturn(false);
        when(mapper.convertClientRequestToClient(request)).thenReturn(client);
        when(jpa.save(client)).thenReturn(client);

        service.createNewClient(request);

        verify(jpa, times(1)).existsByEmailOrPhone(request.getEmail(), request.getPhone());
        verify(mapper, times(1)).convertClientRequestToClient(request);
        verify(jpa, times(1)).save(client);
    }

    @Test
    void createNewClientThrowClientAlreadyExceptionTest() {

        ClientRequest request = new ClientRequest();
        request.setName("Ivan");
        request.setPhone("+70000000000");
        request.setEmail("ivan@mail.ru");

        when(jpa.existsByEmailOrPhone(request.getEmail(), request.getPhone())).thenReturn(true);


        assertThrows(ClientAlreadyExistException.class, () -> service.createNewClient(request));

        verify(jpa, times(1)).existsByEmailOrPhone(request.getEmail(), request.getPhone());
        verify(mapper, never()).convertClientRequestToClient(any());
        verify(jpa, never()).save(any());
    }

    @Test
    void updateClientTest() {

        ClientDto dto = ClientDto.builder()
                .id(1L)
                .name("NewName")
                .email("new@email.com")
                .phone("+79999999999")
                .build();

        Client client = createTestClient();

        when(jpa.findById(1L)).thenReturn(Optional.of(client));
        when(jpa.existsByEmail(dto.getEmail())).thenReturn(false);
        when(jpa.existsByPhone(dto.getPhone())).thenReturn(false);


        service.updateClient(dto);


        verify(jpa, times(1)).findById(1L);
        verify(jpa, times(1)).existsByEmail(dto.getEmail());
        verify(jpa, times(1)).existsByPhone(dto.getPhone());
        verify(mapper, times(1)).updateClient(client, dto);
        verify(jpa, times(1)).save(client);
    }

    @Test
    void updateClientThrowClientNotFoundExceptionTest() {
        ClientDto dto = createTestClientDto();

        when(jpa.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> service.updateClient(dto));

        verify(jpa, times(1)).findById(1L);
        verify(mapper, never()).updateClient(any(), any());
        verify(jpa, never()).save(any());
    }

    @Test
    void updateClientThrowEmailAlreadyExistExceptionTest() {

        ClientDto dto = ClientDto.builder()
                .id(1L)
                .name("NewName")
                .email("new@email.com")
                .phone("+79999999999")
                .build();

        Client client = createTestClient();

        when(jpa.findById(1L)).thenReturn(Optional.of(client));
        when(jpa.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistException.class, () -> service.updateClient(dto));

        verify(jpa, times(1)).findById(1L);
        verify(jpa, times(1)).existsByEmail(dto.getEmail());
        verify(mapper, never()).updateClient(any(), any());
        verify(jpa, never()).save(any());
    }

    @Test
    void updateClientThrowPhoneAlreadyExistException() {
        ClientDto dto = ClientDto.builder()
                .id(1L)
                .name("NewName")
                .email("new@email.com")
                .phone("+79999999999")
                .build();

        Client client = createTestClient();

        when(jpa.findById(1L)).thenReturn(Optional.of(client));
        when(jpa.existsByPhone(dto.getPhone())).thenReturn(true);

        assertThrows(PhoneAlreadyExistException.class, () -> service.updateClient(dto));

        verify(jpa, times(1)).findById(1L);
        verify(jpa, times(1)).existsByPhone(dto.getPhone());
        verify(mapper, never()).updateClient(any(), any());
        verify(jpa, never()).save(any());
    }

    @Test
    void getClientEntityByIdTest(){
        Long id = 1L;
        Client client = createTestClient();

        when(jpa.findById(id)).thenReturn(Optional.of(client));

        Client result = service.getClientEntityById(id);

        assertNotNull(result);

        verify(jpa,times(1)).findById(id);
    }

    @Test
    void getClientEntityByIdThrowClientNotFoundException(){
        Long id = 1L;
        when(jpa.findById(id)).thenReturn(Optional.empty());
        assertThrows(ClientNotFoundException.class,()->service.getClientEntityById(id));
        verify(jpa,times(1)).findById(id);
    }

    @Test
    void findByNameTest(){
        String name = "Ivan";
        Client client = createTestClient();
        when(jpa.findByName(name)).thenReturn(Optional.of(client));

        Client result = service.findByName(name);

        assertNotNull(result);
        assertEquals(result,client);

        verify(jpa,times(1)).findByName(name);
    }

    @Test
    void findByNameTestThrowClientNotFoundException(){
        String name = "Ivan";

        when(jpa.findByName(name)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class,()->service.findByName(name));

        verify(jpa,times(1)).findByName(name);
    }

    private Client createTestClient() {
        return Client
                .builder()
                .id(1L)
                .name("Ivan")
                .phone("+70000000000")
                .email("ivan@mail.ru")
                .build();
    }

    private ClientDto createTestClientDto() {
        return ClientDto
                .builder()
                .id(1L)
                .name("Ivan")
                .phone("+70000000000")
                .email("ivan@mail.ru")
                .build();
    }
}
