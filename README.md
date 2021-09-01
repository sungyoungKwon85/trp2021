# Triple

## 3. 도시 조회 API

---

<br>

### How to run

- local 환경에 redis가 설치되어 있지 않은 경우 아래 커맨드를 실행할 것
- docker가 설치됨을 가정

##### redis

```
> brew install Docker
> docker-compose -f docker-compose-local.yml up -d 
```

##### run application

```
> ./gradlew bootRun
```

##### database console

```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb;
User Name: sa
```

---

<br>

### APIs

```
POST
1. members
http://localhost:8080/api/v1/members
{
  "name": "kwon"
}

POST
2. cities
http://localhost:8080/api/v1/cities
{
  "name": "koje"
}

GET
3. cities/{id}
http://localhost:8080/api/v1/members/1/cities/5

GET
4. cities
http://localhost:8080/api/v1/members/1/cities

POST
5. /members/{memberId}/trips
http://localhost:8080/api/v1/members/1/trips
{
  "title": "my first pusan",
  "cityId": 2,
  "startAt": "2021-09-11",
  "endAt": "2021-09-17"
}

GET
6. /members/{memberId}/trips/{tripId}
http://localhost:8080/api/v1/members/1/trips/2

GET
7. /members/{memberId}/trips
http://localhost:8080/api/v1/members/1/trips
```

---

<br>

### 도시리스트 조회 로직 설명

- CityService.getCities
- `d. 최근 1주일 이내에 조회한 도시` 의 경우 Redis를 이용하여 조회된 도시의 id list를 저장함.
  - zSet 자료 구조를 활용하여 중복 방지 및 순서를 유지하도록 함
- `e. 위 조건에 해당되지 않는 도시들은 랜덤으로 배열합니다.`
  - City 엔티티의 컬럼수를 경계로 하여 Random 값을 추출하여 이를 활용해 order by에 활용함.

