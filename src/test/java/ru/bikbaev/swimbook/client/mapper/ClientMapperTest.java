package ru.bikbaev.swimbook.client.mapper;

import org.junit.jupiter.api.Test;
import ru.bikbaev.swimbook.client.dto.ClientDto;
import ru.bikbaev.swimbook.client.dto.ClientRequest;
import ru.bikbaev.swimbook.client.model.Client;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientMapperTest {

    ClientMapper mapper = new ClientMapper();

    @Test
    void convertClientToDtoTest() {
        Client client = Client
                .builder()
                .id(10L)
                .name("Иван")
                .phone("+79870000000")
                .email("ivan@mail.ru")
                .build();

        ClientDto dto = mapper.convertClientToDto(client);

        assertNotNull(dto);

        assertEquals(dto.getId(), client.getId());
        assertEquals(dto.getName(), client.getName());
        assertEquals(dto.getPhone(), client.getPhone());
        assertEquals(dto.getEmail(), client.getEmail());
    }

    @Test
    void convertClientListToDtoTest() {

        List<Client> testClientList = Arrays.asList(
                Client.builder().id(10L).name("Ivan").phone("+79877777777").email("ivan@mail.ru").build(),
                Client.builder().id(20L).name("Tagir").phone("+79171111111").email("tagir@gmail.com").build()
        );

        List<ClientDto> testDtoList = mapper.convertClientListToDto(testClientList);

        assertNotNull(testDtoList);

        for (int i = 0; i < testClientList.size(); i++) {
            Client client = testClientList.get(i);
            ClientDto dto = testDtoList.get(i);
            assertEquals(client.getId(), dto.getId());
            assertEquals(client.getName(), dto.getName());
            assertEquals(client.getPhone(), dto.getPhone());
            assertEquals(client.getEmail(), dto.getEmail());
        }

    }

    @Test
    void convertClientRequestToClientTest() {
        ClientRequest request = new ClientRequest();
        request.setName("Ivan");
        request.setPhone("+77777777777");
        request.setEmail("ivan@mail.ru");

        Client client = mapper.convertClientRequestToClient(request);

        assertNotNull(client);

        assertEquals(client.getName(), request.getName());
        assertEquals(client.getPhone(), request.getPhone());
        assertEquals(client.getEmail(), request.getEmail());
    }

    @Test
    void updateClientTest() {
        Client client = Client
                .builder()
                .id(1L)
                .name("OldName")
                .phone("+71111111111")
                .email("oldemail@mail.ru")
                .build();

        ClientDto updateDto = ClientDto
                .builder()
                .id(1L)
                .name("NewName")
                .phone("+72222222222")
                .email("newemail@mail.ru")
                .build();

        mapper.updateClient(client, updateDto);

        assertEquals(client.getId(), updateDto.getId());
        assertEquals(client.getName(), updateDto.getName());
        assertEquals(client.getPhone(), updateDto.getPhone());
        assertEquals(client.getEmail(), updateDto.getEmail());

    }
}
