CREATE table if not exists CMVFS (
	CMFNAM varchar (255),
	CMDTYP integer ,
	CMMODD bigint,
	CMWHOM varchar (50) NULL,
	CMDATA LONGVARCHAR NULL,
	PRIMARY KEY (CMFNAM)
);

CREATE table if not exists CMCHAB (
CMUSERID varchar (50),
CMABID varchar (50),
CMABPF integer NULL,
CMABTX LONGVARCHAR NULL,
PRIMARY KEY (CMUSERID, CMABID)
);

CREATE table if not exists CMSTAT (
	CMSTRT bigint,
	CMENDT bigint,
	CMDATA LONGVARCHAR NULL,
PRIMARY KEY (CMSTRT)
);
	
CREATE table if not exists CMPOLL (
CMNAME varchar (100) ,
CMBYNM varchar (100) NULL ,
CMSUBJ varchar (255) NULL ,
CMDESC LONGVARCHAR NULL ,
CMOPTN LONGVARCHAR NULL ,
CMFLAG integer NULL ,
CMQUAL varchar (255) NULL ,
CMRESL LONGVARCHAR NULL,
CMEXPI bigint NULL,
PRIMARY KEY (CMNAME)
);

CREATE table if not exists CMCHAR (
CMUSERID varchar (50),
CMPASS varchar (50) NULL,
CMCLAS varchar (200) NULL,
CMSTRE integer NULL,
CMRACE varchar (50) NULL,
CMDEXT integer NULL,
CMCONS integer NULL,
CMGEND varchar (50) NULL,
CMWISD integer NULL,
CMINTE integer NULL,
CMCHAR integer NULL,
CMHITP integer NULL,
CMLEVL varchar (50) NULL,
CMMANA integer NULL,
CMMOVE integer NULL,
CMDESC varchar (255) NULL,
CMALIG integer NULL,
CMEXPE integer NULL,
CMEXLV integer NULL,
CMWORS varchar (50) NULL,
CMPRAC integer NULL,
CMTRAI integer NULL,
CMAGEH integer NULL,
CMGOLD integer NULL,
CMWIMP integer NULL,
CMQUES integer NULL,
CMROID varchar (100) NULL,
CMDATE varchar (50) NULL,
CMCHAN integer NULL,
CMATTA integer NULL,
CMAMOR integer NULL,
CMDAMG integer NULL,
CMBTMP integer NULL,
CMLEIG varchar (50) NULL,
CMHEIT integer NULL,
CMWEIT integer NULL,
CMPRPT varchar (250) NULL,
CMCOLR varchar (100) NULL,
CMLSIP varchar (100) NULL,
CMEMAL varchar (255),
CMPFIL LONGVARCHAR NULL,
CMSAVE varchar (150) NULL,
CMMXML LONGVARCHAR NULL,
PRIMARY KEY (CMUSERID)
);

CREATE table if not exists CMCHFO (
CMUSERID varchar (50),
CMFONM integer,
CMFOID varchar (50) NULL,
CMFOTX LONGVARCHAR  NULL,
CMFOLV integer NULL,
CMFOAB integer NULL,
PRIMARY KEY (CMUSERID, CMFONM)
);

CREATE table if not exists CMCHCL (
CMUSERID varchar (50),
CMCLAN varchar (100),
CMCLRO integer NULL,
PRIMARY KEY(CMUSERID, CMCLAN)
);

CREATE table if not exists CMCHIT (
CMUSERID varchar (50),
CMITNM varchar (100),
CMITID varchar (50) NULL,
CMITTX LONGVARCHAR NULL,
CMITLO varchar (100) NULL,
CMITWO bigint NULL,
CMITUR integer NULL,
CMITLV integer NULL,
CMITAB integer NULL,
CMHEIT integer NULL,
PRIMARY KEY (CMUSERID, CMITNM)
);

CREATE table if not exists CMROCH (
CMROID varchar (50),
CMCHNM varchar (100),
CMCHID varchar (50) NULL,
CMCHTX LONGVARCHAR  NULL,
CMCHLV integer NULL,
CMCHAB integer NULL,
CMCHRE integer NULL,
CMCHRI varchar (100),
PRIMARY KEY (CMROID, CMCHNM)
);

CREATE table if not exists CMROEX (
CMROID varchar (50),
CMDIRE integer,
CMEXID varchar (50) NULL,
CMEXTX LONGVARCHAR  NULL,
CMNRID varchar (50) NULL,
PRIMARY KEY (CMROID, CMDIRE)
);

CREATE table if not exists CMROIT (
CMROID varchar (50),
CMITNM varchar (100),
CMITID varchar (50) NULL,
CMITLO varchar (100) NULL,
CMITTX LONGVARCHAR NULL,
CMITRE integer NULL,
CMITUR integer NULL,
CMITLV integer NULL,
CMITAB integer NULL,
CMHEIT integer NULL,
PRIMARY KEY (CMROID, CMITNM)
);

CREATE table if not exists CMROOM (
CMROID varchar (50),
CMLOID varchar (50) NULL,
CMAREA varchar (50) NULL,
CMDESC1 varchar (255) NULL,
CMDESC2 LONGVARCHAR NULL,
CMROTX LONGVARCHAR NULL,
PRIMARY KEY (CMROID)
);

CREATE table if not exists CMQUESTS (
CMQUESID varchar (50),
CMQUTYPE varchar (50) NULL,
CMQFLAGS integer NULL ,
CMQSCRPT LONGVARCHAR NULL,
CMQWINNS LONGVARCHAR NULL,
PRIMARY KEY (CMQUESID)
);

CREATE table if not exists CMAREA (
CMAREA varchar (50) ,
CMTYPE varchar (50) ,
CMCLIM integer NULL ,
CMSUBS varchar (100) NULL ,
CMDESC LONGVARCHAR NULL ,
CMROTX LONGVARCHAR NULL , 
CMTECH integer NULL ,
PRIMARY KEY (CMAREA)
);

CREATE table if not exists CMJRNL (
	CMJKEY varchar (75) ,
	CMJRNL varchar (50) NULL ,
	CMFROM varchar (50) NULL ,
	CMDATE varchar (50) NULL ,
	CMTONM varchar (50) NULL ,
	CMSUBJ varchar (255) NULL ,
	CMPART varchar (75) NULL ,
	CMATTR integer NULL,
	CMDATA varchar (255) NULL ,
	CMUPTM bigint NULL,
	CMIMGP varchar (50) NULL,
	CMVIEW integer NULL,
	CMREPL integer NULL,
	CMMSGT LONGVARCHAR NULL,
PRIMARY KEY (CMJKEY) 
);

CREATE INDEX if not exists CMJRNLNAME on CMJRNL (CMJRNL);
CREATE INDEX if not exists CMJRNLCMPART on CMJRNL (CMPART);
CREATE INDEX if not exists CMJRNLCMTONM on CMJRNL (CMTONM);
CREATE INDEX if not exists CMJRNLCMUPTM on CMJRNL (CMUPTM);

CREATE table if not exists CMCLAN (
	CMCLID varchar (100) ,
	CMTYPE integer ,
	CMDESC LONGVARCHAR NULL ,
	CMACPT varchar (255) NULL ,
	CMPOLI LONGVARCHAR NULL ,
	CMRCLL varchar (50) NULL ,
	CMDNAT varchar (50) NULL ,
	CMSTAT integer NULL ,
	CMMORG varchar (50) NULL ,
	CMTROP integer NULL ,
	PRIMARY KEY (CMCLID) 
);

CREATE table if not exists CMPDAT (
	CMPLID varchar (100) ,
	CMSECT varchar (100) ,
	CMPKEY varchar (100) ,
	CMPDAT LONGVARCHAR NULL ,
	PRIMARY KEY (CMPLID,CMSECT,CMPKEY)
);

CREATE table if not exists CMGRAC (
	CMRCID varchar (50) ,
	CMRDAT LONGVARCHAR NULL ,
	PRIMARY KEY (CMRCID)
);

CREATE table if not exists CMCCAC (
	CMCCID varchar (50) ,
	CMCDAT LONGVARCHAR NULL ,
	PRIMARY KEY (CMCCID)
);

CREATE table if not exists CMGAAC (
	CMGAID varchar (50) ,
	CMGAAT LONGVARCHAR NULL , 
	CMGACL varchar (50) NULL ,
	PRIMARY KEY (CMGAID)
);

CREATE table if not exists CMACCT (
	CMANAM varchar (50) ,
	CMPASS varchar (50) ,
	CMCHRS LONGVARCHAR NULL ,
	CMAXML LONGVARCHAR NULL ,
	PRIMARY KEY (CMANAM)
);