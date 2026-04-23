package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.dto.UserDto;
import esgi.hackathon.wsd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.update(id, userDto));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
