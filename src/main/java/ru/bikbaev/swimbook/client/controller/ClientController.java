package ru.bikbaev.swimbook.client.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.bikbaev.swimbook.client.dto.ClientDto;
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

@RestController
@RequestMapping("/api/v0/pool/client")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClientDto>> getClients() {
        List<ClientDto> clients = clientService.getAllClient();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/get")
    public ResponseEntity<ClientDto> getClient(@RequestParam Long id) {
        ClientDto client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addClient(@RequestBody @Valid ClientRequest clientRequest) {
        clientService.createNewClient(clientRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateClient(@RequestBody @Valid ClientDto clientDto) {
        clientService.updateClient(clientDto);
        return new ResponseEntity<>(HttpStatus.OK);
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
