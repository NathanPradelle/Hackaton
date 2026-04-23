package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.dto.AuthRequestDto;
import esgi.hackathon.wsd.dto.AuthResponseDto;
import esgi.hackathon.wsd.dto.AuthResponseDto.AuthUserDto;
import esgi.hackathon.wsd.dto.RegisterRequestDto;
import esgi.hackathon.wsd.entity.users.Client;
import esgi.hackathon.wsd.entity.users.User;
import esgi.hackathon.wsd.enums.UserRole;
import esgi.hackathon.wsd.repository.ClientRepository;
import esgi.hackathon.wsd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto req) {
        return userRepository.findByUsername(req.identifier())
            .filter(u -> req.password().equals(u.getPassword()))
            .map(user -> {
                Client client = clientRepository.findByUserId(user.getId()).orElse(null);
                AuthUserDto authUser = new AuthUserDto(
                    user.getId(),
                    user.getUsername(),
                    client != null ? client.getName() : null,
                    client != null ? client.getSiretNumber() : null,
                    client != null ? client.getCity() : null,
                    client != null ? client.getId() : null
                );
                return ResponseEntity.ok(new AuthResponseDto(String.valueOf(user.getId()), authUser));
            })
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto req) {
        if (userRepository.findByUsername(req.identifier()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = new User();
        user.setUsername(req.identifier());
        user.setPassword(req.password());
        user.setRole(UserRole.CLIENT);
        user = userRepository.save(user);

        Client client = new Client();
        client.setUser(user);
        client.setName(req.name());
        client.setSiretNumber(req.siret());
        client.setCity(req.city());
        client = clientRepository.save(client);

        AuthUserDto authUser = new AuthUserDto(
            user.getId(), user.getUsername(),
            client.getName(), client.getSiretNumber(), client.getCity(), client.getId()
        );
        return new ResponseEntity<>(new AuthResponseDto(String.valueOf(user.getId()), authUser), HttpStatus.CREATED);
    }
}
