-- password: Qwerty12
insert into OMNI_USERS (ID, LOGIN, PASSWORD, STATUS)
values (1, 'panokiv', '$2a$10$fPyFpDMo.J67l2Fq9D73tun/SN3LE1U1HXfDidwUL5QvoL6iGtMui', 1);

insert into USR (USR_KEY, USR_EMP_NO, USR_DISPLAY_NAME, USR_START_DATE, USR_END_DATE) values (1, '000-111', 'Panyok Igor', '2023-01-01', '2024-01-01');
insert into USR (USR_KEY, USR_EMP_NO, USR_DISPLAY_NAME, USR_START_DATE, USR_END_DATE) values (2, '000-222', 'Kholiavko Egor', '2023-01-01', '2024-01-01');

insert into ORC (ORC_KEY, USR_KEY) values (1, 1);
insert into ORC (ORC_KEY, USR_KEY) values (2, 2);

insert into UD_ADUSER (ID, ORC_KEY, UD_ADUSER_UID) values (1, 1, 'panokiv');
insert into UD_ADUSER (ID, ORC_KEY, UD_ADUSER_UID) values (2, 2, 'kholiavkoes');

insert into OIU (ID, ORC_KEY, OST_KEY) values (1, 1, 1);
insert into OIU (ID, ORC_KEY, OST_KEY) values (2, 2, 2);

insert into OST (OST_KEY, OST_STATUS) values (1, 'Active');
insert into OST (OST_KEY, OST_STATUS) values (2, 'Active');