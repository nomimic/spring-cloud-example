# spring-cloud

## 준비

* OS : Mac
* JDK : 11+
* consul : version 1.11.1
* consul 실행

```bash
consul agent -server -bootstrap -ui -client=0.0.0.0 -data-dir ${CUSTOM_CONSOL_DATA_DIR}
ex) consul agent -server -bootstrap -ui -client=0.0.0.0 -data-dir ./consul/data
```

## 과정

### Step 01

* branch : step-01
* spring cloud 중 gateway 및 config server 에 대한 이해 및 동작 실습

### Step 02

* branch : step-02
* config 서버에 보안 적용

### Step 03

* branch : step-03
* Tracing 적용 (Jaeger 활용)
