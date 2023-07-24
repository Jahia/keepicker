import {gql} from 'graphql-tag';
import {PredefinedFragments} from '@jahia/data-helper';

export const edpKeepeekContentPropsQuery = gql`
    query edpKeepeekContentPropsQuery($uuid: String!,$language: String!) {
        jcr{
            result: nodeById(uuid: $uuid) {
                displayName(language: $language)
                formType: property(name: "kpk:formType") {value}
                format: property(name: "kpk:format") {value}
                cover: property(name: "kpk:cover") {value}
                width: property(name: "kpk:width") {value}
                height: property(name: "kpk:height") {value}
                bytes: property(name: "kpk:fileSize") {value}
                ...NodeCacheRequiredFields
            }
        }
    }
    ${PredefinedFragments.nodeCacheRequiredFields.gql}
`;
