create table BlogsEntry (
	uuid_ VARCHAR(75) null,
	entryId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	title VARCHAR(150) null,
	subtitle STRING null,
	urlTitle VARCHAR(150) null,
	description STRING null,
	content TEXT null,
	displayDate DATE null,
	allowPingbacks BOOLEAN,
	allowTrackbacks BOOLEAN,
	trackbacks TEXT null,
	coverImageCaption STRING null,
	coverImageFileEntryId LONG,
	coverImageURL STRING null,
	smallImage BOOLEAN,
	smallImageFileEntryId LONG,
	smallImageId LONG,
	smallImageURL STRING null,
	lastPublishDate DATE null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table BlogsStatsUser (
	statsUserId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	entryCount INTEGER,
	lastPostDate DATE null,
	ratingsTotalEntries INTEGER,
	ratingsTotalScore DOUBLE,
	ratingsAverageScore DOUBLE
);