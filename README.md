# RunWith

상태: 완료
나의 역할: 백엔드 개발, 데이터베이스 & 클라우드 서버 구축
공동 작업자: 프론트엔드 개발자 2명, 백엔드 개발자 1명
기술: AWS EC2, AWS RDS, Flutter, MySQL, SpringBoot
날짜: 2024년 3월 4일 → 2024년 6월 18일

# 📝 기획

---

- 늘어나는 운동에 대한 관심과 다르게 바쁜 사회 속에서 꾸준히 운동을 할 수 있게 도와주는 비대면 러닝 매칭 앱 RunWith. 멀리 떨어져 있는 상대와도 실시간으로 달리기로 경쟁을 할 수 있다.
- 차별성
    - ‘비대면’ 매칭
        - 공간적 제약을 없애고, 누군가와 달리기를 하고싶을 때 서로 매칭을 통해 달리기 실력을 경쟁 할 수 있고, 또한 자신의 코스와 상대의 코스를 공유하면서 달리기에 대한 관심도를 높일 수 있다.
    - 경쟁을 통한 등급 부여
        - 실력이 비슷한 사람들끼리의 매칭을 위해, 등급을 부여하여 경쟁을 고취할 수 있고, 이 경쟁을 도입하면서 운동을 더 꾸준히 하고, 기록을 단축하기 위해서 신체 능력을 향상 시킬 수 있게 한다.
    - ‘실시간’ 매칭
        - 기존 어플과는 다르게 언제든 누군가와 실시간으로 대전할 수 있고, 이를 통해서 같이 뛰고있다는 느낌을 받게 해줄 수 있다.

# 📜 설계

---

## 📚Database ERD

![Image](https://github.com/user-attachments/assets/426fa5a3-0631-44ff-b0d7-e0d09d98d986)

유저 테이블

| 속성 | 자료형 | 설명 |
| --- | --- | --- |
| user_id | bigint | 고유 사용자 식별자 |
| nickname | varchar(20) | 사용자 닉네임 |
| gender | boolean | 성별 |
| profileimage | varchar(300) | 프로필 이미지 링크 |
| email | varchar(255) | 사용자 이메일 |

매치 정보 테이블

| 속성 | 자료형 | 설명 |
| --- | --- | --- |
| match_id | bigint | 매치 식별자 |
| matchStartTime | datetime | 매치 시작 시간 |
| matchEndTime | datetime | 매치 종료 시간 |
| matchType | boolean | 매치 종류 |
| matchResult | int | id를 통해 매치 승자 표시 |
| matchDistance | int | 매치 거리 |

GPS 테이블

| 속성 | 자료형 | 설명 |
| --- | --- | --- |
| GPSDataId | bigint | GPS데이터 ID |
| record_id | bigint | 기록 테이블 ID |
| GPSTime | datetime | GPS기록 시간 |
| Longitude | float | 위도 |
| Latitude | float | 경도 |
| Altitude | float | 고도 |

참가자 테이블

| 속성 | 자료형 | 설명 |
| --- | --- | --- |
| ID | bigint | 테이블 고유 식별자 |
| user_id | bigint | 사용자 식별자 |
| match_id | bigint | 매치 식별자 |
| completed | boolean | 완료 여부 |
| completionTime | datetime | 완료 시간 |

Km별 정보 테이블

| 속성 | 자료형 | 설명 |
| --- | --- | --- |
| ID | bigint | 테이블 ID |
| user_id | bigint | 유저 식별자 |
| rating | int | 레이팅 점수 |
| win | int | 승수 |
| lose | int | 패수 |
| best_record | double | 최고 기록 |

러닝 기록 테이블

| 속성 | 자료형 | 설명 |
| --- | --- | --- |
| record_id | bigint | 기록 테이블 ID |
| user_id | bigint | 유저 식별자 |
| match_id | bigint | 매치 식별자 |
| runningDistance | int | 달린 거리 |
| averageSpeed | float | 평균 속도 |
| runningTime | int | 달린 시간 |
| changedRating | int | 레이팅 변화값 |
| runningDate | date | 달린 날짜 |

## 📨API 리스트

| API 기능 | HTTP 메서드 | 주소 |
| --- | --- | --- |
| 회원 조회 | GET | /member/{id} |
| 모든 회원 조회 | GET | /member/all |
| 회원 수정 | POST | /member/edit/{fix} |
| 회원 가입 | POST | /member/join |
| 회원 삭제 | DELETE | /member/leave/{id} |
| 매칭 요청 | GET | /match/apply/{id}/{matchtype} |
| 전적 리스트 요청 | GET | /record/apply |
| 전적 세부사항 요청 | GET | /record/detail |
| 거리별 기록 요청 | GET |  |

## 🌐 AWS 설계

![Image](https://github.com/user-attachments/assets/e8e93b32-eed2-4f11-acb5-5940335186d3)
# 👨🏻‍💻담당 구현(강창진)

---

프로젝트에서 나는 **서버 개발 및 유지보수**를 주로 담당하였다. 주요 구현 내용은 다음과 같다.

- **Spring Boot 기반 RESTful API 개발**
    - 매칭 알고리즘 설계 및 구현
    - MySQL을 활용한 데이터베이스 연결
- **AWS 클라우드 환경 구축 및 서버 배포**
    - AWS EC2를 활용하여 서버를 구축하고, RDS를 이용해 데이터베이스를 관리하였다.
    - CI/CD 파이프라인을 구축하여 자동 배포 환경을 구성하였다.
