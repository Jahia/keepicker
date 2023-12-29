package org.jahia.se.modules.dam.keepeek.service;

import org.jahia.se.modules.dam.keepeek.edp.KeepeekDecorator;
import org.jahia.services.content.decorator.JCRNodeDecoratorDefinition;
import org.osgi.service.component.annotations.Component;

import java.util.HashMap;
import java.util.Map;

@Component(immediate = true, service = JCRNodeDecoratorDefinition.class)
public class KeepeekDecoratorDefinition extends JCRNodeDecoratorDefinition {
    private Map<String, Class> decorators = new HashMap<>();

    public KeepeekDecoratorDefinition() {
        decorators.put("cloudynt:image", KeepeekDecorator.class);
        decorators.put("cloudynt:document", KeepeekDecorator.class);
        decorators.put("cloudynt:pdf", KeepeekDecorator.class);
        decorators.put("cloudynt:video", KeepeekDecorator.class);
    }

    @Override
    public Map<String, Class> getDecorators() {
        return decorators;
    }
}
