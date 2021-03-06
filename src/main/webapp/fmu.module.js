(function() {
    'use strict';

    /**
     * fmu Module
     *
     * The main module of fmu app
     */
    angular.module('fmu', [
        // Common module dependencies
        'fmu.core',
        'fmu.widgets',

        // Sub modules
        'fmu.login',
        'fmu.eavrop',
        'fmu.overview'
    ]);
})();