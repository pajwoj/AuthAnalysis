package pl.pajwoj.services;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public void loginSucceeded(String email) {
        attempts.remove(email);
    }

    public void loginFailed(String email) {
        AttemptInfo info = attempts.get(email);
        if (info == null) {
            attempts.put(email, new AttemptInfo(1, LocalDateTime.now()));
        }
        else {
            if (info.lockoutTime.plusMinutes(15).isBefore(LocalDateTime.now())) {
                attempts.put(email, new AttemptInfo(1, LocalDateTime.now()));
            }
            else {
                info.attempts++;
                info.lockoutTime = LocalDateTime.now();
            }
        }
    }

    public boolean isBlocked(String email) {
        AttemptInfo info = attempts.get(email);
        if (info == null) {
            return false;
        }

        if (info.lockoutTime.plusMinutes(15).isBefore(LocalDateTime.now())) {
            attempts.remove(email);
            return false;
        }

        return info.attempts >= 5;
    }

    public long getMinutesUntilUnlock(String email) {
        AttemptInfo info = attempts.get(email);
        if (info == null) {
            return 0;
        }

        LocalDateTime unlockTime = info.lockoutTime.plusMinutes(15);
        return Math.max(0, java.time.Duration.between(LocalDateTime.now(), unlockTime).toMinutes());
    }

    private static class AttemptInfo {
        int attempts;
        LocalDateTime lockoutTime;

        AttemptInfo(int attempts, LocalDateTime lockoutTime) {
            this.attempts = attempts;
            this.lockoutTime = lockoutTime;
        }
    }
}