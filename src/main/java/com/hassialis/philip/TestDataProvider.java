package com.hassialis.philip;

import com.hassialis.philip.auth.persistence.UserEntity;
import com.hassialis.philip.auth.persistence.UserRepository;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

@Singleton
public class TestDataProvider {

  private final UserRepository users;

  public TestDataProvider(UserRepository users) {
    this.users = users;
  }

  @EventListener
  public void onStartup(StartupEvent event) {
    if (users.findByEmail("philip.alexander.hassialis@gmail.com").isEmpty()) {
      final UserEntity newUser = new UserEntity();
      newUser.setEmail("philip.alexander.hassialis@gmail.com");
      newUser.setPassword("secret");
      users.save(newUser);
    }
  }
}
