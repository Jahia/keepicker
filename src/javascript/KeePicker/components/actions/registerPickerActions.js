import React from 'react';
import {Edit, Cancel, MoreVert} from '@jahia/moonstone';
import {unsetFieldAction} from './unsetFieldAction';
import {replaceAction} from './replaceAction';

export const registerKeePickerActions = registry => {
    registry.add('action', 'content-editor/field/KeePicker', registry.get('action', 'menuAction'), {
        buttonIcon: <MoreVert/>,
        buttonLabel: 'label.contentEditor.edit.action.fieldMoreOptions',
        menuTarget: 'content-editor/field/KeePickerActions',
        menuItemProps: {
            isShowIcons: true
        }
    });

    registry.add('action', 'replaceKeepeekContent', replaceAction, {
        buttonIcon: <Edit/>,
        buttonLabel: 'content-editor:label.contentEditor.edit.fields.actions.replace',
        targets: ['content-editor/field/KeePickerActions:1']
    });

    registry.add('action', 'unsetFieldActionKeePicker', unsetFieldAction, {
        buttonIcon: <Cancel/>,
        buttonLabel: 'content-editor:label.contentEditor.edit.fields.actions.clear',
        targets: ['content-editor/field/KeePickerActions:2']
    });
};
