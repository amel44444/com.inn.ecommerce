package com.inn.ecommerce.servicelmpl;

import com.inn.ecommerce.JWT.JwtUtil;
import com.inn.ecommerce.POJO.User;
import com.inn.ecommerce.constents.EcommerceConstants;
import com.inn.ecommerce.dao.PasswordResetTokenRepository;
import com.inn.ecommerce.dao.UserDao;
import com.inn.ecommerce.model.PasswordResetToken;
import com.inn.ecommerce.service.UserService;
import com.inn.ecommerce.utils.EcommerceUtils;
import com.inn.ecommerce.utils.EmailUtils;
import com.inn.ecommerce.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class UserServicelmpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailUtils emailUtils;


    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return EcommerceUtils.getResponseEntity("Successfully registered", HttpStatus.OK);
                } else {
                    return EcommerceUtils.getResponseEntity("Email already exists.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return EcommerceUtils.getResponseEntity(EcommerceConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            log.error("Error occurred during signup", ex);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login {}", requestMap);
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if (user != null && passwordEncoder.matches(requestMap.get("password"), user.getPassword())) {
                if ("true".equalsIgnoreCase(user.getStatus())) {
                    String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
                    return ResponseEntity.ok("{\"token\":\"" + token + "\"}");
                } else {
                    return EcommerceUtils.getResponseEntity("Wait for ADMIN approval", HttpStatus.UNAUTHORIZED);
                }
            }
            return EcommerceUtils.getResponseEntity("Invalid credentials", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            log.error("Error occurred during login", ex);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(passwordEncoder.encode(requestMap.get("password")));
        user.setStatus("false"); // Par défaut à false = besoin d’approbation admin
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers(String authHeader) {
        try {
            // Vérification du token JWT
            String userEmail = jwtUtil.extractUsername(authHeader.substring(7)); // suppose "Bearer <token>"
            log.info("getAllUsers called by user: {}", userEmail);

            // Récupérer tous les utilisateurs depuis la base de données
            List<User> users = userDao.findAll(); // Assurez-vous que cette méthode existe dans UserDao
            List<UserWrapper> userWrappers = new ArrayList<>();
            for (User user : users) {
                userWrappers.add(new UserWrapper(user.getId(), user.getName(), user.getEmail(), user.getContactNumber(), user.getStatus(), user.getRole()));
            }

            return new ResponseEntity<>(userWrappers, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error validating token or fetching users", ex);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }
    }
    @Override
    public ResponseEntity<String> updateUser(Map<String, String> requestMap) {
        try {
            if (!requestMap.containsKey("id")) {
                return EcommerceUtils.getResponseEntity("User ID is required", HttpStatus.BAD_REQUEST);
            }

            Integer userId = Integer.parseInt(requestMap.get("id"));
            User user = userDao.findById(userId).orElse(null);

            if (user == null) {
                return EcommerceUtils.getResponseEntity("User not found", HttpStatus.NOT_FOUND);
            }

            // Mise à jour des champs si présents
            if (requestMap.containsKey("name")) user.setName(requestMap.get("name"));
            if (requestMap.containsKey("email")) user.setEmail(requestMap.get("email"));
            if (requestMap.containsKey("contactNumber")) user.setContactNumber(requestMap.get("contactNumber"));
            if (requestMap.containsKey("status")) user.setStatus(requestMap.get("status"));
            if (requestMap.containsKey("role")) user.setRole(requestMap.get("role"));
            if (requestMap.containsKey("password")) {
                user.setPassword(passwordEncoder.encode(requestMap.get("password")));
            }

            userDao.save(user);

            // Envoi de l'e-mail après sauvegarde
            emailUtils.sendEmail(
                    user.getEmail(),
                    "Mise à jour du compte",
                    "Bonjour " + user.getName() + ", votre compte a été mis à jour et approuvé par l’administrateur."
            );

            return EcommerceUtils.getResponseEntity("User updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //@Override
    public ResponseEntity<String> updatePassword(Map<String, String> requestMap, String authHeader) {
        try {
            // Vérifier le token JWT et récupérer l'email de l'utilisateur
            String userEmail = jwtUtil.extractUsername(authHeader.substring(7)); // "Bearer <token>"

            // Vérifier que les données de la requête sont présentes
            if (!requestMap.containsKey("oldPassword") || !requestMap.containsKey("newPassword")) {
                return EcommerceUtils.getResponseEntity("Old and new passwords are required", HttpStatus.BAD_REQUEST);
            }

            String oldPassword = requestMap.get("oldPassword");
            String newPassword = requestMap.get("newPassword");

            // Récupérer l'utilisateur via son email
            User user = userDao.findByEmail(userEmail);

            if (user == null) {
                return EcommerceUtils.getResponseEntity("User not found", HttpStatus.NOT_FOUND);
            }

            // Vérifier que l'ancien mot de passe est correct
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return EcommerceUtils.getResponseEntity("Old password is incorrect", HttpStatus.BAD_REQUEST);
            }

            // Mettre à jour le mot de passe avec le nouveau mot de passe
            user.setPassword(passwordEncoder.encode(newPassword));
            userDao.save(user); // Sauvegarder l'utilisateur avec le nouveau mot de passe

            return EcommerceUtils.getResponseEntity("Password updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred during password update", e);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Méthode pour envoyer le mot de passe actuel

   /* public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        String email = requestMap.get("email");
        User user = userDao.findByEmail(email);  // Recherche de l'utilisateur par email

        if (user != null) {
            // Le mot de passe actuel (en clair, non sécurisé)
            String currentPassword = user.getPassword();

            // Envoyer un email avec le mot de passe actuel
            String subject = "Your Current Password";
            String body = "Hello " + user.getName() + ",\n\n" +
                    "Your current password is: " + currentPassword + "\n\n" +
                    "Please change your password after logging in.";

            emailUtils.sendEmail(user.getEmail(), subject, body);

            // Retourner une réponse à l'utilisateur
            return new ResponseEntity<>("Check your email for your current password.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Email not found.", HttpStatus.BAD_REQUEST);
        }
    }
*/

   /* public void forgotPassword(String email) {
        String body = "Voici un lien ou un mot de passe temporaire";
        emailUtils.sendEmail(email, "Réinitialisation de mot de passe", body);
    }*/

    /*/public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        String email = requestMap.get("email");
        User user = userDao.findByEmail(email);  // Recherche de l'utilisateur par email

        if (user != null) {
            // Le mot de passe actuel (en clair, non sécurisé)
            String currentPassword = user.getPassword();

            // Envoyer un email avec le mot de passe actuel
            String subject = "Your Current Password";
            String body = "Hello " + user.getName() + ",\n\n" +
                    "Your current password is: " + currentPassword + "\n\n" +
                    "Please change your password after logging in.";

            emailUtils.sendEmail(user.getEmail(), subject, body);

            // Retourner une réponse à l'utilisateur
            return new ResponseEntity<>("Check your email for your current password.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Email not found.", HttpStatus.BAD_REQUEST);
        }
    }

     */
    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        String email = requestMap.get("email");
        User user = userDao.findByEmail(email);

        if (user != null) {
            String token = UUID.randomUUID().toString();  // ou JWT
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));  // expire après 30 minutes
            passwordResetTokenRepository.save(resetToken);

            String resetLink = "http://localhost:3000/reset-password?token=" + token;

            emailUtils.sendEmail(user.getEmail(), "Reset your password",
                    "Click the link to reset your password: " + resetLink);

            return new ResponseEntity<>("Password reset link sent to your email.", HttpStatus.OK);
        }

        return new ResponseEntity<>("User with this email doesn't exist.", HttpStatus.BAD_REQUEST);
    }



    public ResponseEntity<String> resetPassword(Map<String, String> requestMap) {
        String token = requestMap.get("token");
        String newPassword = requestMap.get("newPassword");

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken != null && resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userDao.save(user);
            passwordResetTokenRepository.delete(resetToken);
            return new ResponseEntity<>("Password updated successfully.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Invalid or expired token.", HttpStatus.BAD_REQUEST);
    }




}
