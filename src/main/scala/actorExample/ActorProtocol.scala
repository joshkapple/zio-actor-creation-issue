package actorExample

trait ActorProtocol[+_]

case object Message1 extends ActorProtocol[Unit]

case object Message2 extends ActorProtocol[Unit]

case object Message3 extends ActorProtocol[Unit]
