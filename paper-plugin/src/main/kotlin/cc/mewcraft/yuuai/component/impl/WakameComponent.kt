package cc.mewcraft.yuuai.component.impl

import cc.mewcraft.wakame.attribute.AttributeMapAccess
import cc.mewcraft.wakame.attribute.AttributeProvider
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.TextResult
import cc.mewcraft.yuuai.component.ActionbarComponent
import cc.mewcraft.yuuai.component.ActionbarComponentFactory
import cc.mewcraft.yuuai.component.impl.WakameComponent.Companion.NAMESPACE
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode

interface WakameComponent : ActionbarComponent {
    companion object : AbstractYuuaiComponentFactory<WakameComponent>(), ActionbarComponentFactory<WakameComponent> {
        const val NAMESPACE = "wakame"

        private val attributeProvider: AttributeProvider = AttributeProvider.instance()

        override fun check(node: ConfigurationNode): CheckResult {
            // Wakame 的依赖在 nms 内.
            return CheckResult.Success
        }

        override fun getComponent(node: ConfigurationNode): WakameComponent {
            val attributeFormats = mutableMapOf<String, String>()
            for ((attributeName0, format) in node.node("attributes").childrenMap()) {
                val attributeName = attributeName0.toString()
                val attribute = attributeProvider.getSingleton(attributeName) ?: continue
                attributeFormats[attribute.descriptionId] = format.rawScalar() as String
            }
            return WakameComponentImpl(attributeFormats)
        }
    }
}

private class WakameComponentImpl(
    private val attributeFormats: Map<String, String>
) : WakameComponent {
    override val namespace: String = NAMESPACE
    companion object : KoinComponent {
        private val attributeMapAccess: AttributeMapAccess = AttributeMapAccess.instance()
        private val attributeProvider: AttributeProvider = AttributeProvider.instance()
        private val miniMessage: MiniMessage by inject()
    }

    override fun text(namespace: String, arguments: Array<String>, player: Player): TextResult {
        if (namespace != NAMESPACE)
            return TextResult.InvalidNamespace(namespace, NAMESPACE)
        if (arguments[0] != "attributes") {
            return TextResult.InvalidValues(arguments[0], "attributes")
        }
        val attributeString = arguments[1]
        if (attributeString !in attributeFormats.keys)
            return TextResult.InvalidValues(attributeString, *attributeFormats.keys.toTypedArray())
        val hideWhenZero = if (arguments.size == 3) {
            arguments[2].toBooleanStrictOrNull() ?: false
        } else false

        val attribute = attributeProvider.getSingleton(attributeString) ?: return TextResult.InvalidValues(attributeString, *attributeFormats.keys.toTypedArray())
        val format = attributeFormats[attributeString]!!
        val attributeData = attributeMapAccess.get(player).getOrThrow().getValue(attribute)

        if (hideWhenZero && attributeData == .0) {
            return TextResult.Success(Component.empty())
        }

        return TextResult.Success(
            miniMessage.deserialize(format, Formatter.number("value", attributeData))
        )
    }

    override fun load() = Unit
    override fun unload() = Unit
}