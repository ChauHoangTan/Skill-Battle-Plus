package com.example.eureka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
public class OtpService {
    private static final Duration OTP_EXPIRATION_TIME = Duration.ofMinutes(2);
    private final RedisTemplate<String, String> redisTemplate;
    private static final String OTP_PREFIX = "OTP: ";
    private static final Random RANDOM = new Random();

    @Autowired
    public OtpService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateOtp() {
        return String.valueOf(100000 + RANDOM.nextInt(899999));
    }

    public void saveOtp(String email, String otp) {
        String key = OTP_PREFIX + email;
        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRATION_TIME);
    }

    public boolean validateOtp(String email, String otp) {
        String key = OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        return storedOtp != null && storedOtp.equals(otp);
    }

    public void deleteOtp(String email) {
        String key = OTP_PREFIX + email;
        redisTemplate.delete(key);
    }
}
