import {gql} from 'graphql-tag';
import {PredefinedFragments} from '@jahia/data-helper';

export const edpKeepeekContentPropsQuery = gql`
    query edpKeepeekContentPropsQuery($uuid: String!,$language: String!) {
        jcr{
            result: nodeById(uuid: $uuid) {
                displayName(language: $language)
                formType: property(name: "kpk:formType") {value}
                format: property(name: "kpk:format") {value}
                poster: property(name: "kpk:poster") {value}
                width: property(name: "kpk:width") {value}
                height: property(name: "kpk:height") {value}
                fileSize: property(name: "kpk:fileSize") {value}
                fileSizeString: property(name: "kpk:fileSizeString") {value}
                ...NodeCacheRequiredFields
            }
        }
    }
    ${PredefinedFragments.nodeCacheRequiredFields.gql}
`;
