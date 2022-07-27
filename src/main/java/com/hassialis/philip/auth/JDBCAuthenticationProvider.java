package com.hassialis.philip.auth;

import java.util.ArrayList;
import java.util.Optional;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hassialis.philip.auth.persistence.UserEntity;
import com.hassialis.philip.auth.persistence.UserRepository;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.inject.Singleton;

@Singleton
public class JDBCAuthenticationProvider implements AuthenticationProvider {

  private static final Logger LOG = LoggerFactory.getLogger(JDBCAuthenticationProvider.class);
  final UserRepository users;

  public JDBCAuthenticationProvider(UserRepository users) {
    this.users = users;
  }

  @Override
  public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest,
      AuthenticationRequest<?, ?> authenticationRequest) {

    final String identity = (String) authenticationRequest.getIdentity();

    return Flowable.create(emitter -> {
      LOG.debug("User {} tries to login", identity);
      final Optional<UserEntity> maybeUser = users.findByEmail(identity);

      if (maybeUser.isPresent()) {
        LOG.debug("Found user {}", maybeUser.get().getEmail());
        String secret = (String) authenticationRequest.getSecret();
        if (secret.equals(maybeUser.get().getPassword())) {
          LOG.debug("User {} successfully logged in", identity);
          emitter.onNext(AuthenticationResponse.success(maybeUser.get().getEmail(), new ArrayList<>()));
          emitter.onComplete();
          return;
        } else {
          LOG.debug("User {} failed to login", identity);
          emitter.onError(new AuthenticationException("Invalid password"));
        }
      } else {
        LOG.debug("No user found for email {}", identity);
      }

      emitter.onError(new AuthenticationException("Login failed"));
    }, BackpressureStrategy.ERROR);
  }
}
