package com.thespace.thespace.service;

import com.thespace.thespace.domain.User;
import com.thespace.thespace.domain.UserRole;
import com.thespace.thespace.dto.user.UserInfoDTO;
import com.thespace.thespace.dto.user.UserRegisterDTO;
import com.thespace.thespace.exception.UserNotFound;
import com.thespace.thespace.repository.UserRepository;
import com.thespace.thespace.repository.UserRoleRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public List<Long> findUserRoles(String userId) {
        return userRepository.findRoleIdsByUserId(userId);
    }

    @Transactional
    public UserInfoDTO getUserinfoDTO(Authentication authentication) {
        if (authentication == null) {
            throw new UserNotFound();
        }

        User user = (User) authentication.getPrincipal();
        User data = userRepository.findByIdWithRoles(user.getId()).orElseThrow();

        List<String> roles = data.getRoles().stream()
            .map(UserRole::getRole)
            .collect(Collectors.toList());

        return UserInfoDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .uuid(user.getUuid())
            .roles(roles)
            .build();
    }

    public void register(UserRegisterDTO userRegisterDTO, boolean check) throws Exception {
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

    public void setRole(String id, String role) {
        UserRole userRole = userRoleRepository.findByRole(role).orElseThrow(UserNotFound::new);
        User user = userRepository.findById(id).orElseThrow(UserNotFound::new);
        user.getRoles().add(userRole);
        userRepository.save(user);
    }


} //Implement modify User data later
