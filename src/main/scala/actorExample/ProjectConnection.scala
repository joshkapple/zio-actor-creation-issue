package actorExample

import zio.ZIO
import zio.actors.ActorRef
import zio.http.ChannelEvent.{ExceptionCaught, Read, UserEvent, UserEventTriggered}
import zio.http.{Handler, SocketApp, WebSocketFrame}

case class ProjectConnection(actor: ActorRef[ActorProtocol]) {

  val socketHandler: SocketApp[Any] = Handler.webSocket { channel => {
    channel.receiveAll {
      case Read(WebSocketFrame.Text("actor")) => for {
        _ <- zio.Console.printLine("actor message!")
        path <- actor.path
        _ <- zio.Console.printLine(s"actor $path $actor")
        _ <- actor ! Message1
      } yield ()

      case Read(WebSocketFrame.Text(s)) => channel.send(Read(WebSocketFrame.Text(s)))

      case UserEventTriggered(UserEvent.HandshakeComplete) =>
        channel.send(Read(WebSocketFrame.text("Greetings!")))

      // Print the exception if it's not a normal close
      case ExceptionCaught(cause) =>
        zio.Console.printLine(s"Channel error!: ${cause.getMessage}")
      case _ => ZIO.unit
    }
  }
  }
}
