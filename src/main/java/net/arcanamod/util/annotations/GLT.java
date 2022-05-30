package net.arcanamod.util.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * \@GenLootTable
 */
public @interface GLT {
	/**
	 * replacement() Item as ResourceLocation.toString()
	 */
	String replacement() default "";
}
