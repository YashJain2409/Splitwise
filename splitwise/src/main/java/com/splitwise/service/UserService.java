package com.splitwise.service;

import com.splitwise.dao.UserDAO;
import com.splitwise.dto.UpdateUserProfile;
import com.splitwise.dto.UserDTO;
import com.splitwise.enums.NotificationEventType;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.NotificationPreferences;
import com.splitwise.model.User;
import com.splitwise.repository.NotificationPreferenceRepository;
import com.splitwise.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private NotificationPreferenceRepository notificationPreferenceRepository;

	@Autowired
	private ModelMapper modelMapper;

	public void updateProfile(UpdateUserProfile userDTO, int userId) {

		User user = userRepository.findById(userId).orElseThrow();
		if (!user.getPassword().equals(userDTO.getPassword())) {
			throw new ApplicationException("Current Password is not correct", HttpStatus.BAD_REQUEST);
		}

		user.setPassword(userDTO.getNewPassword());
		userRepository.save(user);

		for (Map.Entry<NotificationEventType, Boolean> entry : userDTO.getPreferences().entrySet()) {
			updatePref(user, entry.getKey(), entry.getValue());
		}

	}

	private void updatePref(User user, NotificationEventType key, Boolean value) {
		NotificationPreferences pref = notificationPreferenceRepository.findByUserAndNotificationEventType(user, key);
		pref.setEmailEnabled(value);
		notificationPreferenceRepository.save(pref);
	}

	@Transactional
	public UserDTO saveUser(UserDTO userDTO) {
		UserDTO userDto = userDAO.saveUser(userDTO);
		User u = userRepository.findById(userDTO.getUserId()).orElseThrow();
		NotificationPreferences pref = new NotificationPreferences();
		for (NotificationEventType e : NotificationEventType.values()) {
			pref.setEmailEnabled(true);
			pref.setNotificationEventType(e);
			pref.setSmsEnabled(false);
			pref.setUser(u);
			notificationPreferenceRepository.save(pref);
		}
		return userDto;
	}
}
