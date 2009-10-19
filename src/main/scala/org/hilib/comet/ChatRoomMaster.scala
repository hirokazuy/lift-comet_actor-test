package org.hilib.comet

import scala.actors.Actor
import scala.actors.Actor._
import java.util.Date

case class JoinRoom(user: ChatUser)
case class DropRoom(user: ChatUser)
case class TimeOut
case class NewMessage(user:String, time:Date, message:String)

class ChatRoomMaster extends Actor {

    var chatUsers:List[ChatUser] = Nil

    def act = {
        loop {
            react {
                case JoinRoom(user) =>
                    chatUsers ::= user
                case DropRoom(user) =>
                    chatUsers -= user
                case NewMessage(u, t, m) =>
                    chatUsers.map(cu => cu ! NewMessage(u, t, m))
            }
        }
    }
}

object ChatRoomMaster extends ChatRoomMaster
