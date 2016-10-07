var liferayAlloyConfig = require('liferay-forms-alloy-config');

liferayAlloyConfig.addModule(
	{
		modulePath: 'dynamic-data-mapping-form-renderer',
		pattern: '/js/*.js',
		excludePattern: '/*.soy.js'
	}
);

liferayAlloyConfig.addModule(
	{
		modulePath: 'dynamic-data-mapping-type-text',
		pattern: '/!(*.soy).js',
		excludePattern: '/*.soy.js'
	}
);

module.exports = function(karmaConfig) {
	liferayAlloyConfig.setConfigCoverage(karmaConfig);

	karmaConfig.files.push(		
 		'src/test/*.js'		
 	);
};