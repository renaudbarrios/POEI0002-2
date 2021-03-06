package com.example.demo.services.fixtures;

import com.example.demo.entities.User;
import com.example.demo.repository.UserRepository;
import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.Hash;

/**
 * User fixtures service.
 * @author Cedrick Pennec
 */
@Component
public class UserFixtureService implements Fixture {

  /**
 * UserRepository
 */
@Autowired
  private UserRepository repository;
  /**
 * List of String
 */
private List<String> usernames;
  /**
 * String
 */
private String username;

  /**
 * Number of fake users to create. 100 by default.
 */
@Value("${number.fakeuser:100}")
  int nbFakeUser;

  /**
 * Load user fakers.
 */
@Override
  public void load() {
    Faker faker = new Faker();
    usernames = new ArrayList<>();

    User user1 = new User();
    user1.setUsername("admin");
    user1.setPassword(Hash.hash().encode("admin"));
    user1.setCity("paris");
    user1.setRole("admin");
    user1.setEnable(true);
    this.repository.save(user1);


    for (int i = 0; i < nbFakeUser; i++) {
      User user = new User();
      user.setCity(faker.address().city());
      user.setEnable(true);
      //            user.setRole("user");
      user.setRole("ANONYMOUS");

      user.setPassword(Hash.hash().encode(faker.elderScrolls().dragon()));
      do {
        username = faker.harryPotter().character();
      } while (this.usernames.contains(username));
      usernames.add(username);
      user.setUsername(username);
      user.setEmail(faker.internet().emailAddress());
      user.setName(faker.pokemon().name());
      Date birthday = faker.date().birthday();
      user.setBirthDate(LocalDateTime.ofInstant(birthday.toInstant(), ZoneId.systemDefault()));
      user.setSex(faker.team().creature());
      user.setProfession(faker.job().title());
      user.setDescription(faker.harryPotter().quote());
      user.setMaritalStatus(faker.leagueOfLegends().rank());

      this.repository.save(user);
    }
  }
}
