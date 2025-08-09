FROM openjdk:17-jdk

COPY build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

# spring boot 프로젝트에서 build 에서 나온 파일이 jar 파일인데
# 이것을 도커 이미지로 만들기 위해 이렇게 작성을 한 것 이다.