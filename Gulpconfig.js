'use strict';

module.exports = function() {
    var appPath = './src/main/webapp';
    var dist = appPath + '/dist';
    var bower = appPath + '/dependencies/bower_components/';
    var translationfolder = appPath + '/common/translations';
    var config = {
        appPath: appPath,
        tmp: './.tmp',
        index: appPath + '/index.html',
        jsfiles: [
            appPath + '/**/*.js',
            '!./src/main/webapp/dependencies/**',
            '!' + dist + '/**'
        ],
        sassfiles: [
            appPath + '/common/styles/sass/**/*.scss'
        ],
        cssfiles: ['.tmp/styles/*.css'],
        htmlfiles: appPath + '**/**.html',
        translationfolder: translationfolder,
        translationfiles: [appPath + '/**/**.{html,js}',
            '!' + appPath + '/dependencies/**',
            '!' + appPath + '/dist/**'],
        imagefiles: appPath + '/common/images/**/*.{png,jpg,jpeg,gif}',
        fonts: [bower + '**/*.{eot,svg,ttf,woff}', appPath + '/fonts/**'],
        dist: dist,
        bower: {
            json: require('./bower.json'),
            directory: bower
        }
    };

    config.getWiredepOptions = function(){
        var options = {
            bowerJson: config.bower.json,
            directory: config.bower.directory,
            ignorePath: config.bower.ignorePath
        };
        return options;
    };

    return config;
};
