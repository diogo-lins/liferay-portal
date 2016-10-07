'use strict';

var A = AUI();

var assert = chai.assert;

var createTextField = function(config) {
	return new Liferay.DDM.Field.Text(
		A.merge(
			{
				context: {
					errorMessage: 'error',
					name: 'textField',
					required: true,
					value: 'marcellus'
				}
			},
			config || {}
		)
	).render(document.body);
};

var waitValueChange = function(callback) {
	setTimeout(callback, A.ValueChange.POLL_INTERVAL);
};

describe(
	'DDM Field Text',
	function() {
		before(
			function(done) {
				AUI().use(
					'liferay-ddm-form-field-text',
					function(A) {
						Liferay.DDM.Renderer.FieldTypes.register(
							{
								'javaScriptClass': 'Liferay.DDM.Field.Text',
								'name': 'text',
								'templateNamespace': 'ddm.text'
							}
						);

						done();
					}
				);
			}
		);

		it(
			'should show loading feedback',
			function(done) {
				var textField = createTextField();

				var container = textField.get('container');

				assert.notOk(
					container.one('.icon-spinner'),
					'Loading icon should not be visible'
				);

				textField.showLoadingFeedback();

				assert.ok(
					container.one('.icon-spinner'),
					'Loading icon should be visible'
				);

				textField.destroy();

				done();
			}
		);

		it(
			'Should show error message',
			function(done) {
				var textField = createTextField();

				textField.set('errorMessage', 'Error');

				textField.showErrorMessage();

				assert.isOk(
					textField.get('errorMessage'),
					'Error message should be setted'
				);

				var container = textField.get('container');

				assert.isOk(
					container.one('.help-block'),
					'Error message should be visible'
				);

				done();
			}
		);

		it(
			'Should create autoComplete with options',
			function(done) {
				var textField = createTextField(
					{
						options: [
							{label: 'Foo', value: 'Foo'},
							{label: 'Bar', value: 'Bar'}
						]
					}
				);

				var container = textField.get('container');

				var inputGroup = container.one('.input-group-container');

				var textAutoComplete = inputGroup.one('.yui3-aclist');

				assert.isOk(
					textAutoComplete,
					'Auto Complete is created'
				);

				assert.isOk(
					textField.getAutoComplete(),
					'Auto Complete is created'
				);

				done();
			}
		);

		it(
			'Should create autoComplete without options',
			function(done) {
				var textField = createTextField();

				var autoComplete = textField.getAutoComplete();

				assert.isOk(
					autoComplete,
					'Auto Complete is created'
				);

				done();
			}
		);

		it(
			'Should change options after autoComplete have been created',
			function(done) {
				var textField = createTextField(
					{
						options: [
							{label: 'Foo', value: 'Foo'},
							{label: 'Bar', value: 'Bar'}
						]
					}
				);

				textField.set('options', []);

				waitValueChange(
					function()	{

						assert.notOk(
							textField.get('options').length,
							'Options list is empty'
						);

						done();
					}
				);
			}
		);

		it(
			'Should focus when input is single line',
			function(done) {

				var textField = createTextField();

				textField.focus();
				textField.setValue('Lorem');

				assert(
					textField.getValue() === 'Lorem',
					'Text field value should be the same'
				);

				done();
			}
		);
	}
);