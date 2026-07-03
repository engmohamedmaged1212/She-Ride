package com.project.shedrive.Config.OTP;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.otp")
public class OtpConfig {
    private int expirationMinutes ;
}
