package org.hilib.comet

import scala.xml.NodeSeq
import net.liftweb._
import http._
import util._
import Helpers._
import js.JsCmds._
import js.jquery.JqJsCmds._

class TestComet extends CometActor {
	override def defaultPrefix = Full("test")

	ActorPing.schedule(this, InitializeMessage, TimeSpan(1000))

	private val tableId = "comet_test"
	override def render = bind("block" -> blankBlock)
	def blankBlock = <table id={tableId}></table>

	var blocks:List[Int] = null

	override def lowPriority = {
		case InitializeMessage =>
//			blocks = List[Int](1)
			registTimerOnce()
		case TimerMessage =>
			Log.info("catch TimerMessage " + blocks.toString)
			drawBlocks()
			updateBlocks()
			registTimerOnce()
	}

	override def localSetup() = {
	    blocks = List[Int](1)
	    super.localSetup()
	}

	override def localShutdown() = {
	    blocks = null
	    super.localShutdown()
	}

	def updateBlocks() = {
		blocks = 1 :: blocks.map(a => a + 1)
	}

	def buildBlockNode(count: Int): NodeSeq = {
		if (count == 0) return Nil
		else {
			return <td></td> ++ {buildBlockNode(count - 1)}
		}
	}

	def buildBlockNode(blocks: List[Int]): NodeSeq = {
		try {
			blocks.map(a => <tr>{buildBlockNode(a)}</tr>)
		} catch {
		case e:Exception => S.error(<p>{e.toString}</p>); <p>error occurs</p>
		}
	}

	def drawBlocks() = {
		Log.info(buildBlockNode(blocks).toString)
		partialUpdate(SetHtml(tableId, buildBlockNode(blocks)))
	}

	def registTimerOnce() = {
		ActorPing.schedule(this, TimerMessage, 1000)
	}
}

case object InitializeMessage
case object TimerMessage
