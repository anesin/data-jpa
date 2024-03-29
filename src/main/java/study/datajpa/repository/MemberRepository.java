package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long>,
                                          MemberRepositoryCustom,
                                          JpaSpecificationExecutor<Member> {

  List<Member> findByUsernameAndAgeGreaterThan(String username, int age);


  //  @Query(name = "Member.findByUsername")
  List<Member> findByUsername(@Param("username") String username);


  @Query("select m from Member m where m.username = :username and m.age = :age")
  List<Member> findMember(@Param("username") String username, @Param("age") int age);


  @Query("select m.username from Member m")
  List<String> findUsernameList();


  @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
  List<MemberDto> findMemberDto();


  @Query("select m from Member m where m.username in :names")
  List<Member> findByNames(@Param("names") Collection<String> usernames);


  List<Member> findMembersByUsername(String name);

  Member findMemberByUsername(String name);

  Optional<Member> findOptionalMemberByUsername(String name);


  Page<Member> findByAge(int age, Pageable pageable);

  Slice<Member> findSliceByAge(int age, Pageable pageable);

  List<Member> findListByAge(int age, Pageable pageable);

  @Query(value = "select m from Member m",
      countQuery = "select count(m.username) from Member m")
  Page<Member> findMemberAllCountBy(Pageable pageable);


  @Modifying(clearAutomatically = true)
  @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
  int bulkAgePlus(@Param("age") int age);


  @Query("select m from Member m left join fetch m.team")
  List<Member> findFetchJoinAll();

  @Override
  @EntityGraph(attributePaths = {"team"})
  List<Member> findAll();

  @EntityGraph(attributePaths = {"team"})
  @Query("select m from Member m")
  List<Member> findMemberEntityGraphAll();

  @EntityGraph("Member.all")
  @Query("select m from Member m")
  List<Member> findMemberNamedEntityGraphAll();

  @EntityGraph(attributePaths = {"team"})
  List<Member> findMemberEntityGraphByUsername(String username);

  @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
  List<Member> findReadOnlyByUsername(String name);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Member> findLockByUsername(String name);


  List<UsernameOnly> findProjectionsByUsername(String name);

  List<UsernameOnlyOpen> findOpenProjectionsByUsername(String name);

  <T> List<T> findProjectionsByUsername(String name, Class<T> type);


  @Query(value = "select * from member where username = ?", nativeQuery = true)
  Member findByNativeQuery(String username);


  @Query(value = "select m.member_id as id, m.username, t.name as teamName from member m left join team t on m.team_id = t.team_id",
         countQuery = "select count(*) from member",
         nativeQuery = true)
  Page<MemberProjection> findByNativeProjection(Pageable pageable);

}
