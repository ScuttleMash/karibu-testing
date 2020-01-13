package com.github.mvysny.kaributesting.v8

import com.vaadin.server.ErrorMessage
import com.vaadin.ui.Component
import org.jsoup.Jsoup

/**
 * Unescapes [ErrorMessage.getFormattedHtmlMessage] and converts it to sane string. E.g.
 * `The&#32;user&#32;does&#32;not&#32;exist` is converted to `The user does not exist`.
 */
val ErrorMessage.message: String get() = Jsoup.parse(formattedHtmlMessage).text()

/**
 * Finds component's parent, parent's parent (etc) which satisfies given [predicate].
 * Returns null if there is no such parent.
 */
fun Component.findAncestor(predicate: (Component) -> Boolean): Component? =
        findAncestorOrSelf { it != this && predicate(it) }

/**
 * Finds component, component's parent, parent's parent (etc) which satisfies given [predicate].
 * Returns null if no component on the ancestor-or-self axis satisfies.
 */
tailrec fun Component.findAncestorOrSelf(predicate: (Component) -> Boolean): Component? {
    if (predicate(this)) {
        return this
    }
    val p: Component = parent ?: return null
    return p.findAncestorOrSelf(predicate)
}

/**
 * Checks if this component is nested in [potentialAncestor].
 */
fun Component.isNestedIn(potentialAncestor: Component) =
        findAncestor { it == potentialAncestor } != null
