package ca.jakegreene.scrape

import java.net.URL
import scala.collection.JavaConversions._
import org.jsoup.nodes.Element
import org.jsoup.Jsoup

object Scraper {
  def open(source: URL): ScrapeSelector = new DefaultSelector(new WebPage(source))
}

trait ScrapeSelector {

  /**
   * Uses the Jsoup selector syntax for selecting elements
   */
  def select(query: String): ScrapeTransformer
}

private class DefaultSelector(page: WebPage) extends ScrapeSelector {
  
  def select(query: String): ScrapeTransformer = {
    return new DefaultTransformer(page, query)
  }
}

trait ScrapeTransformer {
  def foreach(func: Element => Unit): Unit
  def map[A](func: Element => A): Seq[A]
  def get(): Seq[Element]
}

private class DefaultTransformer(page: WebPage, query: String) extends ScrapeTransformer {
  def foreach(func: Element => Unit) {
    val elements = select(query)
    elements.foreach(func)
  }
  
  def map[A](func: Element => A): Seq[A] = {
    val elements = select(query)
    return elements.map(func)
  }
  
  def get(): Seq[Element] = {
    return select(query)
  }
  
  private def select(query: String): Seq[Element] = {
    val doc = Jsoup.connect(page.url.toString()).userAgent("Mozilla").followRedirects(true).timeout(0).get
    return doc.select(query)
  }
}