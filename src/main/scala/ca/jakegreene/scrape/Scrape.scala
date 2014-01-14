package ca.jakegreene.scrape

import java.net.URL
import java.net.URISyntaxException
import java.net.MalformedURLException
import Scraper._
import scala.util.Failure
import scala.util.Success
import akka.actor.ActorSystem

object Scrape extends App {
  val system = ActorSystem("scrape-system")
  import system.dispatcher 
  
  val html = "<html><body><p>Hello World!</p><p>Hello, World!</p></body></html>"
  parse(html) select("p") extract text foreach(println)
  val xhtml = <html><body><p>Hello World!</p><p>Hello, World!</p></body></html>
  parse(xhtml) select("p") extract text foreach(println)
  val site = new URL("http://www.google.ca/search?q=Hello+World")
  open(site) select("#res li.g h3.r a") extract links onComplete {
    case s @ Success(_) => s.get foreach(println)
    case Failure(ex) => println(ex)
  }
}