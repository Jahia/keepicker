import React from 'react';
import PropTypes from 'prop-types';
import {LoaderOverlay} from '../DesignSystem/LoaderOverlay';
import {useTranslation} from 'react-i18next';
import {Dialog,DialogTitle,DialogContent} from '@material-ui/core';
import {postData} from "./engine";
import {useQuery,useLazyQuery} from "@apollo/react-hooks";
import {edpKeepeekContentUUIDQuery,edpKeepeekContentPropsQuery,ReferenceCard} from "./components";
import svgCloudyLogo from "../asset/logo.svg";
import {toIconComponent} from "@jahia/moonstone";
import {DisplayAction} from '@jahia/ui-extender';
import {getButtonRenderer} from '../utils';

const ButtonRenderer = getButtonRenderer({labelStyle: 'none', defaultButtonProps: {variant: 'ghost'}});

export const KeePicker = ({field, value, editorContext, inputContext, onChange, onBlur}) => {
    const [open,setOpen] = React.useState(false);

    const {t} = useTranslation();

    const [loadEdp4UUID, selectedNodeUUID] = useLazyQuery(edpKeepeekContentUUIDQuery);
    //
    const config = window.contextJsParameters.config?.keepeek;

    React.useEffect( () => {
        window.keepickerCardClick = (media) => {
            console.log("keepicker media",media);
            const asset_id = media?.id;
            const edpContentPath = config.mountPoint + "/" + asset_id
            //#2 create record and get uuid
            loadEdp4UUID({
                variables: {
                    edpContentPath
                }
            })
            //close Picker Dialog
            setOpen(false)
        }
    },[]);

    const keepeekNodeInfo = useQuery(edpKeepeekContentPropsQuery, {
        variables :{
            uuid : value,
            language: editorContext.lang,
        },
        skip: !value
    });

    const error = selectedNodeUUID?.error || keepeekNodeInfo?.error;
    const loading = selectedNodeUUID?.loading || keepeekNodeInfo?.loading;

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

    if(selectedNodeUUID?.data?.jcr?.result?.uuid){
        onChange(selectedNodeUUID.data.jcr.result.uuid);
        setTimeout(() => onBlur(), 0);
    }

    let fieldData = null;
    const keepeekJcrProps = keepeekNodeInfo?.data?.jcr?.result;

    if(keepeekJcrProps)
        fieldData = {
            name : keepeekJcrProps.displayName,
            resourceType: keepeekJcrProps.resourceType?.value,
            format: keepeekJcrProps.format?.value,
            url: keepeekJcrProps.url?.value,
            baseUrl: keepeekJcrProps.baseUrl?.value,
            endUrl: keepeekJcrProps.endUrl?.value,
            poster: keepeekJcrProps.poster?.value,
            width: keepeekJcrProps.width?.value,
            height: keepeekJcrProps.height?.value,
            bytes: keepeekJcrProps.bytes?.value,
            aspectRatio: keepeekJcrProps.aspectRatio?.value,
        }

    const dialogConfig = {
        fullWidth: true,
        maxWidth: 'xl',
        dividers: "true"
    };

    const handleShow = () =>
        setOpen(true)
        // alert("open popup keepeek")
        // widget.show();

    const handleClose = () =>
        setOpen(false)

    inputContext.actionContext={
        handleShow,
        onChange,
        onBlur
    }

    return (
        <div className="flexFluid flexRow_nowrap alignCenter">
            {/*{widget &&*/}
            <>
                <ReferenceCard
                    isReadOnly={field.readOnly}
                    emptyLabel={t('keepicker:label.referenceCard.emptyLabel')}
                    emptyIcon={toIconComponent(svgCloudyLogo)}
                    labelledBy={`${field.name}-label`}
                    fieldData={null} //{fieldData}
                    onClick={handleShow}
                />
                {inputContext.displayActions && value && (
                    <DisplayAction
                        actionKey="content-editor/field/KeePicker"
                        value={value}
                        field={field}
                        inputContext={inputContext}
                        render={ButtonRenderer}
                    />
                )}
                <Dialog
                    open={open}
                    fullWidth={dialogConfig.fullWidth}
                    maxWidth={dialogConfig.maxWidth}
                    // classes={{paper: classes.dialogPaper}}
                    onClose={handleClose}
                >
                <DialogTitle>
                    KeePicker
                </DialogTitle>
                <DialogContent dividers={dialogConfig.dividers}>
                    <kpk-keepicker
                        keycloak-url="https://auth.keepeek.com/auth"
                        keycloak-realm="iconeek"
                        keycloak-client-id="refront-iconeek-kpk-iconeek"
                        api-endpoint="https://iconeek.keepeek.com"
                        data-locale="FR"
                        ui-locale="FR"
                        card-click="keepickerCardClick">

                    </kpk-keepicker>
                </DialogContent>
            </Dialog>
            </>
        {/*}*/}
        </div>
    )
}

KeePicker.propTypes = {
    editorContext: PropTypes.object.isRequired,
    value: PropTypes.string,
    field: PropTypes.object.isRequired,
    inputContext: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired,
    onBlur: PropTypes.func.isRequired
};
