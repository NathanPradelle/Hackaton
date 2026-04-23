package esgi.hackathon.wsd.service;

import esgi.hackathon.wsd.dto.UserDto;
import esgi.hackathon.wsd.entity.users.User;
import esgi.hackathon.wsd.mapper.UserMapper;
import esgi.hackathon.wsd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto save(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + id));
        user.setUsername(userDto.username());
        if (userDto.role() != null) user.setRole(userDto.role());
        return userMapper.toDto(userRepository.save(user));
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public UserDto login(String identifier, String password) {
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> u.getIdentifier() != null && u.getIdentifier().equals(identifier))
                .findFirst();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword() != null && user.getPassword().equals(password)) {
                return userMapper.toDto(user);
            }
        }

        return null;
    }
}
