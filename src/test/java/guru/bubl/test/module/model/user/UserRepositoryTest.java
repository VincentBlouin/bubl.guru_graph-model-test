/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.user;

import com.google.inject.Inject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.forgot_password.UserForgotPasswordToken;
import guru.bubl.module.repository.user.ExistingUserException;
import guru.bubl.module.repository.user.NonExistingUserException;
import guru.bubl.module.repository.user.UserRepository;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class UserRepositoryTest extends ModelTestResources {

    @Test
    public void can_save_user() {
        assertFalse(
                userRepository.usernameExists(
                        "bob_thankyou"
                )
        );
        User user = User.withEmailAndUsername(
                "some_email@example.org",
                randomUsername()
        ).password("password");
        userRepository.createUser(user);
        assertTrue(
                userRepository.emailExists("some_email@example.org")
        );
    }

    @Test
    public void try_to_save_twice_a_user_with_same_email_is_not_possible() {
        String email = randomEmail();
        User user1 = User.withEmailAndUsername(
                email,
                randomUsername()
        ).password("password");
        User user2 = User.withEmailAndUsername(
                email,
                randomUsername()
        ).password("password");
        userRepository.createUser(user1);
        try {
            userRepository.createUser(user2);
            fail();
        } catch (ExistingUserException e) {
            assertThat(
                    e.getMessage(),
                    is("A user already exist with username or email: " + email)
            );
        }
    }

    @Test
    public void try_to_save_twice_a_user_with_same_username_is_not_possible() {
        User user1 = User.withEmailAndUsername(
                randomEmail(),
                "same"
        ).password("password");
        String user2Email = randomEmail();
        User user2 = User.withEmailAndUsername(
                user2Email,
                "same"
        ).password("password");
        userRepository.createUser(user1);
        try {
            userRepository.createUser(user2);
            fail();
        } catch (ExistingUserException e) {
            assertThat(e.getMessage(), Matchers.is("A user already exist with username or email: " + "same"));
        }
    }

    @Test
    public void user_fields_are_well_saved() {
        String email = randomEmail();
        User user = User.withEmailAndUsername(
                email,
                randomUsername()
        ).password("secret");
        userRepository.createUser(user);
        User loadedUser = userRepository.findByEmail(email);
        assertThat(
                loadedUser.id(),
                is(user.id())
        );
        assertThat(
                loadedUser.email(),
                is(user.email())
        );
        assertTrue(
                loadedUser.hasPassword("secret")
        );
    }

    @Test
    public void can_find_user_by_email() {
        User user = createAUser();
        userRepository.createUser(user);
        assertThat(
                userRepository.findByEmail(user.email()),
                is(user)
        );
    }

    @Test
    public void try_to_find_none_existing_user_by_email_throw_and_Exception() {
        try {
            userRepository.findByEmail("non_existing@example.org");
            fail();
        } catch (NonExistingUserException e) {
            assertThat(e.getMessage(), Matchers.is("User not found: non_existing@example.org"));
        }

        try {
            userRepository.findByEmail("");
            fail();
        } catch (NonExistingUserException e) {
            assertThat(e.getMessage(), Matchers.is("User not found: "));
        }
    }

    @Test
    public void can_find_user_by_user_name() {
        User user = createAUser();
        user = userRepository.createUser(user);
        assertThat(
                userRepository.findByUsername(user.username()),
                is(user)
        );
    }

    @Test
    public void try_to_find_non_existing_user_by_username_throws_an_exception() {
        try {
            userRepository.findByUsername("non_existing_user_name");
            fail();
        } catch (NonExistingUserException e) {
            assertThat(e.getMessage(), Matchers.is("User not found: non_existing_user_name"));
        }
    }

    @Test
    public void resetting_password_sets_a_token() {
        User user = userRepository.createUser(
                createAUser()
        );
        UserForgotPasswordToken userForgotPasswordToken = userRepository.getUserForgetPasswordToken(
                user
        );
        assertTrue(
                userForgotPasswordToken.isEmpty()
        );
        userRepository.generateForgetPasswordToken(
                user,
                UserForgotPasswordToken.generate()
        );
        userForgotPasswordToken = userRepository.getUserForgetPasswordToken(
                user
        );
        assertFalse(
                userForgotPasswordToken.isEmpty()
        );
    }

    @Test
    public void can_change_password() {
        User user = userRepository.createUser(
                createAUser()
        );
        assertTrue(user.hasPassword("password"));
        assertFalse(user.hasPassword("new_password"));
        user.password("new_password");
        userRepository.changePassword(user);
        user = userRepository.findByUsername(user.username());
        assertFalse(user.hasPassword("password"));
        assertTrue(user.hasPassword("new_password"));
    }

    @Test
    public void changing_password_nullifies_the_reset_password_token() {
        User user = userRepository.createUser(
                createAUser()
        );
        userRepository.generateForgetPasswordToken(
                user,
                UserForgotPasswordToken.generate()
        );
        assertFalse(
                userRepository.getUserForgetPasswordToken(user).isEmpty()
        );
        user.password("new_password");
        userRepository.changePassword(user);
        assertTrue(
                userRepository.getUserForgetPasswordToken(user).isEmpty()
        );
    }

    @Test
    public void can_update_user_preferred_locale() {
        User user = createAUser();
        userRepository.createUser(
                user
        );
        user = userRepository.findByUsername(
                user.username()
        );
        assertThat(
                user.getPreferredLocales().toString(),
                is("[]")
        );
        List<Locale> preferredLocales = new ArrayList<>();
        preferredLocales.add(
                Locale.CANADA_FRENCH
        );
        user.setPreferredLocales(preferredLocales);
        userRepository.updatePreferredLocales(user);
        user = userRepository.findByUsername(
                user.username()
        );
        assertThat(
                user.getPreferredLocales().toString(),
                is("[fr_CA]")
        );
    }

    @Test
    public void updating_locale_only_updates_for_given_user() {
        User user = createAUser();
        User anotherUser = createAUser();
        userRepository.createUser(
                user
        );
        userRepository.createUser(
                anotherUser
        );
        anotherUser = userRepository.findByUsername(
                anotherUser.username()
        );
        assertThat(
                anotherUser.getPreferredLocales().toString(),
                is("[]")
        );
        List<Locale> preferredLocales = new ArrayList<>();
        preferredLocales.add(
                Locale.CANADA_FRENCH
        );
        user.setPreferredLocales(preferredLocales);
        userRepository.updatePreferredLocales(user);
        anotherUser = userRepository.findByUsername(
                anotherUser.username()
        );
        assertThat(
                anotherUser.getPreferredLocales().toString(),
                is("[]")
        );
    }

    @Test
    public void can_search_users() {
        User userA = createAUser("pomme-verte");
        userRepository.createUser(userA);
        User userB = createAUser("asdf");
        userRepository.createUser(userB);
        List<User> results = userRepository.searchUsers("pomm", userB);
        Assert.assertThat(
                results.get(0).username(),
                is("pomme-verte")
        );
    }

    private String randomEmail() {
        return UUID.randomUUID().toString() + "@me.com";
    }

    private User createAUser() {
        return User.withEmailAndUsername(
                randomEmail(),
                randomUsername()
        ).password("password");
    }

    private User createAUser(String username) {
        return User.withEmailAndUsername(
                randomEmail(),
                username
        ).password("password");
    }

    private String randomUsername() {
        return UUID.randomUUID().toString().substring(0, 15);
    }
}
