'use strict';

describe(
	'TextField',
	function() {
		var textField;

		afterEach(
			function(done) {
				textField.destroy();

				done();
			}
		);

		before(
			function(done) {
				AUI().use(
					'liferay-ddm-form-field-text-template',
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

		beforeEach(
			function(done) {
				textField = new Liferay.DDM.Field.Text(
					{
						context: {
							errorMessage: 'error',
							name: 'textField',
							required: true,
							value: 'marcellus'
						}
					}
				).render(document.body)

				done();
			}
		);

		describe(
			'TextField.showErrorMessage',
			function() {
				it(
					'should not show an error message when there isn\'t one',
					function(done) {
						textField.set('errorMessage', '');

						var container = textField.get('container');

						assert.notOk(
							container.one('.help-block'),
							'Error message should not be visible'
						);

						done();
					}
				);

				it(
					'should show an error message when there is one',
					function(done) {
						textField.set('errorMessage', 'Error Message');

						textField.showErrorMessage();

						var container = textField.get('container');

						assert.isOk(
							container.one('.help-block'),
							'Error message should be visible'
						);

						done();
					}
				);
			}
		);

		describe(
			'TextField.showLoadingFeedback',
			function() {
				it(
					'should not show a loading feedback after it\'s created',
					function(done) {
						var container = textField.get('container');

						assert.notOk(
							container.one('.icon-spinner'),
							'Loading icon should not be visible'
						);

						done();
					}
				);

				it(
					'should show loading feedback',
					function(done) {
						var container = textField.get('container');

						textField.showLoadingFeedback();

						assert.ok(
							container.one('.icon-spinner'),
							'Loading icon should be visible'
						);

						done();
					}
				);
			}
		);
	}
);