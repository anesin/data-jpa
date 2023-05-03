package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;


public interface UsernameOnlyOpen {

  @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")
  String getUsername();

}
