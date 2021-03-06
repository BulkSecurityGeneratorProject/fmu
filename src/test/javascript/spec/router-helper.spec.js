describe('Router', function() {
    var routeConf, routeHelperService, state, rootScope;
    beforeEach(function() {
        module('util.router', function($provide, routehelperConfigProvider, $urlRouterProvider, $stateProvider) {
            routehelperConfigProvider.config.$urlRouterProvider = $urlRouterProvider;
            routehelperConfigProvider.config.$stateProvider = $stateProvider;
            routeConf = routehelperConfigProvider;
            mockLogger($provide);
        });

        inject(function(routeHelper, $state, $rootScope) {
            routeHelperService = routeHelper;
            state = $state;
            rootScope = $rootScope;
        });
    });

    it("should define config parameters", function() {
        expect(routeConf.config.$urlRouterProvider).toBeDefined();
        expect(routeConf.config.$stateProvider).toBeDefined();
        expect(routeHelperService).toBeDefined();
        expect(rootScope).toBeDefined();
    });

    it("should set default state", function() {
        var s = '/login';
        routeHelperService.setDefaultState(s);
        expect(routeConf.config.defaultState).toEqual('/login');
    });

    it("should register a state", function() {
        var s = {
            stateName: 'login',
            stateConfig: {
                url: '/login',
                templateUrl: 'login/login.html',
            }
        };

        routeHelperService.registerState(s.stateName, s.stateConfig);
        expect(state.href(s.stateName)).toEqual('#' + s.stateConfig.url);
    });

    it("should register multiple states", function() {
        var states = [{
                stateName: 'state1',
                stateConfig: {
                    url: '/state1',
                }
            }, {
            	stateName: 'state2',
                stateConfig: {
                    url: '/state2',
                }
            }
        ];

        routeHelperService.registerStates(states);
        expect(state.href(states[0].stateName)).toEqual('#' + states[0].stateConfig.url);
        expect(state.href(states[1].stateName)).toEqual('#' + states[1].stateConfig.url);
    });
});