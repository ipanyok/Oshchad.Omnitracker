CREATE TABLE OMNI_USERS (
    ID NUMBER(19,0) primary key,
	LOGIN VARCHAR(150 CHARACTERS),
	PASSWORD VARCHAR(150 CHARACTERS),
	STATUS NUMBER(1) DEFAULT 0
);

CREATE TABLE USR (
    USR_KEY NUMBER(19,0) primary key,
    USR_EMP_NO VARCHAR(100 CHARACTERS),
    USR_UDF_OBJECTID VARCHAR(100 CHARACTERS),
	USR_UDF_MAINBRANCH VARCHAR(250 CHARACTERS),
	USR_UDF_TEMPBRANCH VARCHAR(250 CHARACTERS),
	USR_UDF_REBRANCHINGSTARTDATE DATE,
	USR_UDF_REBRANCHINGENDDATE DATE
);

CREATE TABLE OMNI_REQUEST (
    ID BIGINT auto_increment,
    OBJECT_ID VARCHAR(100 CHARACTERS) UNIQUE,
    EMP_NO VARCHAR(100 CHARACTERS),
	MAINBRANCH VARCHAR(250 CHARACTERS),
	TEMPBRANCH VARCHAR(250 CHARACTERS),
	REBRANCHINGSTARTDATE DATE,
	REBRANCHINGENDDATE DATE,
	CHANGED_AT TIMESTAMP,
	IS_SAVED NUMBER(1) DEFAULT 0,
	IS_PROCESSED NUMBER(1) DEFAULT 0,
	IS_PICKUP_SENT NUMBER(1) DEFAULT 0,
	IS_CLOSURE_SENT NUMBER(1) DEFAULT 0
);





