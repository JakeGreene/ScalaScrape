package ca.jakegreene.scrape

import java.net.URL
import java.net.URISyntaxException
import java.net.MalformedURLException
import Scraper._

object Scrape extends App {
  val site = new URL("http://www.google.ca/search?q=Hello+World")
  //open(site) select("#res li.g h3.r a") extract links foreach(println)
  val html = "<html><body><p>Hello World!</p><p>Hello, World!</p></body></html>"
  parse(html) select("p") extract text foreach(println)
}