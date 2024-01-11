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
        decorators.put("kpknt:image", KeepeekDecorator.class);
        decorators.put("kpknt:video", KeepeekDecorator.class);
        decorators.put("kpknt:other", KeepeekDecorator.class);
//        decorators.put("kpknt:pdf", KeepeekDecorator.class);
    }

    @Override
    public Map<String, Class> getDecorators() {
        return decorators;
    }
}
