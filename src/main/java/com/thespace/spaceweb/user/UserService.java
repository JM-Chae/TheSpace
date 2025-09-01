package com.thespace.spaceweb.user;

import com.thespace.common.exception.Exceptions.UserNotFound;
import com.thespace.spaceweb.user.UserDTOs.MyPage;
import com.thespace.spaceweb.user.UserDTOs.UpdateInfo;
import java.util.List;
import java.util.Objects;
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
    public final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    public User getReferenceById(String id) {
        return userRepository.getReferenceById(id);
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(UserNotFound::new);
    }

    public List<Long> findUserRoles(String userId) {
        return userRepository.findRoleIdsByUserId(userId);
    }

    public List<Long> findRoomIds(String uuid) {
        return userRepository.findRoomIdsByUuid(uuid);
    }

    public User findUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid).orElseThrow(UserNotFound::new);
    }

    public UserDTOs.MyPage getMyPage(String uuid) {

        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFound::new);

        return new MyPage(user.getSignature(), user.getName(), user.getUuid(), user.getIntroduce(), user.getCreateDate());
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
        User data = userRepository.findById(user.getId()).orElseThrow(UserNotFound::new);

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
            .uuid(uuid)
            .password(password)
            .id(userRegisterDTO.id())
            .name(userRegisterDTO.name())
            .email(userRegisterDTO.email())
            .introduce(userRegisterDTO.introduce())
            .signature(userRegisterDTO.signature())
            .build();

        userRepository.save(user);
        userRoleService.setRole(user.getId(), 1L);
    }

    public void updateInfo(UpdateInfo dto, User user) {

        if (!Objects.equals(dto.name(), "")) {
            user.updateName(dto.name());
        }

        if (!Objects.equals(dto.introduce(), "")) {
            user.updateIntroduce(dto.introduce());
        }

        if (!Objects.equals(dto.signature(), "")) {
            user.updateSignature(dto.signature());
        }

        userRepository.save(user);
    }

    public boolean checkUuid(String uuid) {
        return !userRepository.existsByUuid(uuid);
    }

    public boolean checkId(String id) {
        return !userRepository.existsById(id);
    }
}
