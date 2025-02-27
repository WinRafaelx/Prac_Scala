import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import routes.{UserRoutes, BookRoutes}
import db.DatabaseInitializer
import scala.concurrent.{ExecutionContextExecutor, Future, Await}
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Success, Failure, Try}

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem[Nothing](Behaviors.empty, "AkkaHttpApi")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val routes: Route = UserRoutes.routes ~ BookRoutes.routes

    println("Initializing database...")
    Try {
      // Wait for database initialization to complete
      Await.result(DatabaseInitializer.initDatabase(), 30.seconds)
    } match {
      case Success(_) =>
        println("Database initialized successfully")
        startServer(routes)
      case Failure(ex) =>
        println(s"Failed to initialize database: ${ex.getMessage}")
        ex.printStackTrace()
        system.terminate()
    }

    def startServer(routes: Route): Unit = {
      println("Starting HTTP server...")
      val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)
      
      bindingFuture.onComplete {
        case Success(binding) => 
          val address = binding.localAddress
          println(s"Server online at http://${address.getHostString}:${address.getPort}/")
        case Failure(ex) => 
          println(s"Failed to bind HTTP server: ${ex.getMessage}")
          system.terminate()
      }
      
      println("Press ENTER to stop the server...")
      StdIn.readLine()
      
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }
  }
}