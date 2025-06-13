package com.thespace.spaceweb.service;

import com.thespace.spaceweb.domain.User;
import com.thespace.spaceweb.domain.UserRole;
import com.thespace.spaceweb.dto.UserDTOs;
import com.thespace.spaceweb.exception.UserNotFound;
import com.thespace.spaceweb.repository.UserRepository;
import com.thespace.spaceweb.repository.UserRoleRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Long> findUserRoles(String userId) {
        return userRepository.findRoleIdsByUserId(userId);
    }

    public User findUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid).orElseThrow(UserNotFound::new);
    }

    public Set<User> getUsersByUuid(List<String> dto) {
        return dto
            .stream()
            .map(uuid -> userRepository.findByUuid(uuid).orElseThrow(UserNotFound::new))
            .collect(Collectors.toSet());
    }

    @Transactional
    public UserDTOs.Info getUserinfoDTO(Authentication authentication) {
        if (authentication == null) {
            throw new UserNotFound();
        }

        User user = (User) authentication.getPrincipal();
        User data = userRepository.findByIdWithRoles(user.getId()).orElseThrow(UserNotFound::new);

        List<String> roles = data.getRoles().stream()
            .map(UserRole::getRole)
            .collect(Collectors.toList());

        return UserDTOs.Info.builder()
            .name(user.getName())
            .uuid(user.getUuid())
            .roles(roles)
            .build();
    }

    @Transactional
    public void register(UserDTOs.Register userRegisterDTO, boolean check) {
        if (!check) {
            return;
        }

        String password = passwordEncoder.encode(userRegisterDTO.password());
        String uuid;
        do {
            uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
        } while (!checkUuid(uuid));

        User user = User.builder()
            .id(userRegisterDTO.id())
            .uuid(uuid)
            .name(userRegisterDTO.name())
            .email(userRegisterDTO.email())
            .password(password)
            .build();

        userRepository.save(user);
        setRole(user.getId(), "ROLE_USER");
    }

    public boolean checkUuid(String uuid) {
        return !userRepository.existsByUuid(uuid);
    }

    public boolean checkId(String id) {
        return !userRepository.existsById(id);
    }

    @Transactional
    public void setRole(String id, String role) {
        UserRole userRole = userRoleRepository.findByRole(role).orElseThrow();
        User user = userRepository.findById(id).orElseThrow(UserNotFound::new);
        user.getRoles().add(userRole);
        userRepository.save(user);
    }
} //Implement modify User data later
