# 🎫선착순 할인 쿠폰 발급 시스템

🔗[더 자세한 고민이 궁금하다면, ***Click!!!***](https://iamheaming.notion.site/46f5e122fbee47ac93a4ac6c91eb7eb1?pvs=4) 

쿠폰 이벤트 기간 중, 한정된 수량의 할인 쿠폰을 먼저 신청한 유저에게 제공하는 시스템입니다.

## 요구사항
* 이벤트 기간내에(ex. 2024-01-01~2024-01-03) 발급된다.
* 유저당 1번의 쿠폰 발급만 가능하다.
* 쿠폰의 수량이 한정되어 있다.

## Architecture
### 비동기 쿠폰 발급 요청 처리
![image](https://github.com/heaming/coupon-event/assets/85826542/39cafde6-5ce6-45e1-b7ee-cd1163a8408f)

### 캐시 데이터 기반 Validation
![image](https://github.com/heaming/coupon-event/assets/85826542/4112d733-157f-4337-9894-ce230f894ef8)

### Tech Stack
- Open JDK 17.0.10, Spring Boot 3.3.0, Spring Data Jpa, QueryDsl
- Redis 7, MySql 8, H2
- Docker, locust

## Result
* Docker, Locust
* Runtime: 3m
* Numbers of Users: 1000
>- **Total Requests**: 681,471 / **Fail Requests**: 280 / **Failure rate**: 0.04%
> - **RPS** 3497 / **RTS(ms)** 208.16

![image](https://github.com/heaming/coupon-event/assets/85826542/91f168bb-1745-4e1c-9ebc-e856215fcd2e)

