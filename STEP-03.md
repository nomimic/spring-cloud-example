# Step 03

## 목표

* Jaeger를 활용한 Tracing 적용 ( spring cloud sleuth 사용)

## 준비

* 프로젝트 root 폴더에서 consul
  실행(`consul agent -server -bootstrap -ui -client=0.0.0.0 -bind 127.0.0.1 -data-dir ./consul/data -config-dir=./consul/conf`)
* docker-compose 실행(Jaeger)
  * `docker/msa-env/docker-compose.yml`

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

* 기본 설정

```yaml
spring:
  zipkin:
    base-url: http://localhost:9411 # 기본 값이며, Jaeger Collector 주소
```

* Service Discovery 를 사용

```yaml
spring:
  zipkin:
    base-url: http://jaeger-collector # Consul 에 등록된 service Id 
    discovery-client-enabled: true #discovery 기능 활설화
```

### 확인 방법

* config server, backend-hello-service , gateway 실행

#### 기본 Tracing

* `curl "http://localhost:8000/hello-service"` 실행

#### Tag 추가 예시

* Span 에 Tag를 추가할 수 있는 방법은 크게 manual 코드 작성 및 annotaion 기반으로 할 수 있습니다.
* manual 코드
  * `Tracer` 를 통해서 Tag를 수동으로 추가 할 수 있습니다.

```java
public String helloTag(String param){
        tracer.currentSpan().tag("user.name",param);

        return"Hello, My name is "+param;
        }
```

* annotation 기반

```java
@ContinueSpan //현재 span에 적용
public String helloTagAnnotation(@SpanTag(key = "user.name") String param){
        return"Hello, My name is "+param;
        }

@NewSpan //새로운 Span 생성
public String helloTagAnnotationNew(@SpanTag(key = "user.name") String param){
        return"Hello, My name is "+param;
        }
```

* curl 실습 `curl "http://127.0.0.1:8000/hello-service/tag-manual"`
* curl 실습 `curl "http://127.0.0.1:8000/hello-service/tag-annotation"`

#### Async 예시

* curl 실습 `curl "http://127.0.0.1:8000/hello-service/async"`

#### Remote Request 예시

* curl 실습 `curl "http://127.0.0.1:8000/hello-service/proxy"`

#### Remote Baggage 예시

* Baggage?
  * 분산 추적 환경에서 서비스 내부 및 전체에서 필드를 보유(전파)할 수 있는데 이런 필드를 Baggage 라고 합니다.
  * 이는 Tracing Context 내에 특정 필드 값을 전달하여 보다 추적에 용이할 수 있게 해 줍니다.
  * tracingId 값이 이 부분에 해당이 됩니다.
  * 전파할 때에는 Http Header 에 값이 추가 되어 전파 됩니다.
  * 주의점 너무 많은 Baggage 를 추가하게 되면 성능적으로 영향을 주게 됩니다.
* Http Header 에 `X-Client-Id`란 값으로 특정 클라이언트 고유 값이 요청된다고 가정합니다.
* Gateway 에서 `X-Client-Id` header 값을 추출하여 Tracing Context 내에 Baggage를 추가합니다.
  * `CustomGlobalFilter` 코드 참고
* spring 설정에 관련 내용 추가

```yaml
spring:
  sleuth:
    baggage:
      remote-fields:
        - client-id
      tag-fields:
        - client-id
```

* curl 실습 `curl "http://127.0.0.1:8000/hello-service/proxy" -H "X-Client-Id: 12cdddd"`

## 참고 사항

* spring boot actuator 의 endpoint 중 `traces` 를 활성화 하여 기본 값인 최대 10000 개까진 trace 정보 조회 가능
* `management.endpoint.traces.capacity` 를 통해 최대 사이즈 지정 가능
