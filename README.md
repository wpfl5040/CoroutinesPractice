# CoroutinesPractice
  코루틴 연습
  
# chap01
 - ## Dispatchers
   - https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/index.html
   - Dispatchers.Default : CPU 사용량이 많은 작업에 사용합니다. 주 스레드에서 작업하기에는 너무 긴 작업 들에게 알맞습니다.
   - Dispatchers.IO : 네트워크, 디스크 사용 할때 사용합니다. 파일 읽고, 쓰고, 소켓을 읽고, 쓰고 작업을 멈추는것에 최적화되어 있습니다.
   - Dispatchers.Main : 안드로이드의 경우 UI 스레드를 사용합니다.
   
 - ## launch(), async()
   - launch는 job객체를 반환
   - async는 deferred 객체를 이용해 제어가 가능하며 동시에 코루틴 블록에서 계산된 결과값을 받을수 있다.
   
 - ## lazy연산
   - start() , join()함수를 호출하는 시점에서 코루틴 블록 실행
 ~~~
    scope.launch(start = CoroutineStart.LAZY) { }
    scope.async(start = CoroutineStart.LAZY) { }
 ~~~
 - ## wait
   - launch() : join() 함수로 기다림
   - async() : await() 함수로 기다림
   - runBlocking() : 아무런 추가 함수 호출 없이 해당 블록이 완료 하기를 기다림
   - joinAll() : 여러개의 코루틴(job)객 기다리기
   - awaitAll() : 여러개의 코루틴(deferred)객체 기다리기
   
  - ## Cancel
    - cancel() : job 취소
    - yield() : 해당 위치에서 코루틴을 일시 중단
    - isActive 프로퍼티 : 코루틴 블록이 아직 취소 되지 않았는지 상태를 확인
    - try{} finally{} : try-finally 문으로 코루틴 블록을 감싸면, 작업을 종료할때 finally의 블록의 코드가 실행
    - finally에서 delay()함수를 사용해서 일정 시간 대기 해야 할경우 withContext() 함수를 사용
    - withContext(NonCancellable) : withContext 함수 내의 코루틴 블록은 취소되지 않는다
    - withTimeout(mill) : Timeout동작을 간단히 처리할수 있는 함수 - 작업 취소시 TimeoutCancellationException 발생
    - withTimeoutOrNull() : 시간내 정상 종료 시 값을 반환할수도 있고, 만약 시간내 처리되지 못한경우 Exception이 발생하지않고 null 반환
    
# chap02
 - ## Flow
   - Flow 타입을 생성은flow {} 빌더를 이용
   - flow { ... } 블록 안의 코드는 중단 가능
   - foo() 함수는 더이상 suspend 로 마킹 되지 않음
   - 결과 값들은 flow 에서 emit() 함수를 이용하여 방출됨
   - flow 에서 방출된 값들은 collect 함수를 이용하여 수집됨  
   
 - ## Flow Builders
   - flowof{ } : 고정된 값들을 방출하는 플로우
   - .asFlow() : 다양한 컬렉션들과 시퀀스들은 확장함수를 통해 플로우로 변환
   
 - ## 플로우 중간 연산자(Intermediate flow operators)
   - 플로우는 컬렉션이나 시퀀스와 같이 연산자로 변환될 수 있다.
   - 중간 연산자는 업스트림 플로우에 적용되어 다운스트림 플로우를 반환
   - 중단 함수 호출 가능
   
 - ## 변환 연산자 (Transform operator)
   - .transform() : map이나 filter 같은 단순한 변환이나 혹은 복잡한 다른 변환들을 구현하기 위해 사용
   - 임의의 횟수로 임의의 값들을 방출 가능
   - 오래걸리는 비동기 요청을 수행 하기전에 기본 문자열을 먼저 방출하고 요청에 대한 응답이 도착하면 그 결과를 방출할 수 있음
   
 - ## 크기 제한 연산자 (Size-limiting operators)
   - take 같은 크기 제한 중간 연산자는 정의된 제한치에 도달하면 실행을 취소
   - 코루틴에서 취소는 예외를 발생시키는 방식으로 수행 이를통해 try{ } finally{ }로 관리 가능
   
 - ## 플로우 종단 연산자 (Terminal flow operators)
   - 플로우 수집을 시작하는 중단 함수 ex) collect
   
 - ## 플로우 컨텍스트 (Flow context)
   - 플로우의 수집은 항상 호출한 코루틴의 컨텍스트 안에서 수행
   
 - ## flowOn 연산자
   - 플로우에서 context를 변경하기 위해 사용
 
 - ## 버퍼링 (Buffering)
   - buffer 연산자를 사용함으로서 방출 코드가 수집 코드와 동시에 수행 가능
   
 - ## 병합 (conflation)
   - 어떤 플로우가 연산의 일부분이나 연산 상태의 업데이트를 방출하는 경우 방출되는 각각의 값을 처리하는 것은 불필요
   - conflate 연산자를 사용하여 수집기의 처리가 너무 느릴 경우 방출 된 중간 값들을 스킵해 처리속도를 높임
   - collectLatest : 새로운 값이 방출될 때마다 느린 수집기를 취소하고 재시작 하는 연산자
   
 - ## Zip
   - 두개의 플로우들의 값들을 병합하는 연산자
   
 - ## Combine
   - 어떤 플로우가 어떤 연산이나 최근 값을 나타낼 때, 그 플로우의 최근 값에 추가 연산을 수행하거나 또는 별도의 업스트림 플로우가 값을 방출할 때마다 다시 그 추가 연산을 수행해야 할 경우 사용
   
 - ## 수집기의 예외처리
   - try catch 블록을 사용
   
 - ## 플로우 종료 (Flow completion)
   - Imperative / Declarative 두가지 방식으로 가능
   - try/catch에 추가적으로 수집 종료 시 실행할 코드를 finally 블록을 통해 정의 가능
   - .onCompletion { } 중간 연산자를 추가해서 플로우가 완전히 수집되었을 때 실행 될 로직을 정의 가능
   - onCompletion연산자도 업 스트림에서 전달되는 예외만 식별하고 처리할 수 있으며 다운 스트림의 예외는 알지 못함
   
# 참고자료 
  - https://github.com/Kotlin/kotlinx.coroutines/blob/master/coroutines-guide.md
  - https://medium.com/@limgyumin/%EC%BD%94%ED%8B%80%EB%A6%B0-%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%A0%9C%EC%96%B4-5132380dad7f
  - https://charko.tistory.com/17
  - https://medium.com/@myungpyo/%EC%BD%94%EB%A3%A8%ED%8B%B4-%EA%B3%B5%EC%8B%9D-%EA%B0%80%EC%9D%B4%EB%93%9C-%EC%9E%90%EC%84%B8%ED%9E%88-%EC%9D%BD%EA%B8%B0-part-9-a-d0082d9f3b89
  