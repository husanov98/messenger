package uz.mh.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mh.messenger.model.Manager;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager,Long> {
    @Query(nativeQuery = true,value = "select * from managers where phone_number=:phone")
    Optional<Manager> findByPhoneNumber(String phone);
}
