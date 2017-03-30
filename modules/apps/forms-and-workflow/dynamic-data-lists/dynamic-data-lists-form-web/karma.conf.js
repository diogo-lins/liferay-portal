var liferayKarmaAlloyConfig = require('liferay-karma-alloy-config');
var liferayKarmaConfig = require('liferay-karma-config');
var path = require('path');

module.exports = function(config) {
	liferayKarmaConfig(config);

	config.files = [];

	liferayKarmaAlloyConfig(config);

	config.browserConsoleLogOptions = {
	  level: 'log',
	  format: '%b %T: %m',
	  terminal: true
	};

	var resourcesPath = 'src/main/resources/META-INF/resources';

	config.files.push(
		{
			included: true,
			pattern: 'node_modules/soyutils-nogoog/index.js'
		},
		{
			included: true,
			pattern: '../../dynamic-data-mapping/dynamic-data-mapping-form-renderer/' + resourcesPath + '/**/!(config)*.js'
		},
		{
			included: true,
			pattern: resourcesPath + '/**/!(config)*.js'
		},
		'src/test/testJS/**/*.js'
	);

	config.preprocessors[resourcesPath + '/**/*.soy.js'] = ['replacer'];
	config.preprocessors['../dynamic-data-lists-form-web/' + resourcesPath + '/**/*.soy.js'] = ['replacer'];

	config.replacerPreprocessor = {
		replacer: function(file, content) {
			var filePath = file.path.split(path.sep);

			var fileName = filePath.pop();

			if (fileName === 'calculator.soy.js') {
				content = [
					'AUI.add(\'liferay-ddl-form-builder-calculator-template\', function(A) {',
					content.replace(
						'(typeof ddl == \'undefined\') { var ddl = {}; }',
						'(typeof ddl == \'undefined\') { window.ddl = {}; }'
					),
					'}, \'\', {requires: []});'
				].join('');
			}

			return content;
		}
	};
};