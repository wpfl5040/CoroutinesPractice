package chap02

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.naming.Context
import kotlin.system.measureTimeMillis

/*
    Flow(플로우)
    Flow 타입을 생성은flow {} 빌더를 이용
    flow { ... } 블록 안의 코드는 중단 가능
    foo() 함수는 더이상 suspend 로 마킹 되지 않음
    결과 값들은 flow 에서 emit() 함수를 이용하여 방출됨
    flow 에서 방출된 값들은 collect 함수를 이용하여 수집됨
 */

suspend fun main(){
//    flow()
//    println()
//    flowCancel()
//    println()
//    flowBuilder()
//    println()
//    intermediateFlow()
//    println()
//    transFromOperator()
//    println()
//    sizeLimitingOperator()
//    println()
//    terminalOperator()
//    println()
//    flowOnOperTest()
//    println()
//    bufferOperator()
//    println()
//    conflationOperator()
//    println()
//    zipOperator()
//    println()
//    combineOperator()
//    println()
    println()
    exceptionCollector()
}

fun foo(): Flow<Int> = flow { // flow builder
    for (i in 1..3) {
        delay(100) // pretend we are doing something useful here
        emit(i) // emit next value
    }
}

fun flow() = runBlocking<Unit> {
    // Launch a concurrent coroutine to check if the main thread is blocked
    launch {
        for (k in 1..3) {
            println("I'm not blocked $k")
            delay(100)
        }
    }
    // Collect the flow
    foo().collect { value -> println(value) }
}


/*
    플로우의 취소(Flow Cancellation)
 */

fun foo2(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        println("Emitting $i")
        emit(i)
    }
}

fun flowCancel() = runBlocking<Unit> {
    withTimeoutOrNull(250) { // Timeout after 250ms
        foo2().collect { value -> println(value) }
    }
    println("Done")
}


/*
 - ## Flow Builders
   - flowof{ } : 고정된 값들을 방출하는 플로우
   - .asFlow() : 다양한 컬렉션들과 시퀀스들은 확장함수를 통해 플로우로 변환
 */
suspend fun flowBuilder() {
    (1..3).asFlow().collect { value -> println(value) }
}

/*
 - ## 플로우 중간 연산자(Intermediate flow operators)
   - 플로우는 컬렉션이나 시퀀스와 같이 연산자로 변환될 수 있다.
   - 중간 연산자는 업스트림 플로우에 적용되어 다운스트림 플로우를 반환
   - 중단 함수 호출 가능
 */

suspend fun performRequest(request: Int) : String {
    delay(1000) // imitate long-running asynchronous work
    return "response $request"
}

fun intermediateFlow() = runBlocking {
    (1..3).asFlow()
            .map { request -> performRequest(request) }
            .collect { response -> println(response) }
}

/*
 - ## 변환 연산자 (Transform operator)
   - .transform() : map이나 filter 같은 단순한 변환이나 혹은 복잡한 다른 변환들을 구현하기 위해 사용
   - 임의의 횟수로 임의의 값들을 방출 가능
   - 오래걸리는 비동기 요청을 수행 하기전에 기본 문자열을 먼저 방출하고 요청에 대한 응답이 도착하면 그 결과를 방출할 수 있음
 */
suspend fun transFromOperator(){
    (1..3).asFlow() // a flow of requests
            .transform { request ->
                emit("Making request $request")
                emit(performRequest(request))
            }
            .collect { response -> println(response) }
}

/*
 - ## 크기 제한 연산자 (Size-limiting operators)
   - take 같은 크기 제한 중간 연산자는 정의된 제한치에 도달하면 실행을 취소
   - 코루틴에서 취소는 예외를 발생시키는 방식으로 수행 이를통해 try{ } finally{ }로 관리 가능
 */
fun numbers(): Flow<Int> = flow {
    try {
        emit(1)
        emit(2)
        println("This line will not execute")
        emit(3)
    } finally {
        println("Finally in numbers")
    }
}

fun sizeLimitingOperator() = runBlocking<Unit> {
    numbers()
            .take(2) // take only the first two
            .collect { value -> println(value) }
}

/*
 - ## 플로우 종단 연산자 (Terminal flow operators)
   - 플로우 수집을 시작하는 중단 함수 ex) collect
 */
suspend fun terminalOperator() {
    val sum = (1..5).asFlow()
            .map { it * it }
            .reduce {a, b -> a+b}
    println(sum)
}

/*
 - ## 플로우 컨텍스트 (Flow context)
   - 플로우의 수집은 항상 호출한 코루틴의 컨텍스트 안에서 수행
 */

fun contextFlow() {
//    withContext(context) {
//        foo.collect { value ->
//            println(value) // run in the specified context
//        }
//    }
}

/*
 - ## flowOn 연산자
   - 플로우에서 context를 변경하기 위해 사용
 */

fun flowOnOperator(): Flow<Int> = flow {
    for (i in 1..3) {
        Thread.sleep(100) // pretend we are computing it in CPU-consuming way
        println("Emitting $i")
        emit(i) // emit next value
    }
}.flowOn(Dispatchers.Default) // RIGHT way to change context for CPU-consuming code in flow builder

fun flowOnOperTest() = runBlocking {
    flowOnOperator().collect { println("collected $it") }
}

/*
 - ## 버퍼링 (Buffering)
   - buffer 연산자를 사용함으로서 방출 코드가 수집 코드와 동시에 수행 가능
 */
fun bufferOperator(){
    runBlocking {
        val time = measureTimeMillis {
            foo().buffer() // buffer emissions, don't wait
                    .collect { value ->
                        delay(300) // pretend we are processing it for 300 ms
                        println(value)
                    }
        }
        println("Collected in $time ms")
    }
}

/*
 - ## 병합 (conflation)
   - 어떤 플로우가 연산의 일부분이나 연산 상태의 업데이트를 방출하는 경우 방출되는 각각의 값을 처리하는 것은 불필요
   - conflate 연산자를 사용하여 수집기의 처리가 너무 느릴 경우 방출 된 중간 값들을 스킵 가능
 */
fun conflationOperator(){
    runBlocking {
        val time = measureTimeMillis {
            foo()
                    .conflate() // conflate emissions, don't process each one
                    .collect { value ->
                        delay(300) // pretend we are processing it for 300 ms
                        println(value)
                    }
        }
        println("Collected in $time ms")
    }
}

/*
 - ## Zip
   - 두개의 플로우들의 값들을 병합하는 연산자
 */
suspend fun zipOperator(){
    val nums = (1..3).asFlow().onEach { delay(300) } // numbers 1..3 every 300 ms
    val strs = flowOf("one", "two", "three").onEach { delay(400) } // strings every 400 ms
    val startTime = System.currentTimeMillis() // remember the start time
    nums.zip(strs) { a, b -> "$a -> $b" } // compose a single string with "zip"
            .collect { value -> // collect and print
                println("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }
}

/*
 - ## Combine
   - 어떤 플로우가 어떤 연산이나 최근 값을 나타낼 때, 그 플로우의 최근 값에 추가 연산을 수행하거나 또는 별도의 업스트림 플로우가 값을 방출할 때마다 다시 그 추가 연산을 수행해야 할 경우 사용
 */
suspend fun combineOperator(){
    val nums = (1..3).asFlow().onEach { delay(300) } // numbers 1..3 every 300 ms
    val strs = flowOf("one", "two", "three").onEach { delay(400) } // strings every 400 ms
    val startTime = System.currentTimeMillis() // remember the start time
    nums.combine(strs) { a, b -> "$a -> $b" } // compose a single string with "combine"
            .collect { value -> // collect and print
                println("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }
}

/*
 - ## 수집기의 예외처리
   - try catch 블록을 사용
 */
fun exceptionCollector() = runBlocking {
    try {
        foo().collect { value ->
            println(value)
            check(value <= 1) { "Collected $value" }
        }
    } catch (e: Throwable) {
        println("Caught $e")
    }
}