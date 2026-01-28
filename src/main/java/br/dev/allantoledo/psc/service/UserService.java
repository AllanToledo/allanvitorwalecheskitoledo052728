package br.dev.allantoledo.psc.service;

import br.dev.allantoledo.psc.dto.user.SecureUserUpdateForm;
import br.dev.allantoledo.psc.dto.user.UserCreationForm;
import br.dev.allantoledo.psc.dto.user.UserUpdateAdminForm;
import br.dev.allantoledo.psc.entity.User;
import br.dev.allantoledo.psc.repository.UserRepository;
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

    public User adminUpdateUser(UUID id, UserUpdateAdminForm userUpdateAdminForm) {
        User user = getUserById(id);

        if (userUpdateAdminForm.getName() != null) {
            user.setName(userUpdateAdminForm.getName().orElse(null));
        }

        if (userUpdateAdminForm.getEmail() != null) {
            user.setEmail(userUpdateAdminForm.getEmail().orElse(null));
        }

        if (userUpdateAdminForm.getIsAdmin() != null) {
            user.setIsAdmin(userUpdateAdminForm.getIsAdmin().orElse(null));
        }

        if (userUpdateAdminForm.getPassword() != null) {
            if (userUpdateAdminForm.getPassword().isPresent()) {
                user.setPassword(encoder.encode(userUpdateAdminForm.getPassword().get()));
            } else {
                user.setPassword(null);
            }
        }

        return userRepository.save(user);
    }

    public User secureUpdateUser(UUID id, SecureUserUpdateForm secureUserUpdateForm) {
        User user = getUserById(id);

        if (secureUserUpdateForm.getName() != null) {
            user.setName(secureUserUpdateForm.getName().orElse(null));
        }

        if (secureUserUpdateForm.getEmail() != null) {
            user.setEmail(secureUserUpdateForm.getEmail().orElse(null));
        }

        if (secureUserUpdateForm.getPassword() != null) {
            if (secureUserUpdateForm.getPassword().isPresent()) {
                user.setPassword(encoder.encode(secureUserUpdateForm.getPassword().get()));
            } else {
                user.setPassword(null);
            }
        }

        return userRepository.save(user);
    }
}
