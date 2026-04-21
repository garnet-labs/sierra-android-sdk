// Copyright Sierra

package ai.sierra.sdk

@RequiresOptIn(
        message = "This API is internal to Sierra SDK modules and not intended for direct use.",
        level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
@Target(
        AnnotationTarget.PROPERTY,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.CLASS
)
public annotation class SierraInternalApi
