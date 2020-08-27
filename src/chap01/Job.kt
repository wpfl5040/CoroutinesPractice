package chap01

import kotlinx.coroutines.*

suspend fun main(){

    job()
    multiJob()
    controlJob()

}


suspend fun job(){
    val job : Job = CoroutineScope(Dispatchers.IO).launch {
        var i = 0
        while (i < 10) {
            delay(500)
            i++
            print(i)
        }
    }

    job.join() // 완료 대기
    job.cancel() // 취소
}

suspend fun multiJob(){
    val job1 = CoroutineScope(Dispatchers.IO).launch {
        var i = 0
        while (i < 10) {
            delay(500)
            i++
            print("job1 : $i")
        }
    }

    val job2 = CoroutineScope(Dispatchers.IO).launch {
        var i = 0
        while (i < 10) {
            delay(500)
            i++
            print("job2 : $i")
        }
    }

//    job1.join()
//    job2.join()
    //작업 기다리기
    joinAll(job1,job2)
}

suspend fun controlJob(){
    val job1 = CoroutineScope(Dispatchers.IO).launch {
        var i = 0
        while (i < 10) {
            delay(500)
            i++
            println("job1 : $i")
        }
    }

    //위 블록 과 같은 job1 객체를 사용
    CoroutineScope(Dispatchers.IO).launch(job1){
        var i = 0
        while (i < 10) {
            delay(1000)
            i++
            print("job2 : $i")
        }
    }

    //같은 job객체를 사용하게 되면
    //joinAll(job1, job2)와 같다
    job1.join()

}

suspend fun lazyJob(){
    // CoroutineStart.LAZY : 해당 코루틴 블록 지연
    val job = ioScope.launch(start = CoroutineStart.LAZY) {

    }
    //start() , join()함수를 호출하는 시점에서 launch 블록 실행
    job.start()
}