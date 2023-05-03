package study.datajpa.repository;


public interface NestedClosedProjection {

  String getUsername();

  TeamInfo getTeam();

  interface TeamInfo {  // 최적화되지 않음
    String getName();
  }

}
