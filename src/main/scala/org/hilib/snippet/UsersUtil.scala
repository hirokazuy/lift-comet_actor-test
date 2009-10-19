package org.hilib.snippet

import scala.xml.NodeSeq
import org.hilib._
import model._

class UsersUtil {

    def in(xhtml:NodeSeq): NodeSeq = {
        if (User.loggedIn_?) xhtml else NodeSeq.Empty
    }

    def out(xhtml:NodeSeq): NodeSeq = {
        if (!User.loggedIn_?) xhtml else NodeSeq.Empty
    }
}
