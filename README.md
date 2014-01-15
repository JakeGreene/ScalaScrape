ScalaScrape
===========

An HTML scrapper that wraps Jsoup, adding a fluent DSL. Scraping Websites will create a Future[Seq[Element]]

```
/* Open the given site, select all of the 'a' elements,
 * extract the links (url and name) from the elements 
 * and print them
 */
open(site) select("a") extract links foreach(println)

// Open all of the given sites and treat them as one
open(sites) select("#class p") extract text map(_.length)
```

The DLS can also be used to scrape HTML and XHTML, producing a Seq[Element]

```
// Parses HTML strings
val html = "<html><body><p>Hello, World!</p></body></html>"
parse(html) select("p") extract text foreach(println)
// Parses XML
val xhtml = <html><body><p>Hello, World!</p></body></html>
parse(xhtml) select("p") extract text foreach(println)
```

