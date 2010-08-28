package textapp.server

import ccf.server.Server
import ccf.transport.{Event, TransportActor}
import scala.actors.Actor._

class TextAppTransportActor(onMessage: Event.Msg => Unit) extends TransportActor {
  start
  def act = loop { react {
    case msg: Event.Msg => onMessage(msg)
    case _ => 
  }}

  override def initialize(server: Server) {}
}
