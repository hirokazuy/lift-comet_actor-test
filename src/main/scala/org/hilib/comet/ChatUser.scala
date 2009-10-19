package org.hilib.comet

import java.util.Date

import net.liftweb._
import http._
import SHtml.ajaxText
import util._
import Helpers._
import js._
import js.JsCmds._
import js.jquery.JqJsCmds._

import org.hilib.model._

class ChatUser extends CometActor {
    override def defaultPrefix = Full("chat")

    ActorPing.schedule(this, TimeOut, 1000)

    def registTimer() = {
        ActorPing.schedule(this, TimeOut, 10000)
    }

    override def localSetup() = {
        ChatRoomMaster ! JoinRoom(this)
        super.localSetup()
    }

    override def localShutdown() = {
        ChatRoomMaster ! DropRoom(this)
        super.localShutdown()
    }

    def blankMessage = {<tr><th>user</th>
                            <th>date</th>
                            <th>message</th></tr>}

    def inputForm = {
        var user:User = User.currentUser.open_!

        def sendChatMaster(msg:String):JsCmd = {
            var date: Date = new Date()
            Log.info("send master: " + user.firstName)
            ChatRoomMaster ! NewMessage(user.firstName, new Date(), msg)
            DisplayMessage("msg_info", <p>sending</p>, 10, 3000)
        }

        <span>{ajaxText("input here",
                        msg => sendChatMaster(msg))}</span>
        <span id="msg_info"></span>
    }

    private val writeId = "chat_data"
    override def render = {
        bind("message" -> blankMessage,
             "inputForm" -> inputForm)
    }

    override def lowPriority = {
        case TimeOut =>
       	    Log.info("time out...")
            registTimer()

        case NewMessage(user, time, message) =>
            Log.info("message from: " + user)
            drawMessage(user, time, message)
    }

    def buildRecord(user:String, time:Date, message:String) = {
         <tr><td>{user}</td><td>{time.toString}</td><td>{message}</td></tr>
    }

    def drawMessage(user:String, time:Date, message:String) = {
        partialUpdate(PrependHtml(writeId,
                                  buildRecord(user, time, message)))
    }
}
