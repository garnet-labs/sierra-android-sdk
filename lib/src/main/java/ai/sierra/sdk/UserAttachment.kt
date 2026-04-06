// Copyright Sierra

package ai.sierra.sdk

import org.json.JSONObject

enum class UserAttachmentType(val value: String) {
    CUSTOM("custom"),
}

data class UserAttachment(
    val type: UserAttachmentType,
    val data: Map<String, Any?>
) {
    internal fun toJSONObject(): JSONObject = JSONObject().apply {
        put("type", type.value)
        put("data", JSONObject(data))
    }

    companion object {
        fun custom(data: Map<String, Any?>): UserAttachment = UserAttachment(
            type = UserAttachmentType.CUSTOM,
            data = data
        )
    }
}
