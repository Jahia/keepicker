import {gql} from 'graphql-tag';
import {PredefinedFragments} from '@jahia/data-helper';

export const edpKeepeekContentUUIDQuery = gql`
    query edpKeepeekContentUUIDQuery($edpContentPath: String!) {
        jcr{
            result: nodeByPath(path: $edpContentPath) {
                ...NodeCacheRequiredFields
            }
        }
    }
    ${PredefinedFragments.nodeCacheRequiredFields.gql}
`;
