# SPRING ADVANCED

## 1. 과제 개요

Spring 기반 Todo 애플리케이션에서 아래 내용을 중심으로 리팩토링과 기능 개선을 진행했습니다.

- AuthUserArgumentResolver로 로그인 사용자 정보 주입
- Early Return, 불필요한 if-else 제거
- DTO + Validation으로 입력값 검증 분리
- @EntityGraph로 N+1 문제 해결
- 테스트 코드 수정 및 보완
- AOP 기반 어드민 API 로깅 (도전 과제)

---

## 2. 필수 과제 요약

### Lv 1. ArgumentResolver

- `AuthUserArgumentResolver`를 구현해서 컨트롤러에서 `AuthUser`를 바로 받을 수 있게 만들었습니다.
- 사용자 정보 처리 로직을 한 곳에 모아서, 컨트롤러 중복 코드를 줄였습니다.

### Lv 2. 코드 개선

- 회원가입 로직에 Early Return을 적용해, 이미 사용 중인 이메일이면 바로 예외를 던지도록 수정했습니다.
- 날씨 조회 클라이언트에서 if-else 중첩을 없애고 에러를 먼저 처리하는 구조로 정리했습니다.
- 비밀번호 형식 검증은 서비스가 아닌 DTO + Validation에서 처리하도록 역할을 분리했습니다.

### Lv 3. N+1 문제 해결

- Todo 목록 조회 시 User를 함께 가져오기 위해 `@EntityGraph`를 사용하도록 변경해 N+1 문제를 줄였습니다.

### Lv 4. 테스트 코드 정비

- PasswordEncoder, ManagerService, CommentService 관련 테스트를 실제 예외 타입·메시지와 맞게 수정했습니다.
- Todo의 user가 null인 경우를 명시적으로 예외 처리하여 테스트와 실제 동작이 일치하도록 했습니다.

---

## 3. 도전 과제 – AOP 기반 어드민 API 로깅

도전 과제로, 어드민 전용 API에 AOP를 사용해서 접근/요청/응답을 로그로 남기는 기능을 구현했습니다.

### 3-1. 대상 API

- `CommentAdminController.deleteComment()`
- `UserAdminController.changeUserRole()`

도메인 패키지 안의 `*AdminController`를 한 번에 잡는 포인트컷을 사용해서,
새 어드민 API가 생겨도 같은 방식으로 로깅이 적용되도록 했습니다.

### 3-2. 설계 방식

- 포인트컷
    - `org.example.expert.domain.*.controller.*AdminController.*(..)` 패턴을 대상으로 설정했습니다.
    - 모든 어드민 컨트롤러 메서드에 공통으로 AOP 로깅이 적용됩니다.

- 로깅 시점
    - `@Around`를 사용해 메서드 실행 전·후를 감싸는 방식으로 구현했습니다.
    - 실행 전: 요청 정보와 시작 시각을 기록합니다.
    - 실행 후: 응답 정보와 처리 결과를 함께 기록합니다.

### 3-3. 로깅 내용

어드민 API가 호출될 때마다 아래 정보를 로그로 남기도록 했습니다.

- 요청한 사용자 ID
- API 요청 시각
- 요청 URL
- 요청 본문(RequestBody, JSON 기준)
- 응답 본문(ResponseBody)

이를 통해,

- 어드민 API 호출 기록을 쉽게 추적할 수 있고,
- 요청/응답 데이터를 기준으로 디버깅과 모니터링을 할 수 있으며,
- 운영 환경에서 어드민 기능 사용 내역을 더 명확하게 관리할 수 있습니다.

---

## 4. 정리

- 공통 관심사(인증, 로깅)를 분리하고,
- 코드 구조를 단순하게 정리하고,
- 성능 이슈(N+1)와 테스트를 함께 손보면서
  전체 애플리케이션의 읽기 쉬움과 안정성을 높이는 데 집중했습니다.
