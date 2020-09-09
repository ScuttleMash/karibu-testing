package com.github.mvysny.kaributesting.v10

import com.github.mvysny.dynatest.DynaNodeGroup
import com.github.mvysny.dynatest.expectThrows
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.dom.DomEvent
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.ErrorParameter
import com.vaadin.flow.router.HasErrorParameter
import com.vaadin.flow.router.Route
import kotlin.test.expect

val allViews: Set<Class<out Component>> = setOf<Class<out Component>>(
        TestingView::class.java, HelloWorldView::class.java, WelcomeView::class.java,
        ParametrizedView::class.java, ChildView::class.java, NavigationPostponeView::class.java)
val allErrorRoutes: Set<Class<out HasErrorParameter<*>>> = setOf(ErrorView::class.java, MockRouteNotFoundError::class.java)

internal fun DynaNodeGroup.basicUtilsTestbatch() {

    test("AutoViewDiscovery") {
        expect(allViews) { Routes().autoDiscoverViews("com.github").routes }
        expect(allErrorRoutes) { Routes().autoDiscoverViews("com.github").errorRoutes }
    }

    test("calling autoDiscoverViews() multiple times won't fail") {
        expect(allViews) { Routes().autoDiscoverViews("com.github").routes }
        expect(allViews) { Routes().autoDiscoverViews("com.github").routes }
    }

    group("checkEditableByUser") {
        test("disabled textfield fails") {
            expectThrows(java.lang.IllegalStateException::class, "The TextField[DISABLED, value=''] is not enabled") {
                TextField().apply { isEnabled = false }.checkEditableByUser()
            }
        }
        test("invisible textfield fails") {
            expectThrows(java.lang.IllegalStateException::class, "The TextField[INVIS, value=''] is not effectively visible") {
                TextField().apply { isVisible = false }.checkEditableByUser()
            }
        }
        test("textfield in invisible layout fails") {
            expectThrows(java.lang.IllegalStateException::class, "The TextField[value=''] is not effectively visible") {
                VerticalLayout().apply {
                    isVisible = false
                    textField().also { it.checkEditableByUser() }
                }
            }
        }
        test("textfield succeeds") {
            TextField().checkEditableByUser()
        }
    }

    group("expectNotEditableByUser") {
        test("disabled textfield fails") {
            TextField().apply { isEnabled = false }.expectNotEditableByUser()
        }
        test("invisible textfield fails") {
            TextField().apply { isVisible = false }.expectNotEditableByUser()
        }
        test("textfield in invisible layout fails") {
            VerticalLayout().apply {
                isVisible = false
                textField().also { it.expectNotEditableByUser() }
            }
        }
        test("textfield succeeds") {
            expectThrows(AssertionError::class, "The TextField[value=''] is editable") {
                TextField().expectNotEditableByUser()
            }
        }
    }

    group("fireDomEvent()") {
        test("smoke") {
            Div()._fireDomEvent("click")
        }
        test("listeners are called") {
            val div = Div()
            lateinit var event: DomEvent
            div.element.addEventListener("click") { e -> event = e }
            div._fireDomEvent("click")
            expect("click") { event.type }
        }
    }

    test("_focus") {
        val f = TextField()
        var called = false
        f.addFocusListener { called = true }
        f._focus()
        expect(true) { called }
    }

    test("_blur") {
        val f = TextField()
        var called = false
        f.addBlurListener { called = true }
        f._blur()
        expect(true) { called }
    }
}

@Route("testing")
class TestingView : VerticalLayout()

class ErrorView : VerticalLayout(), HasErrorParameter<Exception> {
    override fun setErrorParameter(event: BeforeEnterEvent, parameter: ErrorParameter<Exception>): Int =
            throw RuntimeException(parameter.caughtException)
}
