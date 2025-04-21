package ru.bikbaev.swimbook.client.mapper;

import org.springframework.stereotype.Component;
import ru.bikbaev.swimbook.client.dto.ClientDto;
import ru.bikbaev.swimbook.client.dto.ClientRequest;
import ru.bikbaev.swimbook.client.model.Client;

import java.util.List;

@Component
public class ClientMapper {

    public ClientDto convertClientToDto(Client client){
        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .phone(client.getPhone())
                .email(client.getEmail())
                .build();
    }

    public List<ClientDto> convertClientListToDto(List<Client> clients){
        return clients
                .stream()
                .map(this::convertClientToDto)
                .toList();
    }

    public Client convertClientRequestToClient(ClientRequest request){
        return Client.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();
    }

    public void updateClient(Client client, ClientDto clientDto) {
        if (clientDto.getName() != null) {
            client.setName(clientDto.getName());
        }
        if (clientDto.getPhone() != null) {
            client.setPhone(clientDto.getPhone());
        }
        if (clientDto.getEmail() != null) {
            client.setEmail(clientDto.getEmail());
        }
    }
}
