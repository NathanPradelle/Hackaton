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

    // Récupérer tous les utilisateurs
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    // Créer ou mettre à jour un utilisateur
    public UserDto save(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    // Supprimer un utilisateur
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // Authentification (Login)
    public UserDto login(String identifier, String password) {
        // Astuce Hackathon : on filtre directement sur la liste pour éviter
        // d'avoir à déclarer un "findByIdentifier" dans le UserRepository.
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> u.getIdentifier() != null && u.getIdentifier().equals(identifier))
                .findFirst();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Comparaison en texte clair du mot de passe (Parfait pour un hackathon)
            if (user.getPassword() != null && user.getPassword().equals(password)) {
                return userMapper.toDto(user); // Succès
            }
        }
        
        return null; // Échec : Mauvais identifiant ou mot de passe
    }
}