import React from 'react';
import PropTypes from 'prop-types';
import {LoaderOverlay} from '../DesignSystem/LoaderOverlay';
import {useTranslation} from 'react-i18next';
import {Dialog,DialogTitle,DialogContent} from '@material-ui/core';
import {postData} from "./engine";
import {useQuery,useLazyQuery} from "@apollo/react-hooks";
import {edpCoudinaryContentUUIDQuery,edpCoudinaryContentPropsQuery,ReferenceCard} from "./components";
import svgCloudyLogo from "../asset/logo.svg";
import {toIconComponent} from "@jahia/moonstone";
import {DisplayAction} from '@jahia/ui-extender';
import {getButtonRenderer} from '../utils';

const ButtonRenderer = getButtonRenderer({labelStyle: 'none', defaultButtonProps: {variant: 'ghost'}});

export const KeePicker = ({field, value, editorContext, inputContext, onChange, onBlur}) => {
    const [open,setOpen] = React.useState(false);

    const {t} = useTranslation();

    // const [loadEdp4UUID, selectedNodeUUID] = useLazyQuery(edpCoudinaryContentUUIDQuery);
    //
    // const config = window.contextJsParameters.config?.cloudinary;

    // React.useEffect( () => {
    //     if(!config?.cloudName || !config?.apiKey){
    //         console.error("oups... cloudinary cloudName and apiKey are not configured! Please fill the cloudinary_picker_credentials.cfg file.")
    //     }else{
    //         if(window.cloudinary){
    //             //#0 Prepare the cloudinary media lib
    //             setWidget(window.cloudinary.createMediaLibrary({
    //                 cloud_name: config.cloudName,
    //                 api_key: config.apiKey,
    //                 multiple: false //cannot select more than one asset
    //             }, {
    //                 insertHandler: (data) => {
    //                     // console.debug("cloudinary selected content : ",data);
    //                     //#1 fetch asset_id
    //                     postData(
    //                         "/resources/search",
    //                         {expression: `public_id=${data.assets[0].public_id} && resource_type=${data.assets[0].resource_type}`}
    //                     ).then( apiData => {
    //                         const asset_id = apiData?.resources[0]?.asset_id;
    //                         const edpContentPath = config.mountPoint + "/" + asset_id
    //                         //#2 create record and get uuid
    //                         loadEdp4UUID({
    //                             variables: {
    //                                 edpContentPath
    //                             }
    //                         })
    //                     });
    //                 }
    //             } ));
    //         }else{
    //             console.debug("oups... no window.cloudinary available !")
    //         }
    //     }
    // },[]);

    // const cloudinaryNodeInfo = useQuery(edpCoudinaryContentPropsQuery, {
    //     variables :{
    //         uuid : value,
    //         language: editorContext.lang,
    //     },
    //     skip: !value
    // });
    //
    // const error = selectedNodeUUID?.error || cloudinaryNodeInfo?.error;
    // const loading = selectedNodeUUID?.loading || cloudinaryNodeInfo?.loading;
    //
    // if (error) {
    //     const message = t(
    //         'jcontent:label.jcontent.error.queryingContent',
    //         {details: error.message ? error.message : ''}
    //     );
    //
    //     console.warn(message);
    // }
    //
    // if (loading) {
    //     return <LoaderOverlay/>;
    // }
    //
    // if(selectedNodeUUID?.data?.jcr?.result?.uuid){
    //     onChange(selectedNodeUUID.data.jcr.result.uuid);
    //     setTimeout(() => onBlur(), 0);
    // }
    //
    // let fieldData = null;
    // const cloudinaryJcrProps = cloudinaryNodeInfo?.data?.jcr?.result;
    //
    // if(cloudinaryJcrProps)
    //     fieldData = {
    //         name : cloudinaryJcrProps.displayName,
    //         resourceType: cloudinaryJcrProps.resourceType?.value,
    //         format: cloudinaryJcrProps.format?.value,
    //         url: cloudinaryJcrProps.url?.value,
    //         baseUrl: cloudinaryJcrProps.baseUrl?.value,
    //         endUrl: cloudinaryJcrProps.endUrl?.value,
    //         poster: cloudinaryJcrProps.poster?.value,
    //         width: cloudinaryJcrProps.width?.value,
    //         height: cloudinaryJcrProps.height?.value,
    //         bytes: cloudinaryJcrProps.bytes?.value,
    //         aspectRatio: cloudinaryJcrProps.aspectRatio?.value,
    //     }

    const dialogConfig = {
        fullWidth: true,
        maxWidth: 'xl',
        dividers: "true"
    };

    // const keepickerCardClick = (media) => {
    window.keepickerCardClick = (media) => {
        console.log("keepicker media",media);
    }

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
