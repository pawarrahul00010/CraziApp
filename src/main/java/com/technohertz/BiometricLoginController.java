package com.technohertz;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.technohertz.exception.ResourceNotFoundException;
import com.technohertz.model.Biometric;
import com.technohertz.repo.BiometricRepository;

@RestController
@RequestMapping("/biometric")
public class BiometricLoginController {
	@Autowired
	private BiometricRepository biometricrepo;

	@PutMapping("/user/{id}")
	public ResponseEntity<Biometric> setBiometricDetails(@PathVariable(value = "id") int userId,
			@Valid @RequestBody Biometric biomericDetails) throws ResourceNotFoundException {
		Biometric biometric = biometricrepo.findByBiometricId(userId);
		
		biometric.setBiometricImage(biomericDetails.getBiometricImage());
		biometric.setCreateDate(getDate());
		biometric.setLastModifiedDate(getDate());

	 Biometric setBiometric = biometricrepo.save(biometric);
		return ResponseEntity.ok(setBiometric);

	}

	private LocalDateTime getDate() {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();

		return now;

	}
}
