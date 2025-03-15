CREATE TABLE properties (
    id INTEGER primary key AUTOINCREMENT,
    prop_key varchar not null,
    prop_value varchar not null,
    created timestamp default CURRENT_TIMESTAMP,
    updated timestamp default CURRENT_TIMESTAMP
);
CREATE INDEX properties_keys_idx ON properties (prop_key);

CREATE TABLE groups (
    id INTEGER primary key AUTOINCREMENT,
    name varchar not null,
    created timestamp default CURRENT_TIMESTAMP
);

insert into groups (id, name) values (1, 'Default');

CREATE TABLE numbers (
    id INTEGER primary key AUTOINCREMENT,
    number varchar(36),
    group_id integer not null default 1,
    app_status integer not null default 0,
    number_type integer not null default 0,
    comment varchar(127),
    dateCreated varchar(36),
    weight int,
    cost int,
    description varchar(36),
    cargoType varchar(36),
    status int,
    seatsAmount int,
    announcedPrice varchar(36),
    scheduledDeliveryDate varchar(36),
    recipientFullName varchar(36),
    cityRecipient varchar(36),
    warehouseRecipient varchar(36),
    warehouseRecipientNumber varchar(36),
    phoneRecipient varchar(36),
    recipientAddress varchar(36),
    citySender varchar(36),
    phoneSender varchar(36),
    warehouseSender varchar(36),
    senderAddress varchar(36),
    autoUpdated int(1) default 0,
    json_data text,
    created timestamp default CURRENT_TIMESTAMP,
    updated timestamp default CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(id)
);
CREATE INDEX numbers_number_idx ON numbers(number);
CREATE INDEX numbers_cityR_idx ON numbers(cityRecipient);
CREATE INDEX numbers_cityS_idx ON numbers(citySender);
CREATE INDEX numbers_phoneR_idx ON numbers(phoneRecipient);
CREATE INDEX numbers_phoneS_idx ON numbers(phoneSender);

CREATE TABLE extra_phones (
    id INTEGER primary key AUTOINCREMENT,
    phone varchar(20) not null,
    order_number integer not null
);
CREATE INDEX extra_phones_phone_idx ON extra_phones(phone);

CREATE TABLE number_to_phone (
    id INTEGER primary key AUTOINCREMENT,
    number_id INTEGER not null UNIQUE,
    phone_id INTEGER not null,
    CONSTRAINT number_id_fk FOREIGN KEY (number_id) REFERENCES numbers(id) ON DELETE CASCADE,
    CONSTRAINT phone_id_fk FOREIGN KEY (phone_id) REFERENCES extra_phones(id) ON DELETE CASCADE
);
CREATE INDEX number_to_phone_number_id_idx ON number_to_phone(number_id);
CREATE INDEX number_to_phone_phone_id_idx ON number_to_phone(phone_id);

CREATE TABLE search_params (
    id INTEGER primary key AUTOINCREMENT,
    name varchar(64) not null UNIQUE,
    options TEXT not null,
    order_number integer not null,
    created timestamp default CURRENT_TIMESTAMP,
    updated timestamp default CURRENT_TIMESTAMP
);
CREATE INDEX search_params_name_idx ON search_params(name);

CREATE TABLE search_history (
    id INTEGER primary key AUTOINCREMENT,
    search_text TEXT not null,
    created timestamp default CURRENT_TIMESTAMP
);

CREATE TABLE archive (
    id INTEGER not null,
    number varchar(36),
    group_id integer not null,
    app_status integer not null,
    number_type integer not null,
    comment varchar(127),
    dateCreated varchar(36),
    weight int,
    cost int,
    description varchar(36),
    cargoType varchar(36),
    status int,
    seatsAmount int,
    announcedPrice varchar(36),
    scheduledDeliveryDate varchar(36),
    recipientFullName varchar(36),
    cityRecipient varchar(36),
    warehouseRecipient varchar(36),
    warehouseRecipientNumber varchar(36),
    phoneRecipient varchar(36),
    recipientAddress varchar(36),
    citySender varchar(36),
    phoneSender varchar(36),
    warehouseSender varchar(36),
    senderAddress varchar(36),
    json_data text,
    created timestamp,
    updated timestamp
);
CREATE INDEX archive_number_idx ON archive(number);