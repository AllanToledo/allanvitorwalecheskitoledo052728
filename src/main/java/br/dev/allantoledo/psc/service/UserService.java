package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.dto.user.UserCreationForm;
import br.dev.allantoledo.psc.dto.user.UserUpdateForm;
import br.dev.allantoledo.psc.entity.User;
import br.dev.allantoledo.psc.repository.UserRepository;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;
    final PasswordEncoder encoder;

    public User createUser(UserCreationForm userCreationForm) {
        User user = new User();
        user.setName(userCreationForm.getName());
        user.setEmail(userCreationForm.getEmail());
        user.setPassword(encoder.encode(userCreationForm.getPassword()));
        user.setIsAdmin(false);

        return userRepository.save(user);
    }

    public User updateUser(UUID id, UserUpdateForm userUpdateForm) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (userUpdateForm.getName() != null) {
            user.setName(userUpdateForm.getName().orElse(null));
        }

        if (userUpdateForm.getEmail() != null) {
            user.setEmail(userUpdateForm.getEmail().orElse(null));
        }

        if (userUpdateForm.getIsAdmin() != null) {
            user.setIsAdmin(userUpdateForm.getIsAdmin().orElse(null));
        }

        if (userUpdateForm.getPassword() != null) {
            if (userUpdateForm.getPassword().isPresent()) {
                user.setPassword(encoder.encode(userUpdateForm.getPassword().get()));
            } else {
                user.setPassword(null);
            }
        }

        return userRepository.save(user);
    }
}
