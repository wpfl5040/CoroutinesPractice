package chap01

import kotlinx.coroutines.*

val ioScope = CoroutineScope(Dispatchers.IO)


fun main(){
    /*
                                                   - Dispatchers -
        https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/index.html
        Dispatchers.Default : CPU 사용량이 많은 작업에 사용합니다. 주 스레드에서 작업하기에는 너무 긴 작업 들에게 알맞습니다.
        Dispatchers.IO : 네트워크, 디스크 사용 할때 사용합니다. 파일 읽고, 쓰고, 소켓을 읽고, 쓰고 작업을 멈추는것에 최적화되어 있습니다.
        Dispatchers.Main : 안드로이드의 경우 UI 스레드를 사용합니다.
    */
    val scope = CoroutineScope(Dispatchers.IO)

    /*
        코루틴 실행 방법
    */
    scope.launch {
        // 포그라운드 작업
    }
    scope.launch(Dispatchers.Default) {
        // CoroutineContext 를 변경하여 백그라운드로 전환하여 작업을 처리합니다
    }
    CoroutineScope(Dispatchers.Default).launch {
        // 새로운 CoroutineScope 로 동작하는 백그라운드 작업
    }
    scope.launch(Dispatchers.Default) {
        // 기존 CoroutineScope 는 유지하되, 작업만 백그라운드로 처리
    }

    /*
        Job
     */
    //launch는 Job 객체를 반환
    val job = scope.launch {

        CoroutineScope(Dispatchers.Main).launch {
            // 외부 코루틴 블록이 취소 되어도 끝까지 수행됨
        }

    }
    // 외부 코루틴 블록을 취소
    job.cancel()

}