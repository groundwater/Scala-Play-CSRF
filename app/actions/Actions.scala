package actions

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import controllers._
import models._
import forms._

trait ValidateForm[A] extends Action[A]
object ValidateForm {
    val sessionkey = Form(
		single("sessionkey" -> text)
	)
    def apply[A] (bodyParser: BodyParser[A]) (block: Request[A] => Result ) = new ValidateForm[A] {
        def parser = bodyParser
        def apply ( req: Request[A] ) = {
        	req.session.get("sessionkey") match {
        		case None => Results.BadRequest("No Session Key")
        		case Some(token) =>
		            sessionkey.bindFromRequest()(req).fold (
		            	errors => Results.BadRequest("Missing Form Key"),
		            	{ sessionkey => 
		            		if ( sessionkey == token )
		            			block(req)
		            		else
		            			Results.BadRequest("Bad Key Value")
		            	}
		            )
        	}
        }
    }
    def apply (block: Request[AnyContent] => Result): Action[AnyContent] = {
        ValidateForm(BodyParsers.parse.anyContent)(block)
    }
}

object UUID {
	def apply() = {
		"HelloWorld"
	}
}
trait SessionKey[A] extends Action[A]
object SessionKey {
    def apply[A] (bodyParser: BodyParser[A]) (block: (String,String) => Request[A] => Result ) = new SessionKey[A] {
        def parser = bodyParser
        def apply ( request: Request[A] ) = {
        	val key = request.session.get("sessionkey") match {
        		case Some(key) => key
        		case _         => UUID()
        	}
        	block("sessionkey",key)(request)
        }
    }
    def apply (block: (String,String) => Request[AnyContent] => Result): Action[AnyContent] = {
        SessionKey(BodyParsers.parse.anyContent)(block)
    }
}

