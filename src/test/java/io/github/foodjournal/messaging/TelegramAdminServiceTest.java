package io.github.foodjournal.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

class TelegramAdminServiceTest {
  private final TelegramAccessService access = mock(TelegramAccessService.class);
  private final TelegramAdminService service = new TelegramAdminService(access);

  @Test void grantsRegularUserOnlyForPrivateAdminCommand() {
    when(access.isAdmin(10L)).thenReturn(true);

    assertThat(service.handle("/adduser 42", 10L, true)).contains("User access granted.");
    verify(access).grantUser(42L, 10L);
  }

  @Test void groupCommandDoesNotDiscloseTargetOrTouchAccess() {
    String response = service.handle("/removeuser 42", 10L, false).orElseThrow();

    assertThat(response).isEqualTo("Admin commands are available only in a private chat.");
    assertThat(response).doesNotContain("42");
    verifyNoInteractions(access);
  }

  @Test void rejectsSelfRemovalAndNonAdmins() {
    when(access.isAdmin(10L)).thenReturn(true);
    assertThat(service.handle("/remove 10", 10L, true).orElseThrow()).contains("own administrator access");
    verify(access, never()).revokeRegularUser(anyLong());

    reset(access);
    when(access.isAdmin(11L)).thenReturn(false);
    assertThat(service.handle("/add 42", 11L, true).orElseThrow()).contains("not authorized");
    verify(access, never()).grantUser(anyLong(), anyLong());
  }

  @Test void refusesToRemoveAnAdministratorOrUnknownUser() {
    when(access.isAdmin(10L)).thenReturn(true);
    when(access.revokeRegularUser(42L)).thenReturn(false);

    assertThat(service.handle("/removeuser@FoodBot 42", 10L, true))
        .contains("That user is not a removable regular user.");
  }
}
