# PUPPYNESS API

Java 21/Spring Boot 기반의 커뮤니티 백엔드입니다. 
초기엔 인메모리 저장소로 구현했으나, 어댑터 계층과 OCP·DIP를 통해 JPA + 데이터베이스 구조로 전환했습니다. 
덕분에 도메인 서비스 로직을 거의 수정하지 않고 프로덕션 DB로 마이그레이션하는 경험을 할 수 있었습니다. 
게시글∙댓글∙좋아요∙사용자 관리 기능을 중심으로, 세션 인증과 CSRF 방어를 갖춘 REST API와 전용 캐시 계층, 
이미지 업로드(GCS) 및 Redis 기반의 집계 성능 최적화를 제공합니다.

## 기술 스택
### 언어
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white)

### 프레임워크
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

### 데이터베이스
![MySQL 8](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)

### 빌드
![Gradle 8](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

### 테스트
![JUNIT 5](https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)

### 인프라
![Google Cloud Storage](https://img.shields.io/badge/Google%20Cloud%20Storage-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white)

## 주요 기능

- **사용자**: 회원가입/프로필 조회 및 수정/비밀번호 변경/회원탈퇴, 이메일∙닉네임 중복 확인, 프로필 이미지 업로드.
- **인증**: UsernamePasswordAuthenticationFilter를 커스텀한 LoginFilter가 로그인 요청을 가로채어 인증을 수행하고, 
CustomUserDetails를 만들어 인증 객체를 생성, `/csrf` 로 노출되는 CSRF 토큰, `/logout` 세션 종료.
- **게시글**: multipart 업로드(본문 JSON + 이미지 파일), 무한 스크롤(키셋 기반), 상세 조회, 수정, 삭제.
- **댓글**: 게시글별 댓글 CRUD, `lastCommentId` 기반 무한 스크롤, 삭제 플래그 처리.
- **좋아요/집계**: Redis `CountCache` 를 이용한 조회수∙좋아요수∙댓글수 집계 및 파이프라인 조회, `CommentCount` 테이블을 통한 동시성 제어.
- **이미지 업로드**: Google Cloud Storage 연동(`GCSImageUploader`) 및 간단한 대체 구현(`SimpleImageUploader`), P6Spy SQL 포매팅.
- **문서화/테스트**: springdoc Swagger UI(`/api-test`), 서비스·컨트롤러·리포지토리 단위의 테스트 + Jacoco 커버리지 리포트.

## 기술 스택

| 영역 | 사용 기술                                          |
| --- |------------------------------------------------|
| 언어/런타임 | Java 21, Gradle 8                              |
| 프레임워크 | Spring Boot 3.5, Spring Data JPA, Spring Security |
| 데이터베이스 | MySQL 8                                        |
| 인프라/스토리지 | Redis(조회/좋아요 카운터), Google Cloud Storage(이미지)   |
| 문서화 | Swagger, springdoc-openapi, Jacoco         |

## 패키지 구조 요약

```
src/main/java/com/kyle/week4
├─ controller        # REST API, BaseResponse, Swagger docs
├─ service           # 도메인 서비스
├─ repository        # Adapter/JPA/Memory 추상화
├─ entity            # User/Post/Comment 등 Aggregate
├─ security          # Session 기반 인증, Custom LoginFilter
├─ cache             # Redis CountCache & Simple cache store
├─ utils             # ImageUploader(GCS), DataInit 등 부가 기능
├─ config/redis      # GCP Storage, Redis, Swagger, JPA 설정
└─ exception         # ErrorCode, CustomException, Global handler
```

- 모든 API 응답은 `BaseResponse<T>` 래핑 형태(`data`, `success`, `errorMessage`, `httpStatus`)이며, `ResponseInterceptor` 가 응답 상태코드를 `httpStatus`와 일치하도록 변경합니다.
- `CustomCacheable`/`CustomCacheEvict` + `CustomCacheAspect` 가 사용자 프로필 조회와 같은 읽기 성능을 최적화합니다.
- `CountCache` → `RedisCountCache` 조합은 게시글/좋아요 집계를 Redis String 연산 + 파이프라인으로 처리해 무한 스크롤 응답의 N+1 문제를 줄입니다.

## 실행 전 준비물

1. **Java & Gradle**: JDK 21, `./gradlew` 사용(별도 설치 불필요).
2. **MySQL 8**: 데이터베이스 및 계정을 사전에 생성합니다.
   - `kyle_week7` (로컬), `kyle_week7_test` (테스트) 스키마 필요.
   - 기본 계정: `root` / `root` (필요 시 `application-local.yml` 에서 변경).
3. **Redis**: 로컬(6379) 및 테스트용(6380) 인스턴스가 필요합니다.
4. **Google Cloud Storage**: `spring.cloud.gcp.storage.*` 프로퍼티에 맞는 버킷과 서비스 계정 JSON.
5. (선택) **Docker 실행 예시**
   ```bash
   # MySQL
   docker run -d --name mysql-week4 -e MYSQL_ROOT_PASSWORD=root \
     -p 3306:3306 mysql:8 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

   # Redis (로컬/테스트 포트 분리)
   docker run -d --name redis-week4 -p 6379:6379 redis:7
   docker run -d --name redis-week4-test -p 6380:6379 redis:7
   ```
- GCS 인증 파일은 로컬 경로 또는 Secret Manager 등을 이용해 주입합니다.
- 테스트 프로파일(`SPRING_PROFILES_ACTIVE=test`)은 별도 MySQL 스키마와 Redis(6380 포트)에 연결하며 `ddl-auto=create` 로 스키마를 구성합니다.
- `decorator.datasource.p6spy` 설정으로 SQL 로깅(P6Spy)을 제어할 수 있습니다.

## 빌드 & 실행

```bash
./gradlew clean bootRun           # local 프로파일 기준 애플리케이션 실행
./gradlew clean build             # 패키징(JAR 생성)
SPRING_PROFILES_ACTIVE=test ./gradlew test  # 테스트 DB/Redis를 띄운 뒤 실행
```

### 멀티파트 요청 예시

```bash
curl -X POST http://localhost:8080/posts \
  -H "Cookie: JSESSIONID=..." \
  -F 'request={"title":"제목","content":"내용"};type=application/json' \
  -F 'images=@/path/to/image1.png' \
  -F 'images=@/path/to/image2.png'
```

## 테스트 & 품질

- **단위/통합 테스트**: `src/test/java` 에 `controller`, `service`, `repository` 테스트가 구분되어 있습니다.
  - `IntegrationTestSupport` 는 `@SpringBootTest` + `@ActiveProfiles("test")` 로 구동되며, 프로파일 설정을 통해 프로덕션과 테스트 환경을 분리하였습니다.
  이를 통해 SpringContext 로딩을 1회로 줄여 데스트 실행 속도를 향상했습니다.
  - `ControllerTestSupport` 는 `@WebMvcTest` 환경에서 필터를 제거한 MockMvc 테스트를 수행합니다.
- **Jacoco**: `./gradlew test` 후 `build/reports/jacoco/test/html/index.html` 로 커버리지를 확인할 수 있습니다. `build.gradle` 의 `coverageExclusions` 로 기술성 클래스(설정, 예외 등)는 제외합니다.
- **로그/모니터링**: P6Spy + `P6spySqlFormatter` 로 SQL을 보기 좋게 출력하며, `org.springframework.security` 로그 레벨은 DEBUG로 기본 설정되어 있습니다.

## API 설계 메모

- **무한 스크롤**: 게시글(`GET /posts?limit=&lastPostId=`)과 댓글(`GET /posts/{postId}/comments?limit=&lastCommentId=`)은 커서 기반 페이지네이션을 사용합니다.
- **집계 전략**: 정합성을 위해 댓글 수는 Update 쿼리를 통한 비관적 락을 통해 처리하고, 조회수/좋아요 수는 Redis를 사용하여 증가/감소를 처리합니다.
- **캐싱**: 사용자 프로필은 커스텀 어노테이션 기반 메모리 캐시에 저장되고, 변경 시 `CustomCacheEvict` 로만 제거됩니다.
- **보안**: `SecurityConfig` 는 CORS(`http://localhost:3000`), CSRF 쿠키, 세션 고정 방지, 로그아웃 시 쿠키 제거를 설정합니다.
- **세션/CSRF 문제**: 로그인 전에 `/csrf` 를 호출해 `XSRF-TOKEN` 쿠키를 확보한 뒤, 이후 요청 헤더(`X-XSRF-TOKEN`)에 전달해야 합니다.
- **응답 규격**: 모든 응답 실패는 `CustomException` + `GlobalExceptionHandler` 를 통해 예외에 대한 메시지와 상태 코드를 반환합니다.

## 기능 시연 영상
### 회원가입/로그인

https://github.com/user-attachments/assets/6af348c8-88ef-463f-9466-05db61f28d67

### 회원 정보 수정/비밀번호 수정/로그아웃

https://github.com/user-attachments/assets/605ed585-3fdb-4582-b07d-973a4f55deac


### 게시글 목록 및 상세 정보 조회

https://github.com/user-attachments/assets/e411d17e-0057-444f-ab67-b162b1d43791

### 게시글 생성/수정

https://github.com/user-attachments/assets/d289b105-83f0-4765-8645-44c1cef75cd8

### 게시글 댓글 생성/수정/삭제

https://github.com/user-attachments/assets/1e0c7140-9e3f-4db0-b16d-a6eb5cc0bb00

## Troubleshooting
