    package com.mastercard.repository;
    import com.mastercard.model.users.User;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.Optional;

    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByUsername(String username);
        boolean existsByUsername(String username);
        Optional<User> findById(Long id);


        void deleteById(Long id);
    }
