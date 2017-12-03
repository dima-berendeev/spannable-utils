Use it to show clickable links in TextView.

```kotlin
// make  text with link
val html = "Hello world <a href\"link\">link label</a>"
// create listener
val clickListener:(String) -> Unit = {url-> Toast.makeText(context,url, Toast.LENGTH_LONG)}
// show clicable text in text view
textView.setClickableHtml(html, clickListener)
```

If you need to change link style point third parameter

```kotlin
val linkStyle = LinkStyle(linkType = LinkStyle.LinkType.NORMAL, linkColor = Color.RED)
textView.setClickableHtml(html, clickListener, linkStyle)
```
