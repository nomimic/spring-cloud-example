# Step 03

## 목표

* Jaeger를 활용한 Tracing 적용 ( spring cloud sleuth 사용)

## 준비

* consul 실행(`consul agent -server -bootstrap -ui -client=0.0.0.0 -bind 127.0.0.1 -data-dir ./consul/data`)
* Jaeger 실행

```
docker/msa-env/docker-compose.yml 을 docker-compose 통해서 실행
```

## 설정

### 의존성

```xml

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>

<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

### spring properties 설정

```yaml
spring:
  zipkin:
    base-url: http://localhost:9411 # 기본 값이며, Jaeger Collector 주소
```

### 확인 방법

* Jaeger UI 주소(http://localhost:16686/)
* config server, backend-hello-service , gateway 실행
* curl http://localhost:8000/hello-service 실행
* Jaeger UI 접속 후 검색 및 확인

## 참고 사항

* spring boot actuator 의 endpoint 중 `traces` 를 활성화 하여 기본 값인 최대 10000 개까진 trace 정보 조회 가능
* `management.endpoint.traces.capacity` 를 통해 최대 사이즈 지정 가능
