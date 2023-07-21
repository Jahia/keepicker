import {registry} from '@jahia/ui-extender';
import {KeePicker} from './KeePicker';
import svgKeepeekLogo from './asset/logo.svg';
import i18next from 'i18next';
import {registerKeePickerActions} from "./KeePicker/components/actions/registerPickerActions";

i18next.loadNamespaces('keepicker');

export default function () {
    //load keepeek js TODO
    const script = document.createElement('script');
    script.type = 'text/javascript'
    script.src = 'https://kpkepic.z28.web.core.windows.net/poc-keepicker-auth/0.1.0/static/js/main.efe266d5.js';
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
}
