package org.octopusden.releng.versions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    private ComponentVersionFormat(String releaseVersionFormat, String majorVersionFormat, String buildVersionFormat, String lineVersionFormat) {
//        Validate.notNull(releaseVersionFormat);
//        Validate.notNull(majorVersionFormat);
        this.releaseVersionFormat = releaseVersionFormat;
        this.majorVersionFormat = majorVersionFormat;
        this.buildVersionFormat = buildVersionFormat;
        this.lineVersionFormat = lineVersionFormat;
    }

    public static ComponentVersionFormat create(String majorVersionFormat, String releaseVersionFormat) {
        return create(majorVersionFormat, releaseVersionFormat, null, null);
    }

    @JsonCreator
    public static ComponentVersionFormat create(@JsonProperty("majorVersionFormat") String majorVersionFormat,
                                                @JsonProperty("releaseVersionFormat") String releaseVersionFormat,
                                                @JsonProperty("buildVersionFormat") String buildVersionFormat,
                                                @JsonProperty("lineVersionFormat") String lineVersionFormat) {
        return new ComponentVersionFormat(releaseVersionFormat, majorVersionFormat, buildVersionFormat, lineVersionFormat);
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

    @Override
    public String toString() {
        return "ComponentVersionFormat{" +
                "releaseVersionFormat='" + releaseVersionFormat + '\'' +
                ", majorVersionFormat='" + majorVersionFormat + '\'' +
                ", buildVersionFormat='" + buildVersionFormat + '\'' +
                ", lineVersionFormat='" + lineVersionFormat + '\'' +
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

        if (releaseVersionFormat != null ? !releaseVersionFormat.equals(that.releaseVersionFormat) : that.releaseVersionFormat != null) {
            return false;
        }
        if (majorVersionFormat != null ? !majorVersionFormat.equals(that.majorVersionFormat) : that.majorVersionFormat != null) {
            return false;
        }
        if (lineVersionFormat != null ? !lineVersionFormat.equals(that.lineVersionFormat) : that.lineVersionFormat != null) {
            return false;
        }
        return !(buildVersionFormat != null ? !buildVersionFormat.equals(that.buildVersionFormat) : that.buildVersionFormat != null);

    }

    @Override
    public int hashCode() {
        int result = releaseVersionFormat != null ? releaseVersionFormat.hashCode() : 0;
        result = MAGIK * result + (majorVersionFormat != null ? majorVersionFormat.hashCode() : 0);
        result = MAGIK * result + (buildVersionFormat != null ? buildVersionFormat.hashCode() : 0);
        result = MAGIK * result + (lineVersionFormat != null ? lineVersionFormat.hashCode() : 0);
        return result;
    }
}
