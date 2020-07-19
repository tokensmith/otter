package net.tokensmith.otter.controller.builder;

import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.controller.entity.mime.ParamKey;
import net.tokensmith.otter.controller.entity.mime.ParamValue;
import net.tokensmith.otter.controller.entity.mime.SubType;
import net.tokensmith.otter.controller.entity.mime.TopLevelType;

import java.util.LinkedHashMap;
import java.util.Map;

;

public class MimeTypeBuilder {
    private String topLevelType;
    private String subType;
    private Map<String, String> parameters;

    public MimeTypeBuilder html() {
        this.topLevelType = TopLevelType.TEXT.toString();
        this.subType = SubType.HTML.toString();
        this.parameters = new LinkedHashMap<>();
        parameters.put(ParamKey.CHARSET.toString(), ParamValue.UTF_8.toString());
        return this;
    }

    public MimeTypeBuilder json() {
        this.topLevelType = TopLevelType.APPLICATION.toString();
        this.subType = SubType.JSON.toString();
        this.parameters = new LinkedHashMap<>();
        parameters.put(ParamKey.CHARSET.toString(), ParamValue.UTF_8.toString());
        return this;
    }

    public MimeTypeBuilder jwt() {
        this.topLevelType = TopLevelType.APPLICATION.toString();
        this.subType = SubType.JWT.toString();
        this.parameters = new LinkedHashMap<>();
        parameters.put(ParamKey.CHARSET.toString(), ParamValue.UTF_8.toString());
        return this;
    }

    public MimeTypeBuilder form() {
        this.topLevelType = TopLevelType.APPLICATION.toString();
        this.subType = SubType.FORM.toString();
        this.parameters = new LinkedHashMap<>();
        parameters.put(ParamKey.CHARSET.toString(), ParamValue.UTF_8.toString());
        return this;
    }

    public MimeType build() {
        return new MimeType(topLevelType, subType, parameters);
    }
}
