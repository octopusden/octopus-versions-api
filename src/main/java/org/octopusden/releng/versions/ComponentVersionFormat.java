package org.octopusden.releng.versions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ComponentVersionFormat {

    private static final int MAGIK = 31;
    @JsonProperty
    private String majorVersionFormat;
    @JsonProperty
    private String releaseVersionFormat;
    @JsonProperty
    private String buildVersionFormat;
    @JsonProperty
    private String lineVersionFormat;
    @JsonProperty
    private String hotfixVersionFormat;

    private ComponentVersionFormat(String releaseVersionFormat,
                                   String majorVersionFormat,
                                   String buildVersionFormat,
                                   String lineVersionFormat,
                                   String hotfixVersionFormat) {
//        Validate.notNull(releaseVersionFormat);
//        Validate.notNull(majorVersionFormat);
        this.releaseVersionFormat = releaseVersionFormat;
        this.majorVersionFormat = majorVersionFormat;
        this.buildVersionFormat = buildVersionFormat;
        this.lineVersionFormat = lineVersionFormat;
        this.hotfixVersionFormat = hotfixVersionFormat;
    }

    public static ComponentVersionFormat create(String majorVersionFormat, String releaseVersionFormat) {
        return create(majorVersionFormat, releaseVersionFormat, null, null, null);
    }

    @JsonCreator
    public static ComponentVersionFormat create(@JsonProperty("majorVersionFormat") String majorVersionFormat,
                                                @JsonProperty("releaseVersionFormat") String releaseVersionFormat,
                                                @JsonProperty("buildVersionFormat") String buildVersionFormat,
                                                @JsonProperty("lineVersionFormat") String lineVersionFormat,
                                                @JsonProperty("hotfixVersionFormat") String hotfixVersionFormat) {
        return new ComponentVersionFormat(releaseVersionFormat, majorVersionFormat, buildVersionFormat, lineVersionFormat, hotfixVersionFormat);
    }

    public String getReleaseVersionFormat() {
        return releaseVersionFormat;
    }

    public String getMajorVersionFormat() {
        return majorVersionFormat;
    }

    public String getBuildVersionFormat() {
        return buildVersionFormat;
    }

    public String getLineVersionFormat() {
        return lineVersionFormat;
    }

    public String getHotfixVersionFormat() {
        return hotfixVersionFormat;
    }

    @Override
    public String toString() {
        return "ComponentVersionFormat{" +
                "releaseVersionFormat='" + releaseVersionFormat + '\'' +
                ", majorVersionFormat='" + majorVersionFormat + '\'' +
                ", buildVersionFormat='" + buildVersionFormat + '\'' +
                ", lineVersionFormat='" + lineVersionFormat + '\'' +
                ", hotfixVersionFormat='" + hotfixVersionFormat + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComponentVersionFormat that = (ComponentVersionFormat) o;
        return Objects.equals(releaseVersionFormat, that.releaseVersionFormat) &&
                Objects.equals(majorVersionFormat, that.majorVersionFormat) &&
                Objects.equals(buildVersionFormat, that.buildVersionFormat) &&
                Objects.equals(lineVersionFormat, that.lineVersionFormat) &&
                Objects.equals(hotfixVersionFormat, that.hotfixVersionFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(releaseVersionFormat, majorVersionFormat, buildVersionFormat, lineVersionFormat, hotfixVersionFormat);
    }
}
