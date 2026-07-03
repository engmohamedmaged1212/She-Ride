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
    // internal using
    private String buildKey (String phoneNumber , OtpPurpose purpose) {
        return "otp:" + phoneNumber + ":" + purpose.name();
    }
    private String generateCode (){
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public String generateAndStore(String phoneNumber, OtpPurpose purpose) {
        String code = generateCode();
        String key = buildKey(phoneNumber, purpose);

        redisTemplate.opsForValue().set(
                key,
                code,
                Duration.ofMinutes(otpConfig.getExpirationMinutes())
        );

        return code;
    }
    public void verify(String phoneNumber, String code, OtpPurpose purpose) {
        String key = buildKey(phoneNumber, purpose);
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new OtpExpirationException("OTP expired or not found");
        }
        if (!storedCode.equals(code)) {
            throw new FalseInputData("Invalid OTP code");
        }
        redisTemplate.delete(key);
    }
    public enum OtpPurpose {
        REGISTER, RESET_PASSWORD
    }
}
