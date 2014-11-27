package at.risedev.vaadin.cdi.api;

import javax.enterprise.inject.Stereotype;
import javax.inject.Scope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author marcus.buettner
 */
@Stereotype
@Scope
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface CdiUI {

    public String value() default "";
}
