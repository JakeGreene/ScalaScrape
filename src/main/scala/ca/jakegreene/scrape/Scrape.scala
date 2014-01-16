package ca.jakegreene.scrape

import java.net.URL

import scala.util.Failure
import scala.util.Success

import Scraper._
import akka.actor.ActorSystem

object Scrape extends App {
  val system = ActorSystem("scrape-system")
  import system.dispatcher 
  
  // Scrape HTML
  val html = "<html><body><p>Hello World!</p><p>Hello, World!</p></body></html>"
  parse(html) select("p") extract text foreach(println)
  
  //Scrape XHTML
  val xhtml = <html><body><p>Hello World!</p><p>Hello, World!</p></body></html>
  parse(xhtml) select("p") extract text foreach(println)
  
  // Asynchronously Scrape a webpage
  val site = new URL("http://www.google.ca/search?q=Hello+World")
  open(site) select("#res li.g h3.r a") extract links onComplete {
    case Success(links) => links foreach(println)
    case Failure(ex) => println(ex)
  }
  
  // Asynchronously Scrape multiple webpages
  val sites = site +: new URL("http://www.google.ca/search?q=Goodbye+World") +: Nil
  open(sites) select("#res li.g h3.r a") extract links onComplete {
    case Success(links) => links foreach(println)
    case Failure(ex) => println(ex)
  }
  
  // Create a function URL => Link
  val findLinks = givenUrl select("#res li.g h3.r a") extract links
  findLinks(site) foreach(println)
}