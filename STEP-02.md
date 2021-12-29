# Step 02

## 목표

* 보안을 적용을 해 보도록 하자.
* 설정 파일에 있는 기밀 데이터(db password, secret key ...) 보안 적용
* http 요청에 대한 보안 적용

## 과정

* branch `step-02`

### 설정 파일의 기밀 데이터 보안 적용

* `config server` 의 `application.yml`에 아래 내용을 추가 후 실행해 보자. 그리고 데이터 암/복호화를 진행 해 보자

```yml
encrypt:
  key: 1234567890
```

```bash
$ curl -X POST http://127.0.0.1:8200/encrypt -s -d nomimic-encrypt
191b825bb152e4f31651ff950b7c09ce1a766d9989f56f0becc30321b32ee2c0
$ curl -X POST http://127.0.0.1:8200/decrypt -s -d 191b825bb152e4f31651ff950b7c09ce1a766d9989f56f0becc30321b32ee2c0
nomimic-encrypt
```

* `backend-heelo-service`에 위에서 암호화한 결과값을 설정파일에 입력해 보도록 하자.

```bash
$ cd $HOME/config-repo
$ echo "hello-service.name: '{cipher}191b825bb152e4f31651ff950b7c09ce1a766d9989f56f0becc30321b32ee2c0'" > hello-service-encrypted.yml 
$ git add -A .
$ git commit -m "Add hello-service-encrypted.yml"
```

* config server를 통해 좀 전에 등록한 파일 내용을 조회 해 보자. 결과 값은 복호화 해서 내려주는 것이 보일 것이다.

```bash
$ curl http://localhost:8200/hello-service-encrypted.yml
hello-service:
  name: nomimic-encrypt
```

### HTTP 보안 적용 - config server

* 바로 전 단계를 통해 저장소에 있는 파일 내용에 대한 기밀 데이터 보안 적용해 보았다.
* config server 는 http 로 설정 값을 복호화해서 제공해 주기 때문에 http에 대한 보안 적용이 필요하다.
* `config server` 에 `spring-boot-starter-security` 의존성 추가 후 `application.yml`에 아래 내용을 추가 후 실행해 보자.

```yml
spring:
  security:
    user:
      name: user
      password: 1234
```

* `curl http://127.0.0.1:8200/hello-service/local -v` 요청시 http status 401 로 응답이 되는 것을 확인 할 수 있다.
* `curl -u user:1234 http://127.0.0.1:8200/hello-service/local -v` 로 요청시 http status 200 으로 응답이 되는 것을 확인 할 수 있다. 아주 간단한
  보안 설정이 적용 된 것이다.

* http 보안이 적용된 상태 에서는 `backend-hello-service`가 실행이 안될 것이다.
    * 오류 발생`Could not resolve placeholder 'hello-service.name' in value "${hello-service.name}"`

* `backend-hello-service`의 `application.yml`에 아래 내용을 추가 해 보고 실행 해 보자. 그럼 실행이 되는 것을 확인 할 수 있다.

```yml
spring:
  cloud:
    config:
      username: user # config server 에 설정한 유저명
      password: 1234 # config server 에 설정한 비밀번호
```

* 다시 `config server`에 암호화 요청이 해 보도록 하자. http basic auth 가 적용 되었으니 아래와 같이 실행 해보자. 하지만 `401` 인증 오류가 발생할 것이다.
    * spring security 에서 csrf 옵션이 기본적으로 적용되어 있는데 이 부분을 해제하면 정상적으로 통신할 수 있다.

```bash
$ curl -u user:1234 -X POST http://127.0.0.1:8200/encrypt -s -d nomimic-encrypt -v
```

```java

@Configuration
public class HttpSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf().disable();
    }
}
```