package ru.bikbaev.swimbook.client.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.bikbaev.swimbook.client.dto.ClientDto;
import ru.bikbaev.swimbook.client.dto.ClientIdAndNameResponse;
import ru.bikbaev.swimbook.client.dto.ClientRequest;
import ru.bikbaev.swimbook.client.exception.ClientAlreadyExistException;
import ru.bikbaev.swimbook.client.exception.ClientNotFoundException;
import ru.bikbaev.swimbook.client.exception.EmailAlreadyExistException;
import ru.bikbaev.swimbook.client.exception.PhoneAlreadyExistException;
import ru.bikbaev.swimbook.client.service.ClientService;
import ru.bikbaev.swimbook.common.exception.ErrorMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Tag(name = "API для работы с клиентами")
@RestController
@RequestMapping("/api/v0/pool/client")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Получение списка клиентов бассейна")
    @GetMapping("/all")
    public ResponseEntity<List<ClientIdAndNameResponse>> getClients() {
        return ResponseEntity.status(HttpStatus.OK).body(clientService.getAllClient());
    }

    @Operation(summary = "Получение данных о клиенте ")
    @GetMapping("/get")
    public ResponseEntity<ClientDto> getClient(@RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(clientService.getClientById(id));
    }

    @Operation(summary = "Добавление нового клиента")
    @PostMapping("/add")
    public ResponseEntity<Void> addClient(@RequestBody @Valid ClientRequest clientRequest) {
        clientService.createNewClient(clientRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Обновление данных о клиенте")
    @PostMapping("/update")
    public ResponseEntity<Void> updateClient(@RequestBody @Valid ClientDto clientDto) {
        clientService.updateClient(clientDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ExceptionHandler(ClientAlreadyExistException.class)
    public ResponseEntity<ErrorMessage> handlerClientAlreadyExist(ClientAlreadyExistException exception){
        ErrorMessage message = new ErrorMessage(exception.getMessage(),HttpStatus.CONFLICT);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(message);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorMessage> handlerClientNotFound(ClientNotFoundException exception){
        ErrorMessage message = new ErrorMessage(exception.getMessage(),HttpStatus.NOT_FOUND);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(message);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorMessage> handlerEmailAlreadyExist(EmailAlreadyExistException exception){
        ErrorMessage message = new ErrorMessage(exception.getMessage(),HttpStatus.CONFLICT);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(message);
    }

    @ExceptionHandler(PhoneAlreadyExistException.class)
    public ResponseEntity<ErrorMessage> handlerPhoneAlreadyExist(PhoneAlreadyExistException exception){
        ErrorMessage message = new ErrorMessage(exception.getMessage(),HttpStatus.CONFLICT);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception
                .getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }





}
