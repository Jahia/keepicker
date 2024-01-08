import React, {useEffect, useState, useRef} from 'react';
import PropTypes from 'prop-types';
import {LoaderOverlay} from '../DesignSystem/LoaderOverlay';
import {useTranslation} from 'react-i18next';
import {useQuery} from '@apollo/react-hooks';
import {edpKeepeekContentUUIDQuery} from './edpKeepeekContentUUID.gql-queries';
import {useContentEditorContext} from '@jahia/content-editor';

export const KeePickerDialog = ({className, onItemSelection}) => {
    const {t} = useTranslation();
    const {lang, uilang} = useContentEditorContext();
    const [keepeekData, setKeepeekData] = useState();
    const keepickerEl = useRef(null);

    const keepeekConfig = window.contextJsParameters.config?.keepeek;

    const {data, loading, error} = useQuery(edpKeepeekContentUUIDQuery, {
        variables: {
            edpContentPaths: keepeekData && keepeekData.assets.map(asset => keepeekConfig.mountPoint + '/' + asset.id)
        },
        skip: !keepeekData
    });

    useEffect(() => {
        const handleMediaSelection = event => {
            const derived = event.detail.link ? [{url: event.detail.link}] : [];
            const media = {assets :  [{...event.detail.element, derived}]};

            setKeepeekData(media);
        };

        if (keepickerEl && keepickerEl.current) {
            keepickerEl.current.addEventListener('kpk-insert', handleMediaSelection);
            keepickerEl.current.addEventListener('kpk-insert-link', handleMediaSelection);
        }
    }, [keepickerEl]);

    useEffect(() => {
        if (!error && !loading && data?.jcr?.result) {
            const exts = keepeekData.assets.map(({previewUrl: url, derived, title: name}) => ({
                name: name?.value,
                url: derived && derived.length > 0 ? derived[0].url : url
            }));

            onItemSelection(data.jcr.result.map((m, i) => ({...m, ...exts[i]})));
        }
    }, [keepeekData, data, error, loading, onItemSelection]);

    if (error) {
        const message = t(
            'jcontent:label.jcontent.error.queryingContent',
            {details: error.message ? error.message : ''}
        );

        console.warn(message);
    }

    if (loading) {
        return <LoaderOverlay/>;
    }

    if (!keepeekConfig.keycloakUrl || !keepeekConfig.keycloakRealm || !keepeekConfig.keycloakClientId || !keepeekConfig.apiEndPoint) {
        console.error('Keepeek front config error at least one front paramter is missing check the files org.jahia.se.modules.keepicker_credentials');
        return <p>config issue</p>;
    }

    return (
        <div className={className}>
            <kpk-keepicker
                ref={keepickerEl}
                // Keycloak-url={keepeekConfig.keycloakUrl}
                // keycloak-realm={keepeekConfig.keycloakRealm}
                keycloak-client-id={keepeekConfig.keycloakClientId}
                api-endpoint={keepeekConfig.apiEndPoint}
                data-locale={lang}
                ui-locale={uilang}
            />
        </div>
    );
};

KeePickerDialog.propTypes = {
    className: PropTypes.string,
    onItemSelection: PropTypes.func.isRequired
    // IsMultiple: PropTypes.bool
};
