package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.dto.UserDto;
import esgi.hackathon.wsd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Autorise Angular à communiquer avec le Back
public class UserController {

    @Autowired
    private UserService userService;

    // --- Gestion des Utilisateurs ---

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // --- Authentification ---
    // Cet endpoint correspond à ce que ton AuthService Angular appelle
    
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String identifier = credentials.get("identifier");
        String password = credentials.get("password");

        UserDto user = userService.login(identifier, password);
        
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).body("Identifiant ou mot de passe incorrect");
        }
    }
}