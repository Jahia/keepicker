import React from 'react';
import PropTypes from 'prop-types';
import {LoaderOverlay} from '../DesignSystem/LoaderOverlay';
import {useTranslation} from 'react-i18next';
import {Dialog,withStyles,DialogContent} from '@material-ui/core';

import {useQuery,useLazyQuery} from "@apollo/react-hooks";
import {edpKeepeekContentUUIDQuery,edpKeepeekContentPropsQuery,ReferenceCard} from "./components";
import svgCloudyLogo from "../asset/logo.svg";
import {toIconComponent} from "@jahia/moonstone";
import {DisplayAction} from '@jahia/ui-extender';
import {getButtonRenderer} from '../utils';

const ButtonRenderer = getButtonRenderer({labelStyle: 'none', defaultButtonProps: {variant: 'ghost'}});

const styles = theme => ({
    dialogPaper: {
        minHeight: 'calc(100vh - 96px)',
        maxHeight: 'calc(100vh - 96px)'
    }
})

const KeePickerCmp = ({classes, field, value, editorContext, inputContext, onChange, onBlur}) => {
    const [open,setOpen] = React.useState(false);
    const [dialogEntered,setDialogEntered] = React.useState(false);
    const keepickerEl = React.useRef(null);
    const {t} = useTranslation();


    const [loadEdp4UUID, selectedNodeUUID] = useLazyQuery(edpKeepeekContentUUIDQuery);
    const keepeekConfig = window.contextJsParameters.config?.keepeek;


    React.useEffect( () => {
        const handleMediaSelection = (event) =>{
            const media = event.detail.element;
            console.log("keepicker media",media);
            const asset_id = media?.id;
                const edpContentPath = keepeekConfig.mountPoint + "/" + asset_id
                //#2 create record and get uuid
                loadEdp4UUID({
                    variables: {
                        edpContentPath
                    }
                })
                //close Picker Dialog
                setOpen(false)
        }
        if(dialogEntered && keepickerEl && keepickerEl.current){
            keepickerEl.current.addEventListener("kpk-insert", handleMediaSelection);

            // return () => {
            //     keepickerEl.current.removeEventListener("kpk-insert",handleMediaSelection)
            // }
        }


        // window.keepickerCardClick = (media) => {
        //     console.log("keepickerCardClick media",media);
        //     // const asset_id = media?.id;
        //     // const edpContentPath = keepeekConfig.mountPoint + "/" + asset_id
        //     // //#2 create record and get uuid
        //     // loadEdp4UUID({
        //     //     variables: {
        //     //         edpContentPath
        //     //     }
        //     // })
        //     // //close Picker Dialog
        //     // setOpen(false)
        // }
    },[dialogEntered]);

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
            formType: keepeekJcrProps.formType?.value,
            format: keepeekJcrProps.format?.value,
            poster: keepeekJcrProps.poster?.value,
            width: keepeekJcrProps.width?.value,
            height: keepeekJcrProps.height?.value,
            fileSize: keepeekJcrProps.fileSize?.value,
            fileSizeString: keepeekJcrProps.fileSizeString?.value,
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

    const handleClose = () =>{
        setOpen(false)
        setDialogEntered(false)
    }


    const handleEntered = () =>
        setDialogEntered(true)

    inputContext.actionContext={
        handleShow,
        onChange,
        onBlur
    }

    if(!keepeekConfig.keycloakUrl || !keepeekConfig.keycloakRealm || !keepeekConfig.keycloakClientId || !keepeekConfig.apiEndPoint){
        console.error("Keepeek front config error at least one front paramter is missing check the files org.jahia.se.modules.keepicker_credentials")
        return <p>config issue</p>
    }

    return (
        <div className="flexFluid flexRow_nowrap alignCenter">
            <ReferenceCard
                isReadOnly={field.readOnly}
                emptyLabel={t('keepicker:label.referenceCard.emptyLabel')}
                emptyIcon={toIconComponent(svgCloudyLogo)}
                labelledBy={`${field.name}-label`}
                fieldData={fieldData} //{fieldData}
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
                classes={{paper: classes.dialogPaper}}
                onClose={handleClose}
                onEntered={handleEntered}
            >
                <DialogContent dividers={dialogConfig.dividers}>
                    <kpk-keepicker
                        ref={keepickerEl}
                        keycloak-url= {keepeekConfig.keycloakUrl}
                        keycloak-realm={keepeekConfig.keycloakRealm}
                        keycloak-client-id={keepeekConfig.keycloakClientId}
                        api-endpoint={keepeekConfig.apiEndPoint}
                        // data-locale="FR"
                        // ui-locale="FR"
                        // card-click="keepickerCardClick"
                    >
                    </kpk-keepicker>
                </DialogContent>
            </Dialog>
        </div>
    )
}

KeePickerCmp.propTypes = {
    classes:PropTypes.object.isRequired,
    editorContext: PropTypes.object.isRequired,
    value: PropTypes.string,
    field: PropTypes.object.isRequired,
    inputContext: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired,
    onBlur: PropTypes.func.isRequired
};

export const KeePicker = withStyles(styles)(KeePickerCmp);