package ru.mtuci.rbposervak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mtuci.rbposervak.entities.Signature;
import ru.mtuci.rbposervak.entities.ENUMS.signature.STATUS;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, UUID> {

    List<Signature> findByUpdatedAtAfter(LocalDateTime date);

    /// Поиск актуальных
    @Query("SELECT s FROM Signature s WHERE s.status = 'ACTUAL'")
    List<Signature> findAllActual();

    @Query("SELECT s FROM Signature s WHERE s.id IN :ids AND s.status = 'ACTUAL'")
    List<Signature> findActualByIds(@Param("ids") List<UUID> ids);

    List<Signature> findByIdIn(List<UUID> ids);


    Optional<List<Signature>> findByStatus(STATUS status);

    List<Signature> findByUpdatedAtAfterAndStatus(LocalDateTime date, STATUS status);
}
