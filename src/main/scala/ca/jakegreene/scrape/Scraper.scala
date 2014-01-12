package ca.jakegreene.scrape

import java.net.URL
import scala.collection.JavaConversions._
import org.jsoup.nodes.Element
import org.jsoup.Jsoup

object Scraper {
  def open(source: URL): ScrapeSelector = new DefaultSelector(load(source))
  private def load(source: URL): Element = {
    Jsoup.connect(source.toString()).userAgent("Mozilla").followRedirects(true).timeout(0).get
  }
}

trait ScrapeSelector {

  /**
   * Uses the Jsoup selector syntax for selecting elements
   */
  def select(query: String): Seq[Element]
}

private class DefaultSelector(root: Element) extends ScrapeSelector {
  
  def select(query: String): Seq[Element] = {
    root.select(query)
  }
}