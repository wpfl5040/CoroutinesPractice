package chap01

import kotlinx.coroutines.*

/*
    async
    deferred 객체를 이용해 제어가 가능하며 동시에 코루틴 블록에서 계산된 결과값을 받을수 있다.
 */
suspend fun main(){
    async()
    multiAsync()
    lazyAsync()
}


suspend fun async(){
    val deferred = ioScope.async {
        var i = 0
        while (i < 10) {
            delay(500)
            i++
            println("async() - deferred1 : $i")
        }
        "result"
    }
    //await() : 코루틴 블록이 완료 될때까지 다음코드 수행을 대기
    val msg = deferred.await()
    println("return : $msg")
}

suspend fun multiAsync(){
    val deferred1 = ioScope.async {
        var i = 0
        while (i < 10) {
            delay(500)
            i++
            println("deferred1 : $i")
        }
        "result1"
    }

    val deferred2 = ioScope.async {
        var i = 0
        while (i < 10) {
            delay(500)
            i++
            println("deferred1 : $i")
        }
        "result2"
    }

    //기다리기
    val result1 = deferred1.await()
    val result2 = deferred2.await()
//    awaitAll(deferred1, deferred2)

    println("return - multiAsync : $result1 , $result2")
}

suspend fun lazyAsync() {
    println("lazy - start")
    val deferred = ioScope.async(start = CoroutineStart.LAZY) {
        var i = 0
        while (i < 10) {
            delay(500)
            i++
            println("lazy - async : $i")
        }
    }
    //start() , join()함수를 호출하는 시점에서 launch 블록 실행
    //deferred.start()

    deferred.await()
    println("lazy - end")
}
