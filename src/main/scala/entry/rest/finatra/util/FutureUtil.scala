package entry.rest.finatra.util
import com.twitter.util.Promise
import com.twitter.util.{Future => TwitterFuture}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scala.concurrent.{Future => ScalaFuture}

object FutureUtil {

  implicit class ToTwitterFuture[T](future: ScalaFuture[T]) {

    def toTwitter()(implicit ec: ExecutionContext): TwitterFuture[T] = {
      val twitterPromise = Promise[T]()
      future onComplete {
        case Failure(exception) => twitterPromise setException exception
        case Success(value) => twitterPromise setValue value
      }
      twitterPromise
    }
  }
}