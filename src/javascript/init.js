import {registry} from '@jahia/ui-extender';
import {KeePicker} from './KeePicker';
import svgKeepeekLogo from './asset/logo.svg';
import i18next from 'i18next';
import {registerKeePickerActions} from "./KeePicker/components/actions/registerPickerActions";

i18next.loadNamespaces('keepicker');

export default function () {
    if(window.contextJsParameters.config.keepeek && window.contextJsParameters.config.keepeek.hasOwnProperty("pickerCdn")){
        //load keepeek js
        const script = document.createElement('script');
        script.type = 'text/javascript'
        script.src = window.contextJsParameters.config.keepeek.pickerCdn;
        script.async = true;
        document.getElementsByTagName('head')[0].appendChild(script)

        registry.add('callback', 'keePickerSelectorType',{
            targets:['jahiaApp-init:20'],
            callback: () => {
                registry.add('selectorType','KeePicker', {cmp: KeePicker, supportMultiple:false});
                console.debug('%c KeePicker Editor Extensions  is activated', 'color: #3c8cba');

                registry.add('damSelectorConfiguration','KeePicker',{
                    types: ['kpkmix:kpkAsset'],
                    label:'keepicker:label.selectorConfig.label',
                    description: 'keepicker:label.selectorConfig.description',
                    module:'keepicker',
                    icon: svgKeepeekLogo,
                });

                registerKeePickerActions(registry);
            }
        })
    }else{
        console.error('KeePicker is not loaded, cdn url for the picker front app is missing check the files org.jahia.se.modules.keepicker_credentials');
    }

}
