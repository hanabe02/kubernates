# 쿠보네티스
docker 가 실행하는 여러개의 서버를 관리하는 도구
오케스트레이션 도구 라고 부른다 (여러개의 도커 서버에게 지시를 내려준다.)

개발자가 -> (명령을 내리면/원하는 상태를 문서로 정리) -> 쿠보네티스가 알아서 도커들을 컨테이너들을 관리해준다.

1. 컨테이너를 사용하면 한 대의 서버에서 여러 개의 소프트웨어를 안전하게 효율적으로 운영할 수 있다.
2. 도커는 컨테이너를 관리하기 위한 도구로 일종의 프로그램
3. 쿠버네티스는 서버가 여러 대 있는 환경에서 각각의 서버의 도커에게 대신 지시해주는 오케스트레이션 도구이다.

# 쿠보네티스 (nginx) 파드로 뛰어보는 방법
  1. docker 데스크탑 설치
  2. setting 에서 kubernetes 버전 설치
  3. 프로젝트 생성
  4. yaml 파일 생성 (git 참고)
  5. kubectl apply -f nginx-pod.yaml
  6. kubectl get pods (pods 확인)
  7. tip 매니패스트 파일(Mainfest File): 다양한 리소스 파일(pods, 서비스 등..) 리소스를 생성하고 관리하기 위해 사용하는 파일이다.
  8. localhost:80 으로 요청을 보내도 응답이 없는 것을 확인 할 수 있다.
  9. tip 이는 도커에 대해서 공부했을 때는 컨테이너 내부와 컨테이너 외부의 네트워크가 서로 독립적으로 분리되어 있는데, 쿠버네티스에서는 파드 내부의 네트워크를 컨테이너가 공유해서 같이 사용한다.
  10. 때문에 pod 의 네트워크는 로컬 컴퓨터의 네트워크와는 독립적으로 분리되어 있어, 아무리 로컬에서 요청을 보내도 파드로 뛰운 Nginx 에서는 응답이 없던 것이다.
  11. 접속할 수 있는 2가지 방법
    1-1) 파드 내부로 들어가서 접근하기    
    1-2) kubectl exec -it nginx-pod -- bash    
    1-3) 파드 안으로 접근을 하게 된다.  
    1-4) curl localhost:80 정상적으로 잘 날아오는지 확인  
       이렇게 해주는 이유는 파드 내부에서는 네트워크가 분리되어 있지 않기 때문에 요청에 대한 응답이 날아온다.
      
      2-1) 파드의 내부 네트워크를 외부에서도 접속할 수 있도록 포트 포워딩(포트 연결시키기) 활용
      2-2) 파드에서 나가기 exit 실행
      2-3) kubectl port-forward pod/nginx-pod 80:80
       어떻게 되는 걸까?
        로컬에서 요청을 보냈고 [로컬 컴퓨터] [파드] 사이에 포트를 뚫어서 컨테이너에 요청을 했고 응답을 받게 된 것이다.
  14. kubectl delete pod nginx-pod 파드 삭제
  15. kubectl get pods 삭제 되었는지 확인

# 쿠보네티스 Spring boot 서버 파드로 뛰우기
  1. spring.io 에서 demo 파일 하나 생성, spring web, spring devel tool 2개 추가
  2. AppController 생성(git 참고)
  3. spring boot 실행
  4. error 시 8080 포트 사용 중 인거임
  5. Dockerfile 생성
  6. gradlew clean build
  7. build 파일이 생성이 된다. build/libs 부분 안에
  8. docker build -t spring-server . 도커 이미지 생성
  9. docker image ls (이미지 만들어 졌는지 확인)
  10. spring-pod.yaml 파일 생성
  11. 이 파일을 참고해서 pod 생성
  12. kubectl apply -f spring-pod.yaml 생성
  13. kubectl get pods 확인 error 가 발생했는지 확인을 할 수가 있다.
  14. 이미지 build 잘 했고, 이미지 있는지 확인을 했는데 왜 오류가 났는지 설명해줘
  15. 이걸 알기 위해서는 이미지 풀 정책(image pull policy) 을 알아야 한다.
      1 Always : 로컬에서 이미지를 가져오지 않고, 무조건 레지스토리(= Dockerhub, ECR 과 같은 원격 이미지 저장소)에서 가져온다.  
      2 IfNotPresent : 로컬에서 이미지를 먼저 가져온다. 만약 로컬에 이미작 없는 경우에만 레지스토리에서 가져온다.  
      3 Never : 로컬에서만 이미지를 가져온다.
  16. 때문에 yaml 파일에서 설정을 따로 해주어야 한다.
   ** 그래서 정리하자면 **
      오류가 난 이유는 latest 태그 이거나, 명시되지 않은 경우 imagePullPolicy는 Always로 설정이 된다.  
      이 때문에 우리는 오류가 발생한 것이다. 로컬에서 이미지를 가져오지 않기 때문에
  17. kubectl delete pod spring-pod 기존에 있는 이미지 삭제
  18. kubectl apply -f spring-pod.yaml 다시 생성
  19. 정확하게 이미지가 생성이 되었는지 확인을 하기 위해서는
  20. 1번째 방법 kubectl exec -it spring-pod -- bash 파드 안에 들어가서 거기서 직접 확인해보는 것
  21. curl localhost:8080 응답확인
  22. 정상적으로 hellow worldbash 가 뜨는 것을 확인 할 수 있다.
  23. 그리고 2번쨰 방법인 파드포워드 방식
  24. kubectl port-forward pod/spring-pod 12345:8080
  25. 12345(로컬) 8080(파드)





