package actorExample

import zio.http._
import zio._
import zio.actors.Actor.Stateful
import zio.actors.{ActorSystem, Supervisor}
import zio.actors._

object App extends ZIOAppDefault {

  val actorProtocol = new Stateful[Any, Unit, ActorProtocol] {
    override def receive[A](
                             state: Unit,
                             msg: ActorProtocol[A],
                             context: Context
                           ): RIO[Any, (Unit, A)] =
      msg match {
        case Message1 =>
          for {
            _ <- zio.Console.printLine("message 1")
          } yield ((), ())

        case Message2 =>
          for {
            _ <- zio.Console.printLine("message 2")
          } yield ((), ())

        case Message3 =>
          for {
            _ <- zio.Console.printLine("message 3")
          } yield ((), ())
      }
  }

  val app: App[ActorCollection with  ActorSystem] =
    Http.collectZIO[Request] {

      /**
       * Connecting to this path and sending 'actor' as websocket message results in:
       *
       * actor message!
       * actor zio://primaryActorSystem@0.0.0.0:0000/actorOne zio.actors.ActorRefLocal@6cce9d02
       * message 1
       *
       */
      case Method.GET -> Root / "working" => for {
        actorCollection <- ZIO.service[ActorCollection]
        pc = ProjectConnection(actorCollection.actors.head)
        response <- pc.socketHandler.toResponse
      } yield {
        response
      }


      /**
       * Connecting to this path and sending 'actor' as websocket message doesn't appear to
       * invoke the actor and `message 1` never prints.
       *
       * actor message!
       * actor zio://primaryActorSystem@0.0.0.0:0000/actorFour zio.actors.ActorRefLocal@4ff88860
       *
       */
      case Method.GET -> Root / "not-working" => for {
        actorSystem <- ZIO.service[ActorSystem]
        //actorCollection <- ZIO.service[ActorCollection]
        actor <- actorSystem.make("actorFour", Supervisor.none, (), actorProtocol).orDie
        _ <- actor.path.flatMap(p => zio.Console.printLine(p)).ignore
        pc = ProjectConnection(actor)
        response <- pc.socketHandler.toResponse
      } yield response
    }

  override val run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = Server.serve(app).provide(Server.default, PrimaryActorSystem.layer, ActorCollection.layer)
}