package ru.bikbaev.swimbook.client.service;

import org.springframework.stereotype.Service;
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

@Service
public class ClientService {
    private final JpaClient clientRepository;
    private final ClientMapper clientMapper;

    public ClientService(JpaClient jpaClient, ClientMapper clientMapper) {
        this.clientRepository = jpaClient;
        this.clientMapper = clientMapper;
    }

    /**
     *  Получение всех клиентов
     * @return возвращаем в виде List список всех клиентов, сконвертированных в Dto
     */
    public List<ClientDto> getAllClient() {
        return clientMapper
                .convertClientListToDto(clientRepository.findAll());
    }

    /**
     * Получение клиента по id
     *
     * @param id клиента для поиска
     * @return если клиент найден возвращяем конвертированный в Dto клиент,
     * если же не существует клиента с таким id, выбрасываем исключение ClientNotFoundException
     */
    public ClientDto getClientById(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(
                () -> new ClientNotFoundException("Клиент с id " + id + " не найден")
        );
        return clientMapper.convertClientToDto(client);
    }


    /**
     * Создание нового клиента
     * Если в базе уже существует клиент с идентичным email или phone,
     * выбрасывается исключение ClientAlreadyExistException
     * @param clientRequest в метод передается новый клиент
     */
    public void createNewClient(ClientRequest clientRequest) {
        if (clientRepository.existsByEmailOrPhone(clientRequest.getEmail(), clientRequest.getPhone())) {
            throw new ClientAlreadyExistException("Клиент с таким телефоном или email уже существует");
        }
        Client client = clientMapper.convertClientRequestToClient(clientRequest);
        clientRepository.save(client);

    }

    /**
     * Обновление данных клиента
     * Проверяется, существует ли клиент с таким id, если клиента нет в базе данных,
     * выбрасывается исключение ClientNotFoundException
     * Если новый email или phone уже используется — выбрасываются соответствующие исключения.
     * @param clientDto обновляемые данные клиента
     */
    public void updateClient(ClientDto clientDto) {
        Client client = clientRepository.findById(clientDto.getId()).orElseThrow(
                () -> new ClientNotFoundException("Клиент " + clientDto + " не найден")
        );

        checkIfEmailExists(clientDto.getEmail(), client.getEmail());
        checkIfPhoneExists(clientDto.getPhone(), client.getPhone());

        clientMapper.updateClient(client, clientDto);
        clientRepository.save(client);
    }


    /**
     * Метод поска по id, для работы внутри сервисов
     * @param id клиента
     * @return возвращяет entity объект
     */
    public Client getClientEntityById(Long id){
       return clientRepository.findById(id).orElseThrow(
                () -> new ClientNotFoundException("Клиент с id " + id + " не найден"));
    }


    public Client findByName(String name) {
        return  clientRepository.findByName(name).orElseThrow(
                ()-> new ClientNotFoundException("Клиент с именем: "+name+" не найден")
        );
    }


    private void checkIfEmailExists(String email, String currentEmail) {
        if (!currentEmail.equals(email) && clientRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistException("Пользователь с " + email + " уже существует");
        }
    }

    private void checkIfPhoneExists(String phone, String currentPhone) {
        if (!currentPhone.equals(phone) && clientRepository.existsByPhone(phone)) {
            throw new PhoneAlreadyExistException("Пользователь с " + phone + " уже существует");
        }
    }
}
