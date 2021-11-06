import org.junit.Assert
import org.junit.Test

open class FirstClassTest {

    @Test
    fun test1() {
        Assert.assertEquals("Hello world!", FirstClass().helloWorld())
    }



}