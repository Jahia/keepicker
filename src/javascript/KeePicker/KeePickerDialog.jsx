import React, {useEffect, useState, useRef} from 'react';
import PropTypes from 'prop-types';
import {LoaderOverlay} from '../DesignSystem/LoaderOverlay';
import {useTranslation} from 'react-i18next';
import {useQuery} from '@apollo/react-hooks';
import {edpKeepeekContentUUIDQuery} from './edpKeepeekContentUUID.gql-queries';

export const KeePickerDialog = ({className, onItemSelection}) => {
    const {t} = useTranslation();
    const [keepeekData, setKeepeekData] = useState();
    const keepickerEl = useRef(null);

    const keepeekConfig = window.contextJsParameters.config?.keepeek;

    const {data, loading, error} = useQuery(edpKeepeekContentUUIDQuery, {
        variables: {
            edpContentPaths: keepeekData && [keepeekConfig.mountPoint + '/' + keepeekData.id]
        },
        skip: !keepeekData
    });

    useEffect(() => {
        const handleMediaSelection = event => {
            const media = event.detail.element;
            setKeepeekData(media);
        };

        if (keepickerEl && keepickerEl.current) {
            keepickerEl.current.addEventListener('kpk-insert', handleMediaSelection);
        }
    }, [keepickerEl]);

    useEffect(() => {
        if (!error && !loading && data?.jcr?.result) {
            const exts = [{url: keepeekData.previewUrl, name: keepeekData.title?.value}];
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
                keycloak-url={keepeekConfig.keycloakUrl}
                keycloak-realm={keepeekConfig.keycloakRealm}
                keycloak-client-id={keepeekConfig.keycloakClientId}
                api-endpoint={keepeekConfig.apiEndPoint}
            />
        </div>
    );
};

KeePickerDialog.propTypes = {
    className: PropTypes.string,
    onItemSelection: PropTypes.func.isRequired
    // IsMultiple: PropTypes.bool
};
