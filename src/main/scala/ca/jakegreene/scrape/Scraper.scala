package ca.jakegreene.scrape

import java.net.URL

import scala.collection.JavaConversions.asScalaBuffer
import scala.language.implicitConversions

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

object Scraper {
  
  /**
   * Connect to the given URL and load the page
   */
  def open(source: URL): ScrapeSelector = new DefaultSelector(load(source))
  
  /*
   * Load the given URL into memory
   */
  private def load(source: URL): Element = {
    Jsoup.connect(source.toString()).userAgent("Mozilla").followRedirects(true).timeout(0).get
  }
  
  implicit def extractor2Elements(s: ScrapeExtractor): Seq[Element] = s.elements
  
  case class Link(title: String, href: URL)
  /**
   * Extract a Link out of the given element.
   * 
   * Extractor functions are plural to enhance the readability of the DSL
   */
  def links(element: Element): Link = {
    val link = element.select("a[href]").attr("abs:href")
    val url = new URL(link)
    Link(element.text, url)
  }
  
}

trait ScrapeSelector {
  
  /**
   * Uses the Jsoup selector syntax for selecting elements
   */
  def select(query: String): ScrapeExtractor
}

private class DefaultSelector(root: Element) extends ScrapeSelector {
  
  def select(query: String): ScrapeExtractor = {
    new ScrapeExtractor(root.select(query))
  }
}

class ScrapeExtractor(val elements: Seq[Element]) {
  
  def extract[A](func: Element => A): Seq[A] = {
    elements map(func)
  }
}
