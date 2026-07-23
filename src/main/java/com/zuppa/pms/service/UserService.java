package com.zuppa.pms.service;

import com.zuppa.pms.dto.UserDto;
import com.zuppa.pms.entity.User;
import com.zuppa.pms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final com.zuppa.pms.repository.ProductRepository productRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        
        // Delete all products owned by this user to prevent foreign key constraint violations
        java.util.List<com.zuppa.pms.entity.Product> userProducts = productRepository.findByUserId(id);
        productRepository.deleteAll(userProducts);
        
        userRepository.deleteById(id);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
