package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.dto.NewUser;
import br.dev.allantoledo.psc.dto.UpdatedUser;
import br.dev.allantoledo.psc.entity.User;
import br.dev.allantoledo.psc.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;
    final PasswordEncoder encoder;
    final Validator validator;

    public User createUser(NewUser newUser) {
        User user = new User();
        user.setName(newUser.getName());
        user.setEmail(newUser.getEmail());
        user.setPassword(encoder.encode(newUser.getPassword()));
        user.setIsAdmin(false);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Não é possível cadastrar usuário", violations);
        }

        return userRepository.save(user);
    }

    public User updateUser(UUID id, UpdatedUser updatedUser) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setId(id);
        if(updatedUser.getName() != null) {
            user.setName(updatedUser.getName().orElse(null));
        }

        if(updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail().orElse(null));
        }

        if(updatedUser.getPassword() != null) {
            if(updatedUser.getPassword().isPresent()) {
                user.setPassword(encoder.encode(updatedUser.getPassword().get()));
            } else {
                user.setPassword(null);
            }
        }

        if(updatedUser.getIsAdmin() != null) {
            user.setIsAdmin(updatedUser.getIsAdmin().orElse(null));
        }

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Não é possível atualizar usuário", violations);
        }

        return userRepository.save(user);
    }
}
