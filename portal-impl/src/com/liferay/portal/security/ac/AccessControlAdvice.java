/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.security.ac;

import com.liferay.portal.spring.aop.AnnotationChainableMethodAdvice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Tomas Polesovsky
 * @author Igor Spasic
 * @author Michael C. Han
 * @author Raymond Augé
 */
public class AccessControlAdvice extends
	AnnotationChainableMethodAdvice<AccessControlled> {

	@Override
	public Object before(MethodInvocation methodInvocation) throws Throwable {
		boolean remoteAccess = AccessControlThreadLocal.isRemoteAccess();

		if (remoteAccess) {
			Method targetMethod = methodInvocation.getMethod();

			AccessControlled accessControlled = findAnnotation(
				methodInvocation);

			_accessControlAdvisor.accept(targetMethod, accessControlled);
		}

		return null;
	}

	@Override
	public AccessControlled getNullAnnotation() {
		return _nullAccessControlled;
	}

	public void setAccessControlAdvisor(
		AccessControlAdvisor accessControlAdvisor) {

		_accessControlAdvisor = accessControlAdvisor;
	}

	private static AccessControlled _nullAccessControlled =
		new AccessControlled() {

		public AccessControlType accessControlType() {
			return AccessControlType.ANONYMOUS;
		}

		public Class<? extends Annotation> annotationType() {
			return AccessControlled.class;
		}

	};

	private AccessControlAdvisor _accessControlAdvisor;

}