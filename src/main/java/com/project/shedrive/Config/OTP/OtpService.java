package com.project.shedrive.Config.OTP;

import com.project.shedrive.Exceptions.FalseInputData;
import com.project.shedrive.Exceptions.OtpExpirationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private final OtpConfig otpConfig;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_ATTEMPTS = 5;
    private static final int COOLDOWN_SECONDS = 60;

    // Key Builders
    private String otpKey(String phoneNumber, OtpPurpose purpose) {
        return "otp:" + phoneNumber + ":" + purpose.name();
    }

    private String cooldownKey(String phoneNumber, OtpPurpose purpose) {
        return "otp:cooldown:" + phoneNumber + ":" + purpose.name();
    }

    private String attemptsKey(String phoneNumber, OtpPurpose purpose) {
        return "otp:attempts:" + phoneNumber + ":" + purpose.name();
    }

    // Internal
    private String generateCode() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }

    private String generateAndStore(String phoneNumber, OtpPurpose purpose) {
        String code = generateCode();
        redisTemplate.opsForValue().set(
                otpKey(phoneNumber, purpose),
                code,
                Duration.ofMinutes(otpConfig.getExpirationMinutes())
        );
        return code;
    }

    // Public API
    public String sendOtp(String phoneNumber, OtpPurpose purpose) {
        return generateAndStore(phoneNumber, purpose);
    }

    public String resendOtp(String phoneNumber, OtpPurpose purpose) {
        String cooldownKey = cooldownKey(phoneNumber, purpose);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            Long ttl = redisTemplate.getExpire(cooldownKey);
            throw new FalseInputData(
                    "Please wait " + ttl + " seconds before requesting another OTP."
            );
        }

        redisTemplate.opsForValue().set(
                cooldownKey,
                "LOCKED",
                Duration.ofSeconds(COOLDOWN_SECONDS)
        );

        return generateAndStore(phoneNumber, purpose);
    }


    public void verify(String phoneNumber, String code, OtpPurpose purpose) {
        String otpKey = otpKey(phoneNumber, purpose);
        String storedCode = redisTemplate.opsForValue().get(otpKey);

        if (storedCode == null) {
            throw new OtpExpirationException("OTP expired or not found");
        }

        String attemptsKey = attemptsKey(phoneNumber, purpose);
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);

        if (attempts != null && attempts == 1) {
            redisTemplate.expire(
                    attemptsKey,
                    Duration.ofMinutes(otpConfig.getExpirationMinutes())
            );
        }

        if (!storedCode.equals(code)) {
            if (attempts != null && attempts >= MAX_ATTEMPTS) {
                deleteOtp(phoneNumber, purpose);
                throw new FalseInputData(
                        "Maximum attempts exceeded. Please request a new OTP."
                );
            }
            long remaining = MAX_ATTEMPTS - (attempts != null ? attempts : 0);
            throw new FalseInputData(
                    "Invalid OTP code. " + remaining + " attempts remaining."
            );
        }

        deleteOtp(phoneNumber, purpose);
    }


    public void deleteOtp(String phoneNumber, OtpPurpose purpose) {
        redisTemplate.delete(otpKey(phoneNumber, purpose));
        redisTemplate.delete(attemptsKey(phoneNumber, purpose));
        redisTemplate.delete(cooldownKey(phoneNumber, purpose));
    }

    // =====================================================
    // Enum
    // =====================================================

    public enum OtpPurpose {
        REGISTER, RESET_PASSWORD, LOGIN, DELETE_ACCOUNT
    }
}