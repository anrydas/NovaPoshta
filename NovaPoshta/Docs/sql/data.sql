insert into properties (prop_key, prop_value) values ('end_point', 'https://api.novaposhta.ua/v2.0/json/');
insert into properties (prop_key, prop_value) values ('phone_number', '');
insert into properties (prop_key, prop_value) values ('language', 'en');

insert into groups (id, name) values (1, 'Default');


-- TEST ONLY! ToDo: Remove IT in Prod!!!!
update properties set prop_value = '+380955580145' where prop_key = 'phone_number';
commit;
