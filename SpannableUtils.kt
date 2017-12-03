import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView

object SpannableUtils {
    val DEFAULT_LINK_STYLE = LinkStyle(LinkStyle.LinkType.UNDERLINE)

    fun createClickableSpannable(rawHtml: String, onLinkClickedListener: (String) -> Unit,
                                 linkStyle: LinkStyle): CharSequence {
        val spannedTerm = Html.fromHtml(rawHtml)
        val strBuilder = SpannableStringBuilder(spannedTerm)
        val urls = strBuilder.getSpans(0, spannedTerm.length, URLSpan::class.java)
        for (span in urls) {
            replaceUrlSpanWithClickableSpan(strBuilder, span, onLinkClickedListener, linkStyle)
        }
        return strBuilder
    }

    private fun replaceUrlSpanWithClickableSpan(spannableBuilder: SpannableStringBuilder,
                                                span: URLSpan,
                                                onLinkClickedListener: (String) -> Unit,
                                                linkStyle: LinkStyle) {
        val start = spannableBuilder.getSpanStart(span)
        val end = spannableBuilder.getSpanEnd(span)
        val flags = spannableBuilder.getSpanFlags(span)

        val clickableSpan = object : CustomClickableSpan(linkStyle) {
            override fun onClick(widget: View) {
                onLinkClickedListener(span.url)
            }
        }
        spannableBuilder.setSpan(clickableSpan, start, end, flags)
        spannableBuilder.removeSpan(span)
    }

    private abstract class CustomClickableSpan constructor(
            private val linkStyle: LinkStyle) : ClickableSpan() {

        abstract override fun onClick(widget: View)

        override fun updateDrawState(textPaint: TextPaint) {
            if (linkStyle.linkColor != null) {
                textPaint.color = linkStyle.linkColor
            }

            when (linkStyle.linkType) {
                LinkStyle.LinkType.UNDERLINE -> textPaint.isUnderlineText = true
                LinkStyle.LinkType.NORMAL -> textPaint.isUnderlineText = false
            }
        }
    }
}

data class LinkStyle(val linkType: LinkType, @ColorInt val linkColor: Int? = null) {
    enum class LinkType {
        UNDERLINE,
        NORMAL
    }
}

fun TextView.setClickableHtml(@StringRes htmlResId: Int,
                              onLinkClickedListener: (String) -> Unit,
                              linkStyle: LinkStyle = SpannableUtils.DEFAULT_LINK_STYLE) {
    val html = context.getString(htmlResId)
    setClickableHtml(html, onLinkClickedListener, linkStyle)
}

fun TextView.setClickableHtml(html: String, onLinkClickedListener: (String) -> Unit,
                              linkStyle: LinkStyle = SpannableUtils.DEFAULT_LINK_STYLE) {
    val clickableSpannable = SpannableUtils.createClickableSpannable(html, onLinkClickedListener, linkStyle)
    text = clickableSpannable
    movementMethod = LinkMovementMethod.getInstance()
}
