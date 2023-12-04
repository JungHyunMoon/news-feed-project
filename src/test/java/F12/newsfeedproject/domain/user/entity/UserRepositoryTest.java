package F12.newsfeedproject.domain.user.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import F12.newsfeedproject.domain.user.constant.UserRole;
import F12.newsfeedproject.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class UserRepositoryTest {

    private UserRepository userRepository;

    private TestEntityManager entityManager;

    @Test
    @Transactional
    public void saveUser() {
        // given
        User user = User.builder()
            .userName("tester")
            .userPassword("password")
            .userEmail("tester@example.com")
            .userImageUrl("https://example.com/profile.png")
            .userIntroduce("Hello world.")
            .userRole(UserRole.USER)
            .build();

        // when
        User savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // then
        User foundUser = userRepository.findById(savedUser.getUserId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserName()).isEqualTo("tester");
    }

    @Test
    @Transactional
    public void updateUser() {
        // given
        User user = User.builder()
            .userName("tester")
            .userPassword("password")
            .userEmail("tester@example.com")
            .userImageUrl("https://example.com/profile.png")
            .userIntroduce("Hello world.")
            .userRole(UserRole.USER)
            .build();
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // when
        User foundUser = userRepository.findById(user.getUserId()).orElse(null);
        foundUser.updateUser(user);
        userRepository.save(foundUser);
        entityManager.flush();
        entityManager.clear();

        // then
        User updatedUser = userRepository.findById(foundUser.getUserId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getUserName()).isEqualTo("tester");
    }

    @Test
    @Transactional
    public void deleteUser() {
        // given
        User user = User.builder()
            .userName("tester")
            .userPassword("password")
            .userEmail("tester@example.com")
            .userImageUrl("https://example.com/profile.png")
            .userIntroduce("Hello world.")
            .userRole(UserRole.USER)
            .build();
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // when
        User foundUser = userRepository.findById(user.getUserId()).orElse(null);
        userRepository.delete(foundUser);
        entityManager.flush();
        entityManager.clear();

        // then
        User deletedUser = userRepository.findById(foundUser.getUserId()).orElse(null);
        assertThat(deletedUser).isNull();
    }
}