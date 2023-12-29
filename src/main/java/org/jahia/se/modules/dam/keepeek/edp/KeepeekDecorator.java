package org.jahia.se.modules.dam.keepeek.edp;

import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRNodeDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

import static org.jahia.se.modules.dam.keepeek.ContentTypesConstants.*;

//TODO review
public class KeepeekDecorator extends JCRNodeDecorator {
    private static final Logger logger = LoggerFactory.getLogger(KeepeekDecorator.class);
    private final String THUMBNAIL_WIDTH = "200";
    private final String URL_WIDTH = "1024";
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
        }else{
            return this.getUrl();
        }
    }

    //TODO
    @Override
    public String getThumbnailUrl(String name) {
        String width = THUMBNAIL_WIDTH;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(node.getProperty("cloudy:baseUrl").getString());
            if("poster".equals(name))
                width= URL_WIDTH;

            sb.append("/f_auto,w_"+width+"/");

            if (node.hasProperty("cloudy:poster")) {
                sb.append(node.getProperty("cloudy:poster").getString());
            } else if (node.hasProperty("cloudy:endUrl")) {
                sb.append(node.getProperty("cloudy:endUrl").getString());
            }

            return sb.toString();
        } catch (RepositoryException e) {
            return getUrl();
        }
    }
}
