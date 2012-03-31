package controllers

import play.api._
import play.api.mvc._

import akka.actor._
import akka.pattern._
import akka.util._
import akka.util.duration._

import message._
import actions._
import actors._
import models._
import forms._

import play.api.libs.concurrent.AkkaPromise
import play.libs.Akka.system


object Authenticator extends Controller {
    def login = ValidateForm{ 
    	Action { implicit request =>
    		Ok( views.html.index("You're Loggd In") )
    	}
    }	
}

object Application extends Controller {
    def index = Action { implicit request =>
        Ok( views.html.index("Not Logged In") )
    }
    def login = SessionKey{ (key,signature) => 
    	Action { implicit request =>
    		Ok( views.html.login(signature) ).withSession( key->signature )
    	}
    }
}
