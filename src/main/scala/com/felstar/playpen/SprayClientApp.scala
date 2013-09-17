package com.felstar.playpen

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout.durationToTimeout
import spray.can.Http
import spray.client.pipelining._
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.util._
import scala.util.{Success, Failure}

object SprayClientApp {
    
  def main(args : Array[String]) {
   
   implicit val system = ActorSystem("spray-client")
   import system.dispatcher // execution context for futures

   val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

   val response: Future[HttpResponse] = 
     pipeline(Get("http://maps.googleapis.com/maps/api/elevation/json?locations=27.988056,86.925278&sensor=false"))
 
   response onComplete {
     case Success(resp) => println(resp.status);println(resp.entity);shutdown()
     case Failure(error) => println(error);shutdown()
   }
  
   def shutdown(): Unit = {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }
 }
}