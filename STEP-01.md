# Step 01

## 준비

* OS : Mac
* consul : version 1.11.1
* consul 실행

```bash
consul agent -server -bootstrap -ui -client=0.0.0.0 -data-dir ${CUSTOM_CONSOL_DATA_DIR}
ex) consul agent -server -bootstrap -ui -client=0.0.0.0 -bind 127.0.0.1 -data-dir ./consul/data
```

## Config Server

### config git 저장소 준비

```bash
$ cd $HOME
$ mkdir config-repo
$ cd config-repo
$ git init -b main .
$ echo "hello-service.name: nomimic" > hello-service.yml 
$ git add -A .
$ git commit -m "Add hello-service.yml"
$ echo "hello-service.name: nomimic-local" > hello-service-local.yml 
$ git add -A .
$ git commit -m "Add hello-service-local.yml"
```

### 동작 확인

* 고 가용성을 위해 config server 는 2개 이상이 실행되어야 할 것이다. 예제 코드에서는 아래처럼 총 2개의 config server 를 실행 할 수 있다.

```bash
./mvnw -pl config-server spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=local1"
./mvnw -pl config-server spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=local2"
```

* 아래와 같이 실행하고 consul ui (localhost:8500)에 접속해서 service 목록을 확인 해 보자

```bash
./mvnw -pl config-server spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=local1"
```

* consul에는 `spring.application.name`의 값이 기본적으로 등록이 되는데 `management.server.port`값이 `server.port` 값과 다른 경우 아래처럼 2개의 서비스로
  등록이 되는 것을 확인 할 수 있다.
  ![스크린샷 2021-12-23 오후 6.25.36.png](/files/3170453872215138367)

* 총 2개의 config server를 실행하면 아래처럼 consul ui의 화면이 변경된다. instances 값이 1 에서 2로 변경된 것을 확인 할 수 있다.

* consul ui에서 서비스 상세 페이지에서 보면 서비스 포트는 8201 포트이지만 health check는 `management.server.port`를 통해서 확인하는 것을 확인 할 수 있다.

* `/actuator/health`가 spring cloud 에서는 기본값이지만 `spring.cloud.consul.discovery.health-check-path`값을 지정해 주는 것으로 변경이 가능하다.
* 서비스 중에 graceful 하게 배포를 해야 되는데 actuator 의 health endpoint 에서 임의로 http status 200이 아닌 값을 내려주도록 custom 할 수도 있고 아니면 별도
  endpoint를 만들어서 서비스에서 제외 시킬 수 있다.

## Hello Service Application

* discovery 기능을 사용하여 config server 와 연동하는 주요 설정 값

```yml
spring:
  config:
    import: "optional:configserver:" # config server url 을 지정하는 방식이지만 discovery 기능을 사용함으로 별도로 지정하지 않음.
  cloud:
    consul:
      host: localhost
      port: 8500
    config:
      discovery:
        enabled: true # 기본값은 false 이고 true 인 경우 discovery 기능을 사용하여 config server 와 연동
        service-id: cloud-config-server # consul에 등록한 config server id
```

* Config Server 기동 후 profile을 다르게 적용하여 실행하면 아래와 같은 결과를 볼 수 있다.

```bash
$ ./mvnw -pl backend-hello-service spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=default"
$ curl http://127.0.0.1:8100
Hello, My name is nomimic% # 응답 값                                                                                               

$ ./mvnw -pl backend-hello-service spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=local"
$ curl http://127.0.0.1:8100
Hello, My name is nomimic-local% # 응답 값
```

* `spring.cloud.config.label` 값을 통해 특정 설정값을 적용할 수 있다. commit id, brance name, tag 값을 지정해서 실행하게 되면 main 브랜치의 최신 값이 아닌 다른
  값을 적용된다.

## Gateway

### 동작 확인

* hello service 를 2개 실행

```bash
$ ./mvnw -pl backend-hello-service spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=default"
$ ./mvnw -pl backend-hello-service spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=local1"
```

* `curl http://localhost:8000/hello-service` 로 요청 하여 gateway 통해 분배 처리 되고 있는지 확인 해보자.
* hello service application log 를 통해 분배 처리 되고 있는 것을 확인 할 수 있을 것이다.
