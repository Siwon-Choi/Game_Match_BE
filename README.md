# GameMatch

GameMatch는 게임별 사용자 프로필, 그룹, 친선 경기 매칭, 게시판 기능을 제공하는 Spring Boot 백엔드 프로젝트입니다.

## 프로젝트 방향

이 프로젝트는 Clean Architecture와 Hexagonal Architecture를 기준으로 설계합니다.

- `domain`: 비즈니스 규칙과 핵심 모델
- `application`: 유스케이스, 입력 포트, 출력 포트
- `adapter.in`: Web Controller 등 외부 입력 어댑터
- `adapter.out`: Persistence 등 외부 출력 어댑터
- `global`: 공통 응답, 예외, 설정, 보안

## DB 설계

DB 구조는 dbdiagram.io 문서를 기준으로 관리합니다.

- DB 다이어그램: [GameMatch DB Diagram](https://dbdiagram.io/d/6a4a1df736d348d1206ca849)
- URL: https://dbdiagram.io/d/6a4a1df736d348d1206ca849

주요 기준은 다음과 같습니다.

- `users`, `game`, `game_user`, `game_group`, `friendly_match`, `match_request`, `match_participation` 중심으로 도메인을 나눕니다.
- 중복 생성 방지는 애플리케이션 로직보다 DB `unique constraint`를 우선합니다.
- 매칭 승인, 참가 확정, 모집 마감처럼 상태가 바뀌는 작업은 낙관적 락 또는 비관적 락으로 race condition을 관리합니다.
- 게시글 추천/비추천은 `post_vote` 테이블로 1인 1상태를 관리하고, 카운터는 atomic update로 처리합니다.
- Outbox는 초기 구현 범위에서는 제외하고, 외부 알림이나 메시지 발행 보장이 필요해질 때 도입합니다.

## 기술 스택

- Java 17
- Spring Boot
- Gradle
- Spring Data JPA

