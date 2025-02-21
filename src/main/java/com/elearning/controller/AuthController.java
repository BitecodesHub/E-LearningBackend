package com.elearning.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.elearning.models.User;
import com.elearning.repositories.UserRepository;
import com.elearning.response_request.ApiResponse;
import com.elearning.response_request.LoginRequest;
import com.elearning.response_request.LoginResponse;
import com.elearning.response_request.OtpVerificationRequest;
import com.elearning.security.JwtService;
import com.elearning.services.CustomUserDetailsService;
import com.elearning.services.RegistrationService;
import java.util.Collections;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(RegistrationService registrationService, UserRepository userRepository, JwtService jwtService,
                          AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService) {
        this.registrationService = registrationService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            // Find the user by ID
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
            
            // Update only if the new values are not null
            if (updatedUser.getUsername() != null) existingUser.setUsername(updatedUser.getUsername());
            if (updatedUser.getName() != null) existingUser.setName(updatedUser.getName());
            if (updatedUser.getProfileurl() != null) existingUser.setProfileurl(updatedUser.getProfileurl());
            if (updatedUser.getPhonenum() != null) existingUser.setPhonenum(updatedUser.getPhonenum());
            if (updatedUser.getState() != null) existingUser.setState(updatedUser.getState());
            if (updatedUser.getPassword() != null) existingUser.setPassword(updatedUser.getPassword()); // Ideally, hash this

            // Save the updated user
            userRepository.save(existingUser);

            return ResponseEntity.ok(new ApiResponse(true, "User updated successfully."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Update failed: " + e.getMessage()));
        }
    }
    @PostMapping("/google-auth")
    public ResponseEntity<?> googleAuth(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String name = request.get("name");
            String picture = request.get("picture");
            String password = "Google";
            String username = name;

            if (email == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(Map.of("success", false, "message", "Google OAuth failed: Email is missing."));
            }

            // Check if user exists
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                // Register new user
                user = new User();
                user.setEmail(email);
                user.setName(name != null ? name : "Google User");
                user.setProfileurl(picture != null ? picture : "https://webcrumbs.cloud/placeholder");
                user.setUsername(email.split("@")[0]);
                user.setPassword(password);
                user.setRole("USER");
                user.setEnabled(true);
                userRepository.save(user);
            }

            // ✅ Generate a JWT token using your backend JWT service
            String jwtToken = jwtService.generateToken(user.getEmail());

            // ✅ Send JWT token and user details back
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Google login successful",
                "token", jwtToken,
                "username", user.getUsername(),
                "email", user.getEmail(),
                "profileurl", user.getProfileurl(),
                "userid", user.getId(),
                "name", user.getName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("success", false, "message", "Google OAuth failed: " + e.getMessage()));
        }
    }


    
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
        	if(!registrationService.validUser(user)) {
        		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: Email or Username Already Exist");
        	}
            registrationService.registerUser(user);
            return ResponseEntity.ok("User registered. Check your email for OTP.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
        boolean verified = registrationService.verifyOtp(request.getEmail(), request.getOtp());
        if (verified) {
            return ResponseEntity.ok(new ApiResponse(true, "Email verified!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Invalid or expired OTP."));
        }
    }


    @GetMapping("/user/{id}")
    public ResponseEntity<?> login(@PathVariable Long id ) {
    	User user=new User();
    	try {
  		user= userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with Id: " + id));
    	}
    	catch (Exception e) {
    		return ResponseEntity.ok("error aa gya");
		}
    	return ResponseEntity.ok(user);
    }
    
    @GetMapping("/getUsername/{id}")
    public ResponseEntity<String> getUsernameById(@PathVariable Long id ) {
    	User user=new User();
    	try {
  		user= userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with Id: " + id));
    	}
    	catch (Exception e) {
    		return ResponseEntity.ok("error aa gya");
		}
    	String userName=user.getName();
    	return ResponseEntity.ok(userName);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        try {
            // Load the user from the database based on the provided email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            // Check if the provided password matches the stored password
            if (password.equals(user.getPassword())) {
                // If the passwords match, generate the JWT token
                String token = jwtService.generateToken(user.getEmail());
                String username=user.getUsername();
                String phonenum = (user.getPhonenum() != null) ? user.getPhonenum() : "";
                String state=(user.getState() != null) ? user.getState() : "";
                Long userid=(user.getId() != null) ? user.getId() : 0;
                String profileurl=(user.getProfileurl() != null) ? user.getProfileurl() : "";
                String name=(user.getName() != null) ? user.getName() : "";
                // Return the success response with the token
                LoginResponse response = new LoginResponse(true, token,username,email,phonenum,state,userid,profileurl,name);
                return ResponseEntity.ok(response);
            }

            // If authentication fails
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid email or password."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Login failed: " + e.getMessage()));
        }
    }

}
