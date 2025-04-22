package ru.mtuci.rbposervak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.rbposervak.entities.SignatureHistory;

import java.util.UUID;

@Repository
public interface SignatureHistoryRepository extends JpaRepository<SignatureHistory, UUID> {
}
