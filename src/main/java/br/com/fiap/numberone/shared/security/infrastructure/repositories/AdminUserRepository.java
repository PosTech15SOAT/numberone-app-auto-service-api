package br.com.fiap.numberone.shared.security.infrastructure.repositories;

import br.com.fiap.numberone.shared.security.domain.entities.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {

	Optional<AdminUser> findByUsernameIgnoreCase(String username);
}
