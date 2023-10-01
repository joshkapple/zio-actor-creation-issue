package actorExample

import actorExample.App.actorProtocol
import zio.{ZIO, ZLayer}
import zio.actors.{ActorRef, ActorSystem, Supervisor}

case class ActorCollection(actors: List[ActorRef[ActorProtocol]])

object ActorCollection {

  val layer = ZLayer {
    for {actorSystem <- ZIO.service[ActorSystem]
         _ <- zio.Console.printLine("creating actors")
         actor <- actorSystem.make("actorOne", Supervisor.none, (), actorProtocol)
         actor2 <- actorSystem.make("actorTwo", Supervisor.none, (), actorProtocol)
         actor3 <- actorSystem.make("actorThree", Supervisor.none, (), actorProtocol)
      //_ <- actor ! Message1
         } yield {
      new ActorCollection(actors = List(actor, actor2, actor3))
    }
  }
}
