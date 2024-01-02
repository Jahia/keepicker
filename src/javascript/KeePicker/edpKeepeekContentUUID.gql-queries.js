import {gql} from 'graphql-tag';
import {PredefinedFragments} from '@jahia/data-helper';

export const edpKeepeekContentUUIDQuery = gql`
    query edpKeepeekContentUUIDQuery($edpContentPaths: [String!]!) {
        jcr{
            result: nodesByPath(paths: $edpContentPaths) {
                ...NodeCacheRequiredFields
            }
        }
    }
    ${PredefinedFragments.nodeCacheRequiredFields.gql}
`;
