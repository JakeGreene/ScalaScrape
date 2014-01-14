package ca.jakegreene.scrape

import java.net.URL
import scala.collection.JavaConversions.asScalaBuffer
import scala.language.implicitConversions
import scala.language.higherKinds
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import scala.xml.Elem
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object Scraper {
  
  trait Composition[F[_], G[_]] { type T[A] = F[G[A]] }
  
  /**
   * Connect to the given URL and load the page
   */
  def open(source: URL)(implicit ctx: ExecutionContext): ScrapeSelector[Composition[Future, Seq]#T] = {
    new FutureSelector(Future{load(source)})
  }
  
  /**
   * Parse the given HTML and provide it to a Selector
   */
  def parse(html: String): ScrapeSelector[Seq] = new DefaultSelector(Jsoup.parse(html))
  
  /**
   * Parse the given XHTML and provide it to a Selector
   */
  def parse(xml: Elem): ScrapeSelector[Seq] = new DefaultSelector(Jsoup.parse(xml.toString()))
  
  /*
   * Load the given URL into memory
   */
  private def load(source: URL): Element = {
    Jsoup.connect(source.toString()).userAgent("Mozilla").followRedirects(true).timeout(0).get
  }
  
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
  
  def text(element: Element): String = {
    element.text
  }
  
}

trait ScrapeSelector[T[_]] {
  
  /**
   * Uses the Jsoup selector syntax for selecting elements
   */
  def select(query: String): ScrapeExtractor[T]
}

private class DefaultSelector(root: Element) extends ScrapeSelector[Seq] {
  
  def select(query: String): Extractor = {
    new Extractor(root.select(query))
  }
}

import Scraper.Composition
private class FutureSelector(root: Future[Element])(implicit ctx: ExecutionContext) extends ScrapeSelector[Composition[Future, Seq]#T] {
  def select(query: String): FutureExtractor = {
    val elements: Future[Seq[Element]] = root map(element => element.select(query))
    new FutureExtractor(elements)
  }
}

trait ScrapeExtractor[T[_]] {
  
  def extract[A](func: Element => A): T[A]
}

class Extractor(elements: Seq[Element]) extends ScrapeExtractor[Seq] { 
  def extract[A](func: Element => A): Seq[A] = {
    elements.map(func)
  }
}

class FutureExtractor(elements: Future[Seq[Element]])(implicit ctx: ExecutionContext) 
  extends ScrapeExtractor[Composition[Future, Seq]#T] {
  def extract[A](func: Element => A): Future[Seq[A]] = {
    elements.map(seq => seq map(func))
  }
}
