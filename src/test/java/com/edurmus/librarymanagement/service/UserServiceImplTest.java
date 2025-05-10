package com.edurmus.librarymanagement.service;

import com.edurmus.librarymanagement.exception.user.EmailAlreadyExistException;
import com.edurmus.librarymanagement.exception.user.UsernameAlreadyExistException;
import com.edurmus.librarymanagement.model.dto.request.UserRequest;
import com.edurmus.librarymanagement.model.dto.request.UserRoleRequest;
import com.edurmus.librarymanagement.model.dto.response.UserDetailsResponse;
import com.edurmus.librarymanagement.model.dto.response.UserResponse;
import com.edurmus.librarymanagement.model.dto.response.UserRoleResponse;
import com.edurmus.librarymanagement.model.entity.Role;
import com.edurmus.librarymanagement.model.entity.User;
import com.edurmus.librarymanagement.model.enums.UserRole;
import com.edurmus.librarymanagement.model.mapper.UserMapper;
import com.edurmus.librarymanagement.repository.RoleRepository;
import com.edurmus.librarymanagement.repository.UserRepository;
import com.edurmus.librarymanagement.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Test
    void shouldRegisterUser() {
        UserRequest request = new UserRequest("Emre", "Durmus", "emre_durmus", "emre@example.com",  "05463453543","sifre123");;

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(roleRepository.findByUserRole(UserRole.ROLE_PATRON)).thenReturn(Set.of());
        User savedUser = new User();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        UserResponse response = new UserResponse("emre_durmus", "emre@example.com");
        when(userMapper.toDto(any(User.class))).thenReturn(response);

        UserResponse result = userService.register(request);

        assertThat(result.username()).isEqualTo("emre_durmus");
        verify(userRepository).save(any(User.class));
        log.info("User registered successfully");
    }

    @Test
    void register_shouldThrowEmailAlreadyExistException() {
        UserRequest request = new UserRequest("Emre", "Durmus", "emre_durmus", "emre@example.com",  "05463453543","sifre123");;

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(EmailAlreadyExistException.class);
        log.info("Email already exists");
    }

    @Test
    void register_shouldThrowUsernameAlreadyExistException() {
        UserRequest request = new UserRequest("Emre", "Durmus", "emre_durmus", "emre@example.com",  "05463453543","sifre123");;

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(UsernameAlreadyExistException.class);
        log.info("Username already exists");
    }

    @Test
    void shouldGetAllUsers() {
        List<User> users = List.of(
                User.builder()
                        .username("emre")
                        .email("emre@example.com")
                        .roles(Set.of(Role.builder().userRole(UserRole.ROLE_PATRON).build()))
                        .build(),

                User.builder()
                        .username("ahmet")
                        .email("ahmet@example.com")
                        .roles(Set.of(Role.builder().userRole(UserRole.ROLE_LIBRARIAN).build()))
                        .build());

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDetailsDto(any())).thenReturn(mock(UserDetailsResponse.class));

        List<UserDetailsResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        verify(userMapper, times(2)).toDetailsDto(any());
        log.info("Users fetched successfully");
    }

    @Test
    void shouldUpdateUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);

        UserRequest request = new UserRequest("Emre", "Durmus", "emre_durmus", "emre@example.com",  "05463453543","sifre123");;

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toDetailsDto(existingUser)).thenReturn(mock(UserDetailsResponse.class));

        UserDetailsResponse result = userService.updateUser(userId, request);

        log.info("Updated User: {}", result);

        assertThat(result).isNotNull();
        verify(userRepository).save(existingUser);
        log.info("User updated successfully");
    }

    @Test
    void shouldUpdateUserRole() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        UserRoleRequest roleRequest = new UserRoleRequest("ROLE_LIBRARIAN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByUserRole(UserRole.ROLE_LIBRARIAN)).thenReturn(Set.of());
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toRoleDto(user)).thenReturn(mock(UserRoleResponse.class));

        UserRoleResponse result = userService.updateUserRole(userId, roleRequest);

        assertThat(result).isNotNull();
        verify(userRepository).save(user);
        log.info("User role updated successfully");
    }

    @Test
    void getById_shouldReturnUserResponse_whenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("emredrms");
        user.setEmail("emre@example.com");

        UserDetailsResponse userResponse = new UserDetailsResponse(
                1L, "emredrms", "emre@example.com",  "05463453543", "ROLE_PATRON", true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDetailsDto(user)).thenReturn(userResponse);

        UserDetailsResponse result = userService.getById(userId);

        assertNotNull(result);
        assertEquals("emredrms", result.username());
        assertEquals("emre@example.com", result.email());
    }

    @Test
    void getById_shouldThrowException_whenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getById(userId);
        });
        assertEquals("User not found", exception.getMessage());
    }


    @Test
    void shouldDeleteUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userService.deleteUser(userId);

        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
        log.info("User deleted (inactive) successfully");
    }

    @Test
    void shouldThrowUserNotFoundException() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
        log.info("User not found");
    }
}
