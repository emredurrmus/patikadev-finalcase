package com.edurmus.librarymanagement.service.impl;

import com.edurmus.librarymanagement.exception.user.EmailAlreadyExistException;
import com.edurmus.librarymanagement.exception.user.UserNotFoundException;
import com.edurmus.librarymanagement.exception.user.UsernameAlreadyExistException;
import com.edurmus.librarymanagement.model.dto.request.UserRequest;
import com.edurmus.librarymanagement.model.dto.request.UserRoleRequest;
import com.edurmus.librarymanagement.model.dto.response.UserDetailsResponse;
import com.edurmus.librarymanagement.model.dto.response.UserResponse;
import com.edurmus.librarymanagement.model.dto.response.UserRoleResponse;
import com.edurmus.librarymanagement.model.entity.User;
import com.edurmus.librarymanagement.model.enums.UserRole;
import com.edurmus.librarymanagement.model.mapper.UserMapper;
import com.edurmus.librarymanagement.repository.RoleRepository;
import com.edurmus.librarymanagement.repository.UserRepository;
import com.edurmus.librarymanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponse register(UserRequest request) {
        validateUserRequest(request);
        User user = mapUserFromRequest(request);
        userRepository.save(user);
        return userMapper.toDto(user);
    }


    @Override
    public List<UserDetailsResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDetailsDto)
                .toList();
    }

    @Override
    public UserDetailsResponse getById(Long id) {
        return userMapper.toDetailsDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Override
    @Transactional
    public UserDetailsResponse updateUser(Long id, UserRequest request) {
        validateUserRequest(request);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        updateUserFields(user, request);
        return userMapper.toDetailsDto(userRepository.save(user));
    }


    @Override
    @Transactional
    public UserRoleResponse updateUserRole(Long id, UserRoleRequest userRoleRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        UserRole newRole = UserRole.valueOf(userRoleRequest.role().toUpperCase());

        user.setRoles(roleRepository.findByUserRole(newRole));
        return userMapper.toRoleDto(userRepository.save(user));
    }


    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        User user = userRepository.getReferenceById(id);
        user.setActive(false);
        userRepository.save(user);
        log.info("User with ID {} set as inactive", id);
    }


    private void validateUserRequest(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistException("User already exists with email: " + request.email());
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistException("User already exists with username: " + request.username());
        }
    }

    private User mapUserFromRequest(UserRequest request) {
        User user = new User();
        updateUserFields(user, request);
        user.setRoles(roleRepository.findByUserRole(UserRole.ROLE_PATRON));
        return user;
    }

    private void updateUserFields(User user, UserRequest request) {
        user.setUsername(request.username());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setPassword(passwordEncoder.encode(request.password()));
    }

}

