import {registry} from '@jahia/ui-extender';
import {KeePickerDialog} from './KeePicker';
import svgKeepeekLogo from './asset/logo.svg';
import i18next from 'i18next';

i18next.loadNamespaces('keepicker');

export default function () {
    const config = window.contextJsParameters.config?.keepeek;
    if (config && config.pickerCdn) {
        // Load keepeek js
        const script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = config.pickerCdn;
        script.async = true;
        document.getElementsByTagName('head')[0].appendChild(script);

        registry.add('callback', 'keePickerSelectorType', {
            targets: ['jahiaApp-init:20'],
            callback: () => {
                registry.add('externalPickerConfiguration', 'keepeek', {
                    requireModuleInstalledOnSite: 'keepicker',
                    pickerConfigs: config.applyOnPickers ? config.applyOnPickers.split(',').map(item => item.trim()) : ['image', 'file'],
                    selectableTypes: ['kpkmix:kpkAsset'],
                    keyUrlPath: 'keepeek',
                    pickerInput: {
                        emptyLabel: 'keepicker:label.referenceCard.emptyLabel',
                        emptyIcon: svgKeepeekLogo
                    },
                    pickerDialog: {
                        cmp: KeePickerDialog,
                        label: 'keepicker:label.selectorConfig.label',
                        description: 'keepicker:label.selectorConfig.description',
                        icon: svgKeepeekLogo
                    }
                });
            }
        });
    } else {
        console.error('KeePicker is not loaded, cdn url for the picker front app is missing check the files org.jahia.se.modules.keepicker_credentials');
    }
}
