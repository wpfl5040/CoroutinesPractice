package chap01

import kotlinx.coroutines.runBlocking

/*
    runBlocking() : 코드 블록이 작업을 완료 하기를 기다림
 */
fun main() {
    /*
        launch() - join() 함수로 기다림
        async()  - await() 함수로 기다림
        runBlocking() - 아무런 추가 함수 호출 없이 해당 블록이 완료 하기를 기다림
     */
    runBlocking {

    }
}