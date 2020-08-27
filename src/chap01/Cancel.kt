package chap01

import kotlinx.coroutines.*
/*
    cancel() : job 취소
    yield() : 해당 위치에서 코루틴을 일시 중단
    isActive 프로퍼티 : 코루틴 블록이 아직 취소 되지 않았는지 상태를 확인
    try{} finally{} : try-finally 문으로 코루틴 블록을 감싸면, 작업을 종료할때 finally의 블록의 코드가 실행
    finally에서 delay()함수를 사용해서 일정 시간 대기 해야 할경우 withContext() 함수를 사용
    withContext(NonCancellable) : withContext 함수 내의 코루틴 블록은 취소되지 않는다
    withTimeout(mill) : Timeout동작을 간단히 처리할수 있는 함수 - 작업 취소시 TimeoutCancellationException 발생
    withTimeoutOrNull() : 시간내 정상 종료 시 값을 반환할수도 있고, 만약 시간내 처리되지 못한경우 Exception이 발생하지않고 null 반환
 */

fun main(){
    cancellingCoroutineExecution()
    cancellationIsCooperative()
    makingComputationCodeCancellableUsingYield()
    makingComputationCodeCancellable()
    closingResourcesWithFinally()
    runNonCancellableBlock()
}

fun cancellingCoroutineExecution() = runBlocking {
    val job = ioScope.launch {
        repeat(1000) { i ->
            println("job : I'm sleeping $i...")
            delay(500L)
        }
    }

    delay(1300L)
    println("main : I'm tired of waiting!")
    job.cancel()
    job.join()
    println("main: Now I can quit.")
}

//취소할수 없는 코드 조심!!
fun cancellationIsCooperative() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = ioScope.launch {
        var nextPrintTime = startTime
        var i = 0
        while (i < 10) {
            if(System.currentTimeMillis() >= nextPrintTime){
                println("job : I'm sleeping $i...")
                i++
                nextPrintTime += 500L
            }
        }
    }

    delay(1300L)
    println("main : I'm tired of waiting!")
    job.cancelAndJoin()
    println("main: Now I can quit.")
}

/*
    yield() : 해당 위치에서 코루틴을 일시 중단
 */
fun makingComputationCodeCancellableUsingYield() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = ioScope.launch {
        var nextPrintTime = startTime
        var i = 0
        while (i < 20) {
            yield()

            if(System.currentTimeMillis() >= nextPrintTime){
                println("job : I'm sleeping $i...")
                i++
                nextPrintTime += 500L
            }
        }
    }

    delay(1300L)
    println("main : I'm tired of waiting!")
    job.cancelAndJoin()
    println("main: Now I can quit.")
}


fun makingComputationCodeCancellable() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = ioScope.launch {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) {
            if(System.currentTimeMillis() >= nextPrintTime){
                println("job : I'm sleeping $i...")
                i++
                nextPrintTime += 500L
            }
        }
    }

    delay(1300L)
    println("main : I'm tired of waiting!")
    job.cancelAndJoin()
    println("main: Now I can quit.")
}

fun closingResourcesWithFinally() = runBlocking {
    val job = ioScope.launch {
        try {
            repeat(1000) { i ->
                println("job : I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            println("job: I'm running finally")
        }
    }

    delay(1300L)
    println("main : I'm tired of waiting!")
    job.cancelAndJoin()
    println("main: Now I can quit.")

}

fun runNonCancellableBlock() = runBlocking {
    val job = ioScope.launch {
        try {
            repeat(1000) { i ->
                println("job : I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
            withContext(NonCancellable){
                println("job: I'm running finally")
                delay(1000L)
                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
            }
        }
    }
    delay(1300L)
    println("main : I'm tired of waiting!")
    job.cancelAndJoin()
    println("main: Now I can quit.")
}
