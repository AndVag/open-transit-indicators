// Karma configuration
// http://karma-runner.github.io/0.10/config/configuration-file.html

module.exports = function(config) {

  if (!process.env.SAUCE_USERNAME || !process.env.SAUCE_ACCESS_KEY) {
    console.log('Sauce environment variables not set; testing with PhantomJS (we must be running locally.)');
    var customLaunchers = {PhantomJS: {}};
  } else {
  // browsers for Sauce Labs
    var customLaunchers = {
      sl_chrome: {
        base: 'SauceLabs',
        browserName: 'chrome',
        platform: 'Windows 7'
      },
      sl_firefox: {
        base: 'SauceLabs',
        browserName: 'firefox',
        version: '27'
      },
      sl_ie_11: {
        base: 'SauceLabs',
        browserName: 'internet explorer',
        platform: 'Windows 8.1',
        version: '11'
      }
    };
  }

  config.set({
    // base path, that will be used to resolve files and exclude
    basePath: '',

    // testing framework to use (jasmine/mocha/qunit/...)
    frameworks: ['jasmine'],

    // list of files / patterns to load in the browser
    files: [
      'app/bower_components/jquery/dist/jquery.min.js',
      'app/bower_components/underscore/underscore.js',
      'app/bower_components/bootstrap/dist/js/bootstrap.min.js',
      'app/bower_components/angular/angular.js',
      'app/bower_components/angular-bootstrap/ui-bootstrap.min.js',
      'app/bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js',
      'app/bower_components/angular-cookies/angular-cookies.min.js',
      'app/bower_components/angular-leaflet-directive/dist/angular-leaflet-directive.min.js',
      'app/bower_components/angular-ui-router/release/angular-ui-router.min.js',
      'app/bower_components/angular-mocks/angular-mocks.js',
      'app/bower_components/angular-resource/angular-resource.js',
      'app/bower_components/angular-translate/angular-translate.min.js',
      'app/bower_components/leaflet-dist/leaflet.js',
      'app/bower_components/ng-file-upload/angular-file-upload.min.js',
      'app/scripts/*.js',
      'app/scripts/**/*.js',
      //'test/mock/**/*.js',
      'test/spec/**/*.js'
    ],

    // list of files / patterns to exclude
    exclude: [],

    // web server port
    port: 8080,

    // level of logging
    // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: false,
    
    colors: true,
    
    logLevel: config.LOG_INFO,
    
    sauceLabs: {
        testName: 'Open Transit Indicators Unit Tests'
    },
    
    // Increase timeout in case connection in CI is slow
    captureTimeout: 120000,
    
    customLaunchers: customLaunchers,
    
    browsers: Object.keys(customLaunchers),
    
    reporters: ['dots', 'saucelabs'],

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: true
  });
};
