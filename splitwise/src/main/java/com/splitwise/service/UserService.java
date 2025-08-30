package com.splitwise.service;

import com.splitwise.dao.UserDAO;
import com.splitwise.dto.UpdateUserProfile;
import com.splitwise.dto.UserDTO;
import com.splitwise.enums.NotificationEventType;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.NotificationPreferences;
import com.splitwise.model.User;
import com.splitwise.repository.NotificationPreferenceRepository;
import com.splitwise.model.UserAuth;
import com.splitwise.repository.UserAuthRepository;
import com.splitwise.repository.UserRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserAuthRepository userAuthRepository;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	UserAuthService userAuthService;

	@Autowired
	private NotificationPreferenceRepository notificationPreferenceRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void updateProfile(UpdateUserProfile userDTO, String username) {
		System.out.println("user " + username);
		UserAuth userAuth = userAuthRepository.findByUserEmail(username);
		if ((userDTO.getPic() == null || userDTO.getPic().isEmpty()) || userDTO.getName() == null
				|| userDTO.getName().isEmpty())
			throw new ApplicationException("Invalid inputs provided", HttpStatus.BAD_REQUEST);

		User user = userRepository.findByEmail(username);
		if (userDTO.getPassword() != null && !passwordEncoder.matches(userDTO.getPassword(), userAuth.getPassword())) {
			throw new ApplicationException("Invalid Credentials", HttpStatus.UNAUTHORIZED);
		}

		if (userDTO.getNewPassword() != null && userDTO.getPassword() == null) {
			throw new ApplicationException("Enter current password", HttpStatus.BAD_REQUEST);
		}

		user.setProfilePic(userDTO.getPic());
		user.setName(userDTO.getName());
		userRepository.save(user);
		if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isEmpty()) {
			userAuth.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
			userAuth.setUser(user);
			userAuthRepository.save(userAuth);
		}
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

		UserDTO userDTO1 = userDAO.saveUser(userDTO);

		User user = modelMapper.map(userDTO1, User.class);
		UserAuth userAuth = new UserAuth();
		userAuth.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		userAuth.setUser(user);
		if (userDTO1 != null) {
			userAuthService.saveUserAuth(userAuth);
			for (NotificationEventType e : NotificationEventType.values()) {
				NotificationPreferences pref = new NotificationPreferences();
				pref.setEmailEnabled(true);
				pref.setNotificationEventType(e);
				pref.setSmsEnabled(false);
				pref.setUser(user);
				notificationPreferenceRepository.save(pref);
			}
		}

		return userDTO1;
	}
}
