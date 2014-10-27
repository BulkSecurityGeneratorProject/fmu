'use strict';
angular.module('fmuClientApp')
.directive('fmuHandlingarTable', function(){
    return {
        scope: {
            handlingar: '=handlingar'
        },
        restrict: 'E',
        template: '\
        <div class="fmu-table-k">\
        <table>\
            <tr>\
                <td>\
                    Handling\
                </td>\
                <td>\
                    Registrerad av\
                </td>\
                <td>\
                    Registrerad, datum\
                </td>\
            </tr>\
            <tr ng-repeat="doc in handlingar">\
                <td>{{doc.name}}</td>\
                <td>{{doc.regBy.name}}, {{doc.regBy.unit}}</td>\
                <td>{{doc.regDate}}</td>\
            </tr>\
        </table>\
        </div>'
    };
})
.directive('fmuTillaggTable', function(){
    return {
        scope: {
            tillagg: '=tillagg'
        },
        restrict: 'E',
        template: '\
        <div class="fmu-table-k">\
        <table>\
            <tr>\
                <td>\
                    Handling\
                </td>\
                <td>Begäran skickad, av</th>\
                <td>Begäran skickad, datum</th>\
                <td>Kommentar</th>\
                <td>Begäran skickad till:</th>\
            </tr>\
            <tr ng-repeat="am in tillagg">\
                <td>{{am.name}}</td>\
                <td>{{am.reqBy.name}}, {{am.reqBy.unit}}</td>\
                <td>{{am.reqDate}}</td>\
                <td>{{am.comment}}</td>\
                <td>{{am.reqTo.name}}, {{am.reqBy.unit}}</td>\
            </tr>\
        </table>\
        </div>'
    };
});