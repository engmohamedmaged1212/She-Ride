package com.project.shedrive.User.Auth;

import com.project.shedrive.Config.Jwt.JwtConfig;
import com.project.shedrive.Config.Jwt.JwtResponse;
import com.project.shedrive.Config.Jwt.JwtServices;
import com.project.shedrive.Exceptions.NotAuthException;
import com.project.shedrive.User.User;
import com.project.shedrive.User.UserRepository;
import com.project.shedrive.User.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtServices jwtService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * POST /api/v1/auth/login
     *
     * Request:
     * {
     *   "phoneNumber": "01012345678",
     *   "password": "Sara@123",
     * }
     *
     * Response 201:
     * {
         "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzIiwicGhvbmVfbnVtYmVyIjoiMDEwMjQ0MjE1ODciLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3ODM3MDc3NjIsImV4cCI6MTc4MzcxNDk2Mn0.jaz_ycTLBvbWvHuxN3fYzIdoQ3k3VkAS0XiEwr1anxBnviUuiMzkMyVnpu4QmbqUVT-YCJMnx-WqBmjDsU59hQ"
     * }*/
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody @Valid LoginDto loginRequestDto,
            HttpServletResponse response){

       try {
           var auth = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(
                           loginRequestDto.getPhoneNumber(),
                           loginRequestDto.getPassword()
                   )
           );

           var user = (User) auth.getPrincipal();

           if (Boolean.TRUE.equals(user.getIsBlocked())) {
               throw new NotAuthException("Your account has been blocked. Reason: " + user.getBlockReason() +"So can you call the customer services");
           }
           var accessToken = jwtService.generateAccessTokens(user);
           var refreshToken = jwtService.generateRefreshTokens(user);

           var cookie = new Cookie("refreshToken", refreshToken.toString());
           cookie.setHttpOnly(true);
           cookie.setPath("/api/v1/auth");
           cookie.setMaxAge((int) jwtConfig.getRefreshTokenExpiration());
           cookie.setSecure(true);

           response.addCookie(cookie);

           return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
       }
       catch (BadCredentialsException ex){
           throw new BadCredentialsException("Invalid username or password");
       }
    }
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(value = "refreshToken") String refreshToken){
        var jwt = jwtService.parseToken(refreshToken);
        if(!jwt.isValid()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId = jwt.getUserId();
        var user = userRepository.findById(userId).orElseThrow();
        var accessToken =jwtService.generateAccessTokens(user);
        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

}
