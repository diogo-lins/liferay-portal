/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.subscription.web.util;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.template.MailTemplate;
import com.liferay.mail.kernel.template.MailTemplateContext;
import com.liferay.mail.kernel.template.MailTemplateContextBuilder;
import com.liferay.mail.kernel.template.MailTemplateFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Subscription;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.subscription.web.configuration.SubscriptionConfiguration;
import com.liferay.subscription.web.constants.SubscriptionConstants;

import java.io.IOException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;

/**
 * @author Alejandro Tardín
 */
public class UnsubscribeHooks {

	public UnsubscribeHooks(
		SubscriptionConfiguration configuration,
		TicketLocalService ticketLocalService,
		UserLocalService userLocalService,
		SubscriptionSender subscriptionSender) {

		_configuration = configuration;
		_ticketLocalService = ticketLocalService;
		_userLocalService = userLocalService;
		_subscriptionSender = subscriptionSender;
	}

	public void beforeSendNotificationToPersistedSubscriber(
		Subscription subscription) {

		if (_subscriptionSender.isBulk()) {
			return;
		}

		_userTicketMap.put(subscription.getUserId(), _getTicket(subscription));
	}

	public void processMailMessage(MailMessage mailMessage) {
		if (_subscriptionSender.isBulk()) {
			return;
		}

		InternetAddress[] toAddresses = mailMessage.getTo();

		if (toAddresses.length > 0) {
			String toAddress = toAddresses[0].getAddress();

			User user = _userLocalService.fetchUserByEmailAddress(
				_subscriptionSender.getCompanyId(), toAddress);

			if (user == null) {
				return;
			}

			Ticket ticket = _userTicketMap.get(user.getUserId());

			if (ticket != null) {
				try {
					String unsubscribeURL = _getUnsubscribeURL(user, ticket);

					_addUnsubscribeHeader(mailMessage, unsubscribeURL);
					_addUnsubscribeLink(mailMessage, unsubscribeURL);
				}
				catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}
				finally {
					_userTicketMap.remove(user.getUserId());
				}
			}
		}
	}

	private void _addUnsubscribeHeader(
		MailMessage mailMessage, String unsubscribeURL) {

		InternetHeaders internetHeaders = new InternetHeaders();

		internetHeaders.setHeader(
			"List-Unsubscribe", "<" + unsubscribeURL + ">");

		mailMessage.setInternetHeaders(internetHeaders);
	}

	private void _addUnsubscribeLink(
			MailMessage mailMessage, String unsubscribeURL)
		throws IOException {

		MailTemplateContextBuilder mailTemplateContextBuilder =
			MailTemplateFactoryUtil.createMailTemplateContextBuilder();

		mailTemplateContextBuilder.put("[$UNSUBSCRIBE_URL$]", unsubscribeURL);

		MailTemplateContext mailTemplateContext =
			mailTemplateContextBuilder.build();

		MailTemplate bodyMailTemplate =
			MailTemplateFactoryUtil.createMailTemplate(
				mailMessage.getBody(), true);

		String processedBody = bodyMailTemplate.renderAsString(
			Locale.US, mailTemplateContext);

		mailMessage.setBody(processedBody);
	}

	private Ticket _getTicket(Subscription subscription) {
		Calendar calendar = Calendar.getInstance();

		calendar.add(
			Calendar.DATE, _configuration.unsubscriptionTicketExpirationTime());

		List<Ticket> tickets = _ticketLocalService.getTickets(
			subscription.getCompanyId(), Subscription.class.getName(),
			subscription.getSubscriptionId(),
			SubscriptionConstants.TICKET_TYPE);

		Ticket ticket;

		if (ListUtil.isEmpty(tickets)) {
			ticket = _ticketLocalService.addTicket(
				subscription.getCompanyId(), Subscription.class.getName(),
				subscription.getSubscriptionId(),
				SubscriptionConstants.TICKET_TYPE, StringPool.BLANK,
				calendar.getTime(), _subscriptionSender.getServiceContext());
		}
		else {
			ticket = tickets.get(0);

			try {
				ticket = _ticketLocalService.updateTicket(
					ticket.getTicketId(), Subscription.class.getName(),
					subscription.getSubscriptionId(),
					SubscriptionConstants.TICKET_TYPE, StringPool.BLANK,
					calendar.getTime());
			}
			catch (PortalException pe) {
				throw new RuntimeException(pe);
			}
		}

		return ticket;
	}

	private String _getUnsubscribeURL(User user, Ticket ticket) {
		StringBuilder sb = new StringBuilder();

		sb.append(_subscriptionSender.getContextAttribute("[$PORTAL_URL$]"));
		sb.append(PortalUtil.getPathMain());
		sb.append("/portal/unsubscribe?key=");
		sb.append(ticket.getKey());
		sb.append("&userId=");
		sb.append(user.getUserId());

		return sb.toString();
	}

	private final SubscriptionConfiguration _configuration;
	private final SubscriptionSender _subscriptionSender;
	private final TicketLocalService _ticketLocalService;
	private final UserLocalService _userLocalService;
	private final Map<Long, Ticket> _userTicketMap = new HashMap<>();

}