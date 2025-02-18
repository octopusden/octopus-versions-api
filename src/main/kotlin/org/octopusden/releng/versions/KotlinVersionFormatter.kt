package org.octopusden.releng.versions

import org.octopusden.utils.expandContext
import org.octopusden.utils.offsetFormat

class KotlinVersionFormatter(
    private val versionNames: VersionNames
) : VersionFormatter {

    private val numericVersionFactory: NumericVersionFactory = NumericVersionFactory(versionNames)
    // TODO: make static
    val PREDEFINED_VARIABLES_LIST: List<Pair<String, (IVersionInfo) -> String>> = listOf(
            versionNames.serviceBranch to { version: IVersionInfo -> calculateServiceCBranch(version).offsetFormat(2) },
            versionNames.service to { version: IVersionInfo -> calculateServiceC(version).offsetFormat(2) },
            "service02" to { version: IVersionInfo -> version.service.offsetFormat(2) },
            "service" to { version: IVersionInfo -> version.service.toString() },

            versionNames.minor to { version: IVersionInfo -> calculateMinorC(version).offsetFormat(2) },
            "minor02" to { version: IVersionInfo -> version.minor.offsetFormat(2) },
            "minor" to { version: IVersionInfo -> version.minor.toString() },

            "major02" to { version: IVersionInfo -> version.major.offsetFormat(2) },
            "major" to { version: IVersionInfo -> version.major.toString() },

            "fix04" to { version: IVersionInfo -> version.fix.offsetFormat(4) },
            "fix02" to { version: IVersionInfo -> version.fix.offsetFormat(2) },
            "fix" to { version: IVersionInfo -> version.fix.toString() },

            "build04" to { version: IVersionInfo -> version.buildNumber.offsetFormat(4) },
            "build02" to { version: IVersionInfo -> version.buildNumber.offsetFormat(2) },
            "build" to { version: IVersionInfo -> version.buildNumber.toString() }
    )

    val PREDEFINED_COMPONENT_VARIABLES_LIST = listOf(
        "hotfixSuffix" to { _: String, _: String, hotfixSuffix: String -> hotfixSuffix },
        "versionPrefix" to { version: String, versionPrefix: String, _: String -> versionPrefix },
        "baseVersionFormat" to { version: String, _: String, _: String -> version }
    )

    val PREDEFINED_POSTPROCESSOR_LIST = listOf(
            "module" to { componentName: String, _: String -> componentName },
            "version" to { _: String, version: String -> version },
            "cvsCompatibleUnderscoreVersion" to { _: String, version: String -> version.replace(".", "_") },
            "cvsCompatibleVersion" to { _: String, version: String -> version.replace(".", "-") }
    )

    private fun calculateServiceCBranch(version: IVersionInfo): Int {
        val i = (calculateServiceC(version) / 10)
        return i * 10
    }

    private fun calculateMinorC(version: IVersionInfo) = if (version.service == 99) version.minor + 1 else version.minor


    private fun calculateServiceC(version: IVersionInfo) = if (version.service == 99) 0 else version.service + 1


    override fun format(format: String, version: IVersionInfo): String =
            format.expandContext(PREDEFINED_VARIABLES_LIST.map { (key, value) -> key to value(version) }.toMap())

    override fun formatToCustomerVersion(
        customerFormat: String,
        versionFormat: String,
        versionPrefix: String,
        version: IVersionInfo
    ): String? =
        customerFormat.expandContext(
            PREDEFINED_COMPONENT_VARIABLES_LIST
                .map { (key, value) -> key to value(format(versionFormat, version), versionPrefix, "") }.toMap()
        )

    override fun formatToCustomerVersion(
        customerFormat: String,
        versionFormat: String,
        versionPrefix: String,
        hotfixSuffix: String?,
        versionInfo: IVersionInfo
    ): String? {
        val formattedHotfixSuffix = if (hotfixSuffix != null) { format(hotfixSuffix, versionInfo) } else { "" }
        return customerFormat.expandContext(
            PREDEFINED_COMPONENT_VARIABLES_LIST
                .map { (key, value) -> key to value(format(versionFormat, versionInfo), versionPrefix, formattedHotfixSuffix) }
                .toMap()
        )
    }

    override fun matchesFormat(format: String, version: String): Boolean {
        val numericVersion = numericVersionFactory.create(version)
        val patchedFormat = getPatchedFormat(format)
        return version == format(patchedFormat, numericVersion)
    }

    override fun matchesFormat(customerFormat: String, versionFormat: String, versionPrefix: String, version: String): Boolean {
        val patchedFormat = getPatchedFormat(versionFormat)
        return version == formatToCustomerVersion(customerFormat, patchedFormat, versionPrefix, numericVersionFactory.create(version))
    }

    fun getPatchedFormat(format: String) = format
            .replace("\$${versionNames.serviceBranch}", "\$service02")
            .replace("\$${versionNames.service}", "\$service02")
            .replace("\$${versionNames.minor}", "\$minor02")


    override fun matchesNonStrictFormat(format: String, version: String): Boolean {
        var tempFormat = format
        val predefinedVariableCount = PREDEFINED_VARIABLES_LIST.filter {
            val res = tempFormat.contains(it.first.substring(1))
            tempFormat = tempFormat.replace(it.first, "")
            res
        }.size
        return predefinedVariableCount <= numericVersionFactory.create(version).itemsCount
    }

}
