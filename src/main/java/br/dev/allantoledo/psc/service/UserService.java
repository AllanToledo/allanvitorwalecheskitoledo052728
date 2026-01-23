package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.dto.UserCreationForm;
import br.dev.allantoledo.psc.dto.UserUpdateForm;
import br.dev.allantoledo.psc.entity.User;
import br.dev.allantoledo.psc.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;
    final PasswordEncoder encoder;
    final Validator validator;

    public User createUser(UserCreationForm userCreationForm) {
        User user = new User();
        user.setName(userCreationForm.getName());
        user.setEmail(userCreationForm.getEmail());
        user.setPassword(encoder.encode(userCreationForm.getPassword()));
        user.setIsAdmin(false);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Não é possível cadastrar usuário", violations);
        }

        return userRepository.save(user);
    }

    public User updateUser(UUID id, UserUpdateForm userUpdateForm) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setId(id);
        if(userUpdateForm.getName() != null) {
            user.setName(userUpdateForm.getName().orElse(null));
        }

        if(userUpdateForm.getEmail() != null) {
            user.setEmail(userUpdateForm.getEmail().orElse(null));
        }

        if(userUpdateForm.getPassword() != null) {
            if(userUpdateForm.getPassword().isPresent()) {
                user.setPassword(encoder.encode(userUpdateForm.getPassword().get()));
            } else {
                user.setPassword(null);
            }
        }

        if(userUpdateForm.getIsAdmin() != null) {
            user.setIsAdmin(userUpdateForm.getIsAdmin().orElse(null));
        }

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Não é possível atualizar usuário", violations);
        }

        return userRepository.save(user);
    }
}
