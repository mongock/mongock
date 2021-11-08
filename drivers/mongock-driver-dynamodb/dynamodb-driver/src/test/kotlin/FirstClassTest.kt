import org.junit.Assert
import org.junit.Test

open class FirstClassTest {

    @Test
    fun test1() {
        val transactionRequest:List<String>? = null
        val size = transactionRequest?.size
        if(size == 0) {
            println("equal zero")
        } else {
            println("NOT equal zero")
        }
    }



}