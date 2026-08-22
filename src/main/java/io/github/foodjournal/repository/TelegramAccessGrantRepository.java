package io.github.foodjournal.repository;

import io.github.foodjournal.domain.TelegramAccessGrant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramAccessGrantRepository extends JpaRepository<TelegramAccessGrant, Long> {}
