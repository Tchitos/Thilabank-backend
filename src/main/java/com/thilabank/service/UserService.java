package com.thilabank.service;

import com.thilabank.dto.*;
import com.thilabank.entity.User;
import com.thilabank.exceptions.BadRequestException;
import com.thilabank.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto register(@Valid UserRegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new BadRequestException("Email déjà utilisé");
        } else if (registerDto.getPassword().length() < 4) {
            throw new BadRequestException("Le mot de passe doit contenir au moins 4 caractères");
        }

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setPhone(registerDto.getPhone());
        user.setAddress(registerDto.getAddress());
        user.setCity(registerDto.getCity());
        user.setZipcode(registerDto.getZipcode());

        userRepository.save(user);

        return toResponseDto(user);
    }

    public UserResponseDto getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return toResponseDto(user);
    }

    public UserResponseDto updateProfile(String email, UserUpdateDto updateDto) {

        if (updateDto.getPassword() != null && updateDto.getPassword().length() < 4) {
            throw new BadRequestException("Password must be at least 4 characters");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (updateDto.getFirstName() != null) user.setFirstName(updateDto.getFirstName());
        if (updateDto.getLastName() != null) user.setLastName(updateDto.getLastName());
        if (updateDto.getEmail() != null) user.setEmail(updateDto.getEmail());
        if (updateDto.getPhone() != null) user.setPhone(updateDto.getPhone());
        if (updateDto.getAddress() != null) user.setAddress(updateDto.getAddress());
        if (updateDto.getCity() != null) user.setCity(updateDto.getCity());
        if (updateDto.getZipcode() != null) user.setZipcode(updateDto.getZipcode());
        if (updateDto.getPassword() != null) user.setPassword(passwordEncoder.encode(updateDto.getPassword()));

        userRepository.save(user);

        return toResponseDto(user);
    }

    private UserResponseDto toResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setCity(user.getCity());
        dto.setZipcode(user.getZipcode());
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
