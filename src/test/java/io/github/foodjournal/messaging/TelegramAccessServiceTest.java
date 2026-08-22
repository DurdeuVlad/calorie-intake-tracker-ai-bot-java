package io.github.foodjournal.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.foodjournal.config.BotProperties;
import io.github.foodjournal.domain.TelegramAccessGrant;
import io.github.foodjournal.repository.TelegramAccessGrantRepository;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TelegramAccessServiceTest {
  private final TelegramAccessGrantRepository grants = mock(TelegramAccessGrantRepository.class);
  private final Map<Long, TelegramAccessGrant> rows = new HashMap<>();
  private TelegramAccessService service;

  @BeforeEach void setUp() {
    when(grants.findById(anyLong())).thenAnswer(call -> Optional.ofNullable(rows.get(call.getArgument(0))));
    when(grants.existsById(anyLong())).thenAnswer(call -> rows.containsKey(call.getArgument(0)));
    when(grants.save(any(TelegramAccessGrant.class))).thenAnswer(call -> {
      TelegramAccessGrant row = call.getArgument(0);
      rows.put(row.getTelegramUserId(), row);
      return row;
    });
    service = new TelegramAccessService(grants, properties(Set.of(1L), Set.of(7L)));
  }

  @Test void seedsBootstrapAdminsAndLegacyUsersWithoutRestoringRevokedUsers() {
    assertThat(service.allowed(1L)).isTrue();
    assertThat(service.isAdmin(7L)).isTrue();

    assertThat(service.revokeRegularUser(1L)).isTrue();
    assertThat(service.allowed(1L)).isFalse();
    assertThat(service.isAdmin(7L)).isTrue();
  }

  @Test void grantsAndRevokesRegularUsersButNeverAdministrators() {
    service.allowed(7L);
    service.grantUser(22L, 7L);
    assertThat(service.allowed(22L)).isTrue();
    assertThat(service.revokeRegularUser(22L)).isTrue();
    assertThat(service.allowed(22L)).isFalse();
    assertThat(service.revokeRegularUser(7L)).isFalse();
  }

  private BotProperties properties(Set<Long> legacy, Set<Long> admins) {
    return new BotProperties("token", "secret", legacy, admins, "Europe/Bucharest", "", "test",
        10, "transcribe", "https://foods.example", "", "", "");
  }
}
