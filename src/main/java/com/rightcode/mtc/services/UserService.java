package com.rightcode.mtc.services;

import com.rightcode.mtc.dto.user.UserInfoRequest;
import com.rightcode.mtc.dto.user.UserInfoResponse;
import com.rightcode.mtc.dto.user.UserRequest;
import com.rightcode.mtc.dto.user.UserResponse;
import com.rightcode.mtc.faults.BusinessFault;
import com.rightcode.mtc.faults.FaultCode;
import com.rightcode.mtc.store.entities.User;
import com.rightcode.mtc.store.repositories.MedicalOrganizationRepository;
import com.rightcode.mtc.store.repositories.MedicalPositionRepository;
import com.rightcode.mtc.store.repositories.MedicalSpecialityRepository;
import com.rightcode.mtc.store.repositories.UserRepository;
import com.rightcode.mtc.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final MedicalOrganizationRepository medicalOrganizationRepository;
    private final MedicalPositionRepository medicalPositionRepository;
    private final MedicalSpecialityRepository medicalSpecialityRepository;
    private final UserMapper mapper;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public User getMedicalWorkerById(Long medicalWorkerId) {
        return repository.findById(medicalWorkerId).orElseThrow(() -> new BusinessFault(
                        String.format("User with id: %s not found", medicalWorkerId),
                        FaultCode.E001.name()
                )
        );
    }

    @Transactional
    public UserResponse updateUser(UserRequest request) {
        Optional<User> userOptional = repository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            throw new BusinessFault("User not found", FaultCode.E001.name());
        }

        User user = userOptional.get();

        validateAndUpdateUser(request, user);

        repository.save(user);

        return mapper.toDto(user);
    }

    private void validateAndUpdateUser(UserRequest request, User user) {
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            Matcher phoneMatcher = PHONE_PATTERN.matcher(request.getPhone());
            if (!phoneMatcher.matches()) {
                throw new BusinessFault("Phone number does not match the required pattern +7 (XXX) XXX-XX-XX", FaultCode.E003.name());
            }
            user.setPhoneNumber(request.getPhone());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Matcher emailMatcher = EMAIL_PATTERN.matcher(request.getEmail());
            if (!emailMatcher.matches()) {
                throw new BusinessFault("Email does not match the required pattern", FaultCode.E003.name());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getSurname() != null && !request.getSurname().isEmpty()) {
            user.setSurname(request.getSurname());
        }

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }

        if (request.getPatronymic() != null && !request.getPatronymic().isEmpty()) {
            user.setPatronymic(request.getPatronymic());
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }

        if (request.getDob() != null && !request.getDob().isEmpty()) {
            try {
                user.setDob(LocalDate.parse(request.getDob()));
            }
            catch (DateTimeParseException e) {
                throw new BusinessFault("Invalid data format", FaultCode.E003.name());
            }
        }

        if (request.getPosition() != null) {
            if (!medicalPositionRepository.existsById(request.getPosition())) {
                throw new BusinessFault("Position does not exist", FaultCode.E001.name());
            }
            user.setPosition(medicalPositionRepository.findById(request.getPosition()).orElse(null));
        }

        if (request.getOrganization() != null) {
            if (!medicalOrganizationRepository.existsById(request.getOrganization())) {
                throw new BusinessFault("Organization does not exist", FaultCode.E001.name());
            }
            user.setOrganization(medicalOrganizationRepository.findById(request.getOrganization()).orElse(null));
        }

        if (request.getSpeciality() != null) {
            if (!medicalSpecialityRepository.existsById(request.getSpeciality())) {
                throw new BusinessFault("Speciality does not exist", FaultCode.E001.name());
            }
            user.setSpeciality(medicalSpecialityRepository.findById(request.getSpeciality()).orElse(null));
        }
    }

    public UserInfoResponse getUserInfo(UserInfoRequest request) {
        Optional<User> userOptional = repository.findById(request.getId());
        if (userOptional.isEmpty()) {
            throw new BusinessFault("User not found", FaultCode.E001.name());
        }

        return mapper.toInfoDto(userOptional.get());
    }
}
