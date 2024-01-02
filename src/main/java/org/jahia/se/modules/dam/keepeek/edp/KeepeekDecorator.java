package org.jahia.se.modules.dam.keepeek.edp;

import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRNodeDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.List;

import static org.jahia.se.modules.dam.keepeek.ContentTypesConstants.*;

public class KeepeekDecorator extends JCRNodeDecorator {
    private static final Logger logger = LoggerFactory.getLogger(KeepeekDecorator.class);
    private final String VIDEO_TYPE_480 = "480p";
    private final String VIDEO_TYPE_1080 = "1080p";
    private final String VIDEO_TYPE_PREVIEW = "preview";

    public KeepeekDecorator(JCRNodeWrapper node) {
        super(node);
    }

    @Override
    public String getDisplayableName() {
        try {
            return node.getProperty("jcr:title").getString();
        } catch (RepositoryException e) {
            return super.getDisplayableName();
        }
    }

    @Override
    public String getUrl() {
        try {
            return node.getProperty("kpk:whr").getString();
        } catch (RepositoryException e) {
            return super.getUrl();
        }
    }

    public String getUrl(List<String> params) throws RepositoryException {
        if(this.isNodeType(CONTENT_TYPE_IMAGE)){
            String keepeekProps = null;
            for (String param : params) {
                if (param.startsWith("width:")) {
                    try {
                        int width = Integer.parseInt(StringUtils.substringAfter(param, "width:"));
                        if(width >= 1400){
                            keepeekProps="kpk:xlarge";
                        } else if (width >= 960) {
                            keepeekProps="kpk:large";
                        } else if (width >= 780) {
                            keepeekProps="kpk:medium";
                        } else if (width >= 256) {
                            keepeekProps="kpk:small";
                        }
                    }catch (NumberFormatException e) {
                        logger.warn("Invalid integer input for width");
                    }
                }
            }
            if(keepeekProps != null && !keepeekProps.isEmpty()){
                return node.getProperty(keepeekProps).getString();
            }else{
                return this.getUrl();
            }
        }else if(this.isNodeType(CONTENT_TYPE_VIDEO)){
            String keepeekProps = VIDEO_TYPE_PREVIEW;
            for (String param : params) {
                if (param.startsWith("quality:")) {
                    String quality = StringUtils.substringAfter(param, "quality:");
                    if( VIDEO_TYPE_PREVIEW.equals(quality) ||
                            VIDEO_TYPE_480.equals(quality) ||
                            VIDEO_TYPE_1080.equals(quality)
                    ){
                        keepeekProps = quality;
                    }
                    return node.getProperty(keepeekProps).getString();
                }
            }
            return node.getProperty(keepeekProps).getString();
        }else{
            return this.getUrl();
        }
    }

    @Override
    public String getThumbnailUrl(String name) {
        try {
            return node.getProperty("kpk:poster").getString();
        } catch (RepositoryException e) {
            return super.getUrl();
        }
    }
}
