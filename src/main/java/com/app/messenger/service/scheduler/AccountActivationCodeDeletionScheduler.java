package com.app.messenger.service.scheduler;

import com.app.messenger.repository.UserRepository;
import com.app.messenger.repository.model.AccountState;
import com.app.messenger.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;


@Service
@RequiredArgsConstructor
public class AccountActivationCodeDeletionScheduler {
    private final UserRepository userRepository;
    @Value("${application.user.account.activation-code.expiration-time}")
    private String accountActivationCodeExpirationTime;

    public void scheduleUserDeletionForNotActivatedUser(User user) {
        int expirationTime = Integer.parseInt(accountActivationCodeExpirationTime);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                User userToDelete = userRepository.findByUsername(user.getUsername()).orElse(null);
                if (userToDelete != null) {
                    AccountState accountState = userToDelete.getUserAccount().getState();
                    if (!accountState.equals(AccountState.ACTIVATED)) {
                        userRepository.deleteById(userToDelete.getId());
                    }
                }
            }
        }, expirationTime);
    }
}
