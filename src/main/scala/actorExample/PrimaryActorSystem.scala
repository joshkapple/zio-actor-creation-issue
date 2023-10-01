package actorExample

import zio.ZLayer
import zio.actors.ActorSystem

object PrimaryActorSystem {
  val layer = ZLayer {
    for {actorSystem <- ActorSystem("primaryActorSystem")} yield actorSystem
  }
}
