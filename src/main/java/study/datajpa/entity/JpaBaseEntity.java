package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;


@Getter
@MappedSuperclass
public class JpaBaseEntity {

  @Column(updatable = false)
  private LocalDateTime createdDate;
  private LocalDateTime lastModifiedDate;


  @PrePersist
  public void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    createdDate = now;
    lastModifiedDate = now;
  }


  @PreUpdate
  public void preUpdate() {
    lastModifiedDate = LocalDateTime.now();
  }

}
