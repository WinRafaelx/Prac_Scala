import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._ // Fix for the `~` operator
import routes.{UserRoutes, BookRoutes}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem[Nothing](Behaviors.empty, "AkkaHttpApi")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val routes: Route = UserRoutes.routes ~ BookRoutes.routes // Ensure `routes` is used

    val binding = Http().newServerAt("localhost", 8080).bind(routes)

    println("Server online at http://localhost:8080/")
    StdIn.readLine()
    binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}
