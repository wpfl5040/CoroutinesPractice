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